package com.kuklin.interview_telegram_service.telegram;

import com.kuklin.interview_telegram_service.entities.TelegramUser;
import com.kuklin.interview_telegram_service.entities.UserEntity;
import com.kuklin.interview_telegram_service.services.TelegramUserService;
import com.kuklin.interview_telegram_service.services.UserService;
import com.kuklin.interview_telegram_service.telegram.handlers.UpdateHandler;
import com.kuklin.interview_telegram_service.telegram.utils.BotState;
import com.kuklin.interview_telegram_service.telegram.utils.Command;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class TelegramFacade {
    @Autowired
    private UserService userService;
    @Autowired
    private TelegramUserService telegramUserService;
    private Map<String, UpdateHandler> map = new ConcurrentHashMap<>();

    public void register(String command, UpdateHandler updateHandler) {
        if (map.containsKey(command)) {
            log.error("This command is already exists!");
        }
        map.put(command, updateHandler);
    }

    public void handleUpdate(Update update) {
        /*
         На этапе разработки обработка сообщений
         происходит только от одного пользователя
         */
        User user = update.getMessage() != null ?
                update.getMessage().getFrom() :
                update.getCallbackQuery().getFrom();
        if (user.getId() != 425120436L) return;

        UserEntity userEntity = userService.getOrCreateUser(user);
        processInputUpdate(update, userEntity).handle(update, userEntity);
    }

    public UpdateHandler processInputUpdate(Update update, UserEntity userEntity) {
        String request;
        TelegramUser telegramUser = telegramUserService.getTelegramUserByIdOrNull(userEntity.getTelegramUserId());
        if (update.hasCallbackQuery()) {
            request = update.getCallbackQuery().getData();
        } else {
            request = update.getMessage().getText();
        }
        UpdateHandler currentUpdateHandler = map.get(request.split(TelegramBot.DELIMITER)[0]);
        if (currentUpdateHandler != null) {
            return currentUpdateHandler;
        } else if (telegramUser.getBotState() == BotState.WAIT) {
            return map.get(Command.ERROR.getCommandText());
        } else {
            return map.get(Command.INTERVIEW.getCommandText());
        }

    }
}
