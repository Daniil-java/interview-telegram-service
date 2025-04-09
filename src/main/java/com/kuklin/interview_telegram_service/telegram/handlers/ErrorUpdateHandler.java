package com.kuklin.interview_telegram_service.telegram.handlers;

import com.kuklin.interview_telegram_service.entities.UserEntity;
import com.kuklin.interview_telegram_service.services.TelegramService;
import com.kuklin.interview_telegram_service.telegram.utils.Command;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

//Обработчик непродусмотренных сообщений
@Component
@RequiredArgsConstructor
public class ErrorUpdateHandler implements UpdateHandler {
    private static final String RESPONSE = "Данный запрос непредусмотрен сервисом.";
    private final TelegramService telegramService;
    @Override
    public void handle(Update update, UserEntity userEntity) {
        telegramService.sendReturnedMessage(
                update.getMessage().getChatId(),
                RESPONSE
        );
    }

    @Override
    public String getHandlerListName() {
        return Command.ERROR.getCommandText();
    }
}
