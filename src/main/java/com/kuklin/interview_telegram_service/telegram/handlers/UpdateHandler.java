package com.kuklin.interview_telegram_service.telegram.handlers;

import com.kuklin.interview_telegram_service.entities.UserEntity;
import com.kuklin.interview_telegram_service.telegram.TelegramFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface UpdateHandler {
    void handle(Update update, UserEntity userEntity);

    String getHandlerListName();

    @Autowired
    default void registerMyself(TelegramFacade messageFacade) {
        messageFacade.register(getHandlerListName(), this);
    }

}
