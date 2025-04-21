package com.kuklin.interview_telegram_service.telegram.handlers.interview;

import com.kuklin.interview_telegram_service.entities.ChatMessage;
import com.kuklin.interview_telegram_service.entities.TelegramUser;
import com.kuklin.interview_telegram_service.entities.UserEntity;
import com.kuklin.interview_telegram_service.models.MessageRequestDto;
import com.kuklin.interview_telegram_service.services.*;
import com.kuklin.interview_telegram_service.telegram.handlers.UpdateHandler;
import com.kuklin.interview_telegram_service.telegram.utils.BotState;
import com.kuklin.interview_telegram_service.telegram.utils.Command;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class StartInterviewUpdateHandler implements UpdateHandler {
    private final TelegramService telegramService;
    private final TelegramUserService telegramUserService;
    private final ChatMessageService chatMessageService;
    private final ConversationService conversationService;
    private static final String JOB_ERROR_MESSAGE = "Произошла ошибка! Вы должны указать должность при помощи команды: /job";
    private static final String ERROR_MESSAGE = "Произошла ошибка! Попробуйте обратиться позже!";

    private static final String AI_REQUEST_MESSAGE = "Ты проводишь собеседование на должность: %s\n" +
            "Начинай сразу с приветствия и первого вопроса. " +
            "Вопросы задавай по одному, после ответа переходи к следующему вопросу." +
            "\nНе обсуждай с пользователем сторонние темы. Не дай ему отойти от темы собеседования." +
            "Ты не ИИ, ты профессиональый ревьюер с опытом в соответствующей сфере ." +
            "Дополнительные условия следующие: %s";

    @Override
    public void handle(Update update, UserEntity userEntity) {
        Message requestMessage = update.getMessage();
        Long chatId = requestMessage.getChatId();
        String response;

        if (userEntity.getJobTitle() == null || userEntity.getJobTitle().isEmpty()) {
            telegramService.sendReturnedMessage(chatId, JOB_ERROR_MESSAGE);
            return;
        }
        String aiRequestMessage = String.format(AI_REQUEST_MESSAGE,
                userEntity.getJobTitle(), userEntity.getProperties());

        TelegramUser telegramUser = telegramUserService.setBotStateByIdOrGetNull(
                userEntity.getTelegramId(), BotState.INTERVIEW);

        if (telegramUser == null) {
            telegramService.sendReturnedMessage(chatId, ERROR_MESSAGE);
            return;
        }

        Long conversationId = conversationService.getNewConversation(userEntity).getId();

        try {
            ChatMessage chatMessage = chatMessageService.processUserMessageOrGetNull(
                    MessageRequestDto.getDefault(aiRequestMessage, conversationId)
                    , userEntity);
            response = chatMessage.getContent();
        } catch (Exception e) {
            response = ERROR_MESSAGE;
        }

        telegramUserService.setActualConversationIdOrGetNull(
                telegramUser.getTelegramId(), conversationId);

        telegramService.sendReturnedMessage(chatId, response);

    }

    @Override
    public String getHandlerListName() {
        return Command.START_INTERVIEW.getCommandText();
    }
}
