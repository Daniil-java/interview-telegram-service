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
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
@Component
@RequiredArgsConstructor
public class InterviewUpdateHandler implements UpdateHandler {
    private final TelegramService telegramService;
    private final TelegramUserService telegramUserService;
    private final ChatMessageService chatMessageService;
    private final OpenAiIntegrationService openAiIntegrationService;
    private static final String ERROR_MESSAGE = "Ошибка! Попробуйте продолжить собеседование позже";
    private static final String VOICE_ERROR_MESSAGE = "Ошибка! Не получилось обработать голосовое сообщение";

    @Override
    public void handle(Update update, UserEntity userEntity) {
        Message requestMessage = update.getMessage();
        Long chatId = requestMessage.getChatId();

        TelegramUser telegramUser = telegramUserService.getTelegramUserByIdOrNull(userEntity.getTelegramId());
        String request = requestMessage.getText();

        if (requestMessage.hasVoice()) {
            String fileId = requestMessage.getVoice().getFileId();
            byte[] inputAudioFile = telegramService.downloadFileOrNull(fileId);
            if (inputAudioFile == null) {
                telegramService.sendReturnedMessage(chatId, VOICE_ERROR_MESSAGE);
                return;
            }
            request = openAiIntegrationService.fetchAudioResponse(inputAudioFile);
        }

        String response;
        try {
            ChatMessage chatMessage = chatMessageService.processUserMessageOrGetNull(
                    MessageRequestDto.getDefault(request, telegramUser.getActualAiConversationId()),
                    userEntity);
            response = chatMessage.getContent();

            if (requestMessage.hasVoice()) {
                byte[] outputAudioFile = openAiIntegrationService.makeSpeech(response);
                String filename = chatId + requestMessage.getMessageId() + "";
                telegramService.sendVoiceMessage(chatId, outputAudioFile, filename);
            }
        } catch (Exception e) {
            response = ERROR_MESSAGE;
        }

        telegramService.sendReturnedMessage(chatId, response);
    }

    @Override
    public String getHandlerListName() {
        return Command.INTERVIEW.getCommandText();
    }
}
