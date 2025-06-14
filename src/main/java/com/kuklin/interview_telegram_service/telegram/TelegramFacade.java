package com.kuklin.interview_telegram_service.telegram;

import com.kuklin.interview_telegram_service.entities.TelegramUser;
import com.kuklin.interview_telegram_service.entities.UserEntity;
import com.kuklin.interview_telegram_service.services.TelegramUserService;
import com.kuklin.interview_telegram_service.services.UserService;
import com.kuklin.interview_telegram_service.telegram.handlers.UpdateHandler;
import com.kuklin.interview_telegram_service.telegram.utils.BotState;
import com.kuklin.interview_telegram_service.telegram.utils.Command;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class TelegramFacade {
    @Autowired
    private UserService userService;
    @Autowired
    private TelegramUserService telegramUserService;
    private Map<String, UpdateHandler> updateHandlerMap = new ConcurrentHashMap<>();

    public void register(String command, UpdateHandler updateHandler) {
        if (updateHandlerMap.containsKey(command)) {
            log.error("This command is already exists!");
        }
        updateHandlerMap.put(command, updateHandler);
    }

    public void handleUpdate(Update update) {
        /*
         На этапе разработки обработка сообщений
         происходит только от одного пользователя
         */
        if (!update.hasCallbackQuery() && !update.hasMessage()) return;
        User user = update.getMessage() != null ?
                update.getMessage().getFrom() :
                update.getCallbackQuery().getFrom();
        if (!List.of(425120436L, 420478432L).contains(user.getId())) return;

        UserEntity userEntity = userService.getOrCreateUser(user);

        processInputUpdate(update, userEntity).handle(update, userEntity);
    }

    public UpdateHandler processInputUpdate(Update update, UserEntity userEntity) {
        String request;
        TelegramUser telegramUser = telegramUserService.getTelegramUserByIdOrNull(userEntity.getTelegramId());
        if (update.hasCallbackQuery()) {
            request = update.getCallbackQuery().getData();
        } else {
            request = update.getMessage().getText();
        }


        UpdateHandler currentUpdateHandler = null;
        if (request != null) {
            currentUpdateHandler = updateHandlerMap.get(request.split(TelegramBot.DELIMITER)[0]);
        }
        if (currentUpdateHandler != null) {
            return currentUpdateHandler;
        } else if (telegramUser.getBotState() == BotState.WAIT) {
            if (isValidatedUrl(request)) {
                return updateHandlerMap.get(Command.URL_PROCESS.getCommandText());
            } else {
                return updateHandlerMap.get(Command.ERROR.getCommandText());
            }
        } else {
            return updateHandlerMap.get(Command.INTERVIEW.getCommandText());
        }

    }

    private boolean isValidatedUrl(String url) {
        return url.startsWith("https://hh.ru/vacancy") ||
                url.startsWith("hh.ru/vacancy")
                ;
    }
}
