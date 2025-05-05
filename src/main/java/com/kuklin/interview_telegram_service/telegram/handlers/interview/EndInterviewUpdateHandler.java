package com.kuklin.interview_telegram_service.telegram.handlers.interview;

import com.kuklin.interview_telegram_service.entities.ChatMessage;
import com.kuklin.interview_telegram_service.entities.TelegramUser;
import com.kuklin.interview_telegram_service.entities.UserEntity;
import com.kuklin.interview_telegram_service.models.MessageRequestDto;
import com.kuklin.interview_telegram_service.services.ChatMessageService;
import com.kuklin.interview_telegram_service.services.InterviewService;
import com.kuklin.interview_telegram_service.services.TelegramService;
import com.kuklin.interview_telegram_service.services.TelegramUserService;
import com.kuklin.interview_telegram_service.telegram.handlers.UpdateHandler;
import com.kuklin.interview_telegram_service.telegram.utils.BotState;
import com.kuklin.interview_telegram_service.telegram.utils.Command;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class EndInterviewUpdateHandler implements UpdateHandler {
    private final TelegramService telegramService;
    private final TelegramUserService telegramUserService;
    private final ChatMessageService chatMessageService;
    private final InterviewService interviewService;
    private static final String ERROR_MESSAGE = "Произошла ошибка! Не получилось закончить собеседование!";
    private static final String AI_REQUEST_MESSAGE = "Собеседование закончено. " +
            "Проанализируй ответы. Расскажи об ошибках. " +
            "Сделай выводы. Скажи какие темы стоит подтянуть";
    @Override
    public void handle(Update update, UserEntity userEntity) {
        Message requestMessage = update.getMessage();
        Long chatId = requestMessage.getChatId();

        TelegramUser telegramUser = telegramUserService.setBotStateByIdOrGetNull(
                userEntity.getTelegramId(), BotState.WAIT);

        if (telegramUser == null) {
            telegramService.sendReturnedMessage(chatId, ERROR_MESSAGE);
            return;
        }

        Long conversationId = telegramUser.getActualAiConversationId();
        String response;
        try {
            ChatMessage chatMessage = chatMessageService.processUserMessageOrGetNull(
                    MessageRequestDto.getDefault(AI_REQUEST_MESSAGE, conversationId), userEntity);
            response = chatMessage.getContent();
        } catch (Exception e) {
            response = ERROR_MESSAGE;
        }

        telegramService.sendReturnedMessage(chatId, response);
        interviewService.saveResult(userEntity, conversationId, response);
    }

    @Override
    public String getHandlerListName() {
        return Command.END.getCommandText();
    }
}
