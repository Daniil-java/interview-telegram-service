package com.kuklin.interview_telegram_service.telegram.handlers.interview;

import com.kuklin.interview_telegram_service.entities.ChatMessage;
import com.kuklin.interview_telegram_service.entities.TelegramUser;
import com.kuklin.interview_telegram_service.entities.UserEntity;
import com.kuklin.interview_telegram_service.models.MessageRequestDto;
import com.kuklin.interview_telegram_service.services.ChatMessageService;
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
    private static final String ERROR_MESSAGE = "Ошибка! Попробуйте продолжить собеседование позже";
    @Override
    public void handle(Update update, UserEntity userEntity) {
        Message requestMessage = update.getMessage();
        Long chatId = requestMessage.getChatId();
        String response;

        TelegramUser telegramUser = telegramUserService.getTelegramUserByIdOrNull(userEntity.getTelegramId());

        try {
            ChatMessage chatMessage = chatMessageService.processUserMessageOrGetNull(
                    MessageRequestDto.getDefault(update.getMessage().getText(), telegramUser.getActualAiConversationId()), userEntity);
            response = chatMessage.getContent();
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
