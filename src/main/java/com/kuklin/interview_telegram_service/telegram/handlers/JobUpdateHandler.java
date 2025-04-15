package com.kuklin.interview_telegram_service.telegram.handlers;

import com.kuklin.interview_telegram_service.entities.UserEntity;
import com.kuklin.interview_telegram_service.exceptions.ErrorResponseException;
import com.kuklin.interview_telegram_service.services.*;
import com.kuklin.interview_telegram_service.telegram.TelegramBot;
import com.kuklin.interview_telegram_service.telegram.utils.Command;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class JobUpdateHandler implements UpdateHandler {

    private final TelegramService telegramService;
    private final UserService userService;
    private final ChatMessageService chatMessageService;
    private final ConversationService conversationService;
    private static final String JOB_ERROR_MESSAGE = "Произошла ошибка! Введите существующую должность.";

    private static final String ERROR_MESSAGE = "Произошла ошибка! Не получилось сохранить новую должность:";
    private static final String SUCCESS_MESSAGE = "Новая должность сохранена: ";

    private static final String AI_REQUEST_MESSAGE = "Ты должен ответить на вопрос " +
            "является ли следующая строка должна являться должностью, " +
            "на которую можно провести собеседование. Ответ должен быть однозначным," +
            " не пиши ничего лишнего, только 1 - если является, и 0 - если нет;\n" +
            "Строка: %s";

    @Override
    public void handle(Update update, UserEntity userEntity) {
        Message requestMessage = update.getMessage();
        Long chatId = requestMessage.getChatId();
        String response;

        String[] splitted = requestMessage.getText().split(TelegramBot.DELIMITER);
        String jobTittle = splitted[1];

        try {
            response = chatMessageService.sendDaemonMessage(String.format(AI_REQUEST_MESSAGE, jobTittle));
        } catch (ErrorResponseException e) {
            telegramService.sendReturnedMessage(chatId, e.getErrorStatus().getMessage());
            return;
        }

        if (response.equals("0")) {
            telegramService.sendReturnedMessage(chatId, JOB_ERROR_MESSAGE);
            return;
        }

        userEntity = userService.setJobTittleOrGetNull(userEntity, jobTittle);

        if (userEntity == null) {
            telegramService.sendReturnedMessage(chatId, ERROR_MESSAGE);
        } else {
            telegramService.sendReturnedMessage(chatId, SUCCESS_MESSAGE + userEntity.getJobTitle());
        }
    }

    @Override
    public String getHandlerListName() {
        return Command.JOB.getCommandText();
    }
}
