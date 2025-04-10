package com.kuklin.interview_telegram_service.telegram.handlers;

import com.kuklin.interview_telegram_service.entities.UserEntity;
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
public class IfUpdateHandler implements UpdateHandler {
    private final UserService userService;
    private final TelegramService telegramService;
    private static final String ERROR_MESSAGE = "Произошла ошибка! Не получилось задать новые условия!";
    private static final String SUCCESS_MESSAGE = "Новые условия сохранены: ";
    @Override
    public void handle(Update update, UserEntity userEntity) {

        Message requestMessage = update.getMessage();
        Long chatId = requestMessage.getChatId();

        String[] splitted = requestMessage.getText().split(TelegramBot.DELIMITER);
        String properties = splitted[1];

        userEntity = userService.setPropertiesOrGetNull(userEntity, properties);

        if (userEntity == null) {
            telegramService.sendReturnedMessage(chatId, ERROR_MESSAGE);
        } else {
            telegramService.sendReturnedMessage(
                    chatId, SUCCESS_MESSAGE + userEntity.getProperties());
        }
    }

    @Override
    public String getHandlerListName() {
        return Command.IF.getCommandText();
    }
}
