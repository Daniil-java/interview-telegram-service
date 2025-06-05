package com.kuklin.interview_telegram_service.telegram.handlers.interview;

import com.kuklin.interview_telegram_service.entities.ChatMessage;
import com.kuklin.interview_telegram_service.entities.TelegramUser;
import com.kuklin.interview_telegram_service.entities.UserEntity;
import com.kuklin.interview_telegram_service.models.MessageRequestDto;
import com.kuklin.interview_telegram_service.services.ChatMessageService;
import com.kuklin.interview_telegram_service.services.OpenAiIntegrationService;
import com.kuklin.interview_telegram_service.services.TelegramService;
import com.kuklin.interview_telegram_service.services.TelegramUserService;
import com.kuklin.interview_telegram_service.telegram.handlers.UpdateHandler;
import com.kuklin.interview_telegram_service.telegram.utils.Command;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@RequiredArgsConstructor
@Slf4j
public class InterviewUpdateHandler implements UpdateHandler {
    private final TelegramService telegramService;
    private final TelegramUserService telegramUserService;
    private final ChatMessageService chatMessageService;
    private final OpenAiIntegrationService openAiIntegrationService;
    private static final String ERROR_MESSAGE = "Ошибка! Попробуйте продолжить собеседование позже";
    private static final String VOICE_ERROR_MESSAGE = "Ошибка! Не получилось обработать голосовое сообщение";
    private static final String VOICE_ERROR_RESPONSE_MESSAGE = "Ошибка! Не получилось отправить голосовое сообщение";

    @Override
    public void handle(Update update, UserEntity userEntity) {
        Message requestMessage = update.getMessage();
        Long chatId = requestMessage.getChatId();

        TelegramUser telegramUser = telegramUserService.getTelegramUserByIdOrNull(userEntity.getTelegramId());
        String request = requestMessage.getText();

        if (requestMessage.hasVoice()) {
            request = processVoidMessage(requestMessage);
            if (request == null) {
                telegramService.sendReturnedMessage(chatId, VOICE_ERROR_MESSAGE);
            }
        }

        String response = getResponseMessage(requestMessage, telegramUser, userEntity, request);
        telegramService.sendReturnedMessage(chatId, response);
    }

    private String processVoidMessage(Message message) {
        log.info("Скачивание аудиофайла с телеграмма...");
        String fileId = message.getVoice().getFileId();
        byte[] inputAudioFile = telegramService.downloadFileOrNull(fileId);
        if (inputAudioFile == null) {
            log.info("Аудиофайла не существует.");
            return null;
        }
        return openAiIntegrationService.fetchAudioResponse(inputAudioFile);
    }

    private String getResponseMessage(Message requestMessage, TelegramUser telegramUser,
                                      UserEntity userEntity, String request) {
        String response;
        try {
            ChatMessage chatMessage = chatMessageService.processUserMessageOrGetNull(
                    MessageRequestDto.getDefault(request, telegramUser.getActualAiConversationId()),
                    userEntity);
            response = chatMessage.getContent();

            if (requestMessage.hasVoice()) {
                log.info("Попытка создания аудиоответа...");
                byte[] outputAudioFile = openAiIntegrationService.makeSpeech(response);
                String filename = requestMessage.getChatId() + requestMessage.getMessageId() + "";
                log.info("Попытка отправки аудиособщения в телеграмм...");
                telegramService.sendVoiceMessage(requestMessage.getChatId(), outputAudioFile, filename);
            }
        } catch (TelegramApiException e) {
            log.error("Ошибка при попытке отправить голосовое собщение в телеграмм!", e);
            return VOICE_ERROR_RESPONSE_MESSAGE;
        } catch (Exception e) {
            log.error("Ошибка при попытке создать аудиоответ или связаться с ИИ!", e);
            return ERROR_MESSAGE;
        }
        return response;
    }

    @Override
    public String getHandlerListName() {
        return Command.INTERVIEW.getCommandText();
    }
}
