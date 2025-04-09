package com.kuklin.interview_telegram_service.telegram.handlers.interview;

import com.kuklin.interview_telegram_service.entities.TelegramUser;
import com.kuklin.interview_telegram_service.entities.UserEntity;
import com.kuklin.interview_telegram_service.models.MessageResponseDto;
import com.kuklin.interview_telegram_service.services.OpenAiIntegrationService;
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
public class StartInterviewUpdateHandler implements UpdateHandler {
    private final TelegramService telegramService;
    private final TelegramUserService telegramUserService;
    private final OpenAiIntegrationService openAiIntegrationService;
    private static final String JOB_ERROR_MESSAGE = "Произошла ошибка! Вы должны указать должность при помощи команды: /job";
    private static final String ERROR_MESSAGE = "Произошла ошибка! Пользователь не найден!";

    @Override
    public void handle(Update update, UserEntity userEntity) {
        String aiRequestMessage = String.format(
                "Ты проводишь собеседование на должность: %s\n" +
                        "Начинай сразу с приветствия и первого вопроса. " +
                        "Вопросы задавай по одному, после ответа переходи к следующему вопросу." +
                        "\nНе обсуждай с пользователем сторонние темы. Не дай ему отойти от темы собеседования." +
                        "Ты не ИИ, ты профессиональый ревьюер с опытом разработчика." +
                        "Дополнительные условия следующие:"
        , userEntity.getJobTittle(), userEntity.getProperties());

        Message requestMessage = update.getMessage();
        Long chatId = requestMessage.getChatId();

        if (userEntity.getJobTittle() == null || userEntity.getJobTittle().isEmpty()) {
            telegramService.sendReturnedMessage(chatId, JOB_ERROR_MESSAGE);
            return;
        }

        TelegramUser telegramUser = telegramUserService.setBotStateByIdOrGetNull(
                userEntity.getTelegramUserId(), BotState.INTERVIEW);

        if (telegramUser == null) {
            telegramService.sendReturnedMessage(chatId, ERROR_MESSAGE);
            return;
        }

        Long conversationId = openAiIntegrationService.getNewConversationId();
        MessageResponseDto response =
                openAiIntegrationService.sendMessage(aiRequestMessage, conversationId);

        telegramUserService.setActualConversationIdOrGetNull(
                telegramUser.getTelegramId(), conversationId);

        telegramService.sendReturnedMessage(chatId, response.getContent());

    }

    @Override
    public String getHandlerListName() {
        return Command.START_INTERVIEW.getCommandText();
    }
}
