package com.kuklin.interview_telegram_service.telegram.handlers;

import com.kuklin.interview_telegram_service.entities.UserEntity;
import com.kuklin.interview_telegram_service.models.MessageResponseDto;
import com.kuklin.interview_telegram_service.services.OpenAiIntegrationService;
import com.kuklin.interview_telegram_service.services.TelegramService;
import com.kuklin.interview_telegram_service.services.UserService;
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
    private final OpenAiIntegrationService openAiIntegrationService;
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

        String[] splitted = requestMessage.getText().split(TelegramBot.DELIMITER);
        String jobTittle = splitted[1];

        Long conversationId = openAiIntegrationService.getNewConversationId();
        MessageResponseDto response =
                openAiIntegrationService.sendMessage(String.format(AI_REQUEST_MESSAGE, jobTittle), conversationId);

        if (response.getContent().equals("0")) {
            telegramService.sendReturnedMessage(chatId, JOB_ERROR_MESSAGE);
            return;
        }

        userEntity = userService.setJobTittleOrGetNull(userEntity, jobTittle);

        if (userEntity == null) {
            telegramService.sendReturnedMessage(chatId, ERROR_MESSAGE);
        } else {
            telegramService.sendReturnedMessage(chatId, SUCCESS_MESSAGE + userEntity.getJobTittle());
        }
    }

    @Override
    public String getHandlerListName() {
        return Command.JOB.getCommandText();
    }
}
