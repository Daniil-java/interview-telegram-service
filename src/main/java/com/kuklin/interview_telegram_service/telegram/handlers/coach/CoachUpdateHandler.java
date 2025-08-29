package com.kuklin.interview_telegram_service.telegram.handlers.coach;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kuklin.interview_telegram_service.entities.UserEntity;
import com.kuklin.interview_telegram_service.entities.coach.Vacancy;
import com.kuklin.interview_telegram_service.services.*;
import com.kuklin.interview_telegram_service.telegram.handlers.UpdateHandler;
import com.kuklin.interview_telegram_service.telegram.utils.Command;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class CoachUpdateHandler implements UpdateHandler {
    private final TelegramService telegramService;
    private final VacancyService vacancyService;
    private final SkillUpdateHandler skillUpdateHandler;

    public static final String ID_LIST_COMMAND = "/listid#";
    private static final String NEXT_COMMAND = "/prevpage#";
    private static final String PREV_COMMAND = "/nextpage#";
    private static final int LIST_PAGE_ROW_COUNT = 5;

    @Override
    public void handle(Update update, UserEntity userEntity) {
        if (update.hasCallbackQuery()) {
            processCallback(update, userEntity);
        } else {
            processNewCoachCommand(update, userEntity);
        }
    }

    //Обработка нового сообщения с командой COACH
    private void processNewCoachCommand(Update update, UserEntity userEntity) {
        //Извлечение сообщения пользователя
        String request = update.getMessage().getText()
                .substring(Command.COACH.getCommandText().length())
                .trim();

        //Если сообщение не пустое, то начинается создание новой вакансии, для пользователя
        if (!request.isEmpty()) {
            try {
                vacancyService.createVacancyName(userEntity, request);
            } catch (JsonProcessingException e) {
                log.error("Ошибка десериализации ответа ИИ", e);
                telegramService.sendReturnedMessage(update.getMessage().getChatId(), "Обратитесь позже");
                return;
            }
        }

        sendVacancyPage(update.getMessage().getChatId(), userEntity, 0, null);
    }

    //Обработка callback из уже существующего сообщения
    private void processCallback(Update update, UserEntity userEntity) {
        CallbackQuery callbackQuery = update.getCallbackQuery();
        String data = callbackQuery.getData();

        //Случай для возвращения списка с самого начала
        if (data.equals(Command.COACH.getCommandText())) {
            sendVacancyPage(callbackQuery.getMessage().getChatId(), userEntity, 0, callbackQuery.getMessage().getMessageId());
        }
        //Случай, когда пользователь выбрал вакансию из списка в клавиатуре
        else if (data.contains(ID_LIST_COMMAND)) {
            skillUpdateHandler.handle(update, userEntity);
        }
        //Пролистывание списка
        else if (data.contains(NEXT_COMMAND) || data.contains(PREV_COMMAND)) {
            int page = extractPage(data);
            sendVacancyPage(callbackQuery.getMessage().getChatId(), userEntity, page, callbackQuery.getMessage().getMessageId());
        }
    }

    //Отправка или редактирование сообщения со списком вакансий
    private void sendVacancyPage(Long chatId, UserEntity userEntity, int page, Integer editMessageId) {
        List<Vacancy> vacancyList = vacancyService.getVacanciesByUser(
                userEntity,
                PageRequest.of(page, LIST_PAGE_ROW_COUNT, Sort.by("id"))
        );

        String message = getVacanciesInfo(vacancyList);

        if (editMessageId == null) {
            telegramService.sendReturnedMessage(chatId, message,
                    getInlineMessageVacancyListButtons(vacancyList, LIST_PAGE_ROW_COUNT, page), null);
        } else {
            telegramService.sendEditMessage(chatId, message, editMessageId,
                    getInlineMessageVacancyListButtons(vacancyList, LIST_PAGE_ROW_COUNT, page));
        }
    }

    //Формирование текстового содержания сообщения
    private String getVacanciesInfo(List<Vacancy> vacancies) {
        return vacancies.stream()
                .map(Vacancy::getTitle)
                .collect(Collectors.joining("\n"));
    }

    //Извлечение номера страницы из callback-данных
    private int extractPage(String data) {
        String cmd = data.contains(NEXT_COMMAND) ? NEXT_COMMAND : PREV_COMMAND;
        return Integer.parseInt(data.substring(getHandlerListName().length() + 1 + cmd.length()));
    }

    //Создание клавиатуры-списка
    public InlineKeyboardMarkup getInlineMessageVacancyListButtons(List<Vacancy> vacancyList, int rowCount, int page) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        // Кнопки вакансий
        for (Vacancy vacancy : vacancyList) {
            InlineKeyboardButton button = new InlineKeyboardButton(
                    String.format("[%s]: %s", vacancy.getId(), vacancy.getTitle()));
            button.setCallbackData(
                    skillUpdateHandler.getHandlerListName() + " " + SkillUpdateHandler.VACANCY_ID + vacancy.getId()
            );

            rows.add(Collections.singletonList(button));
        }

        // Кнопки навигации
        List<InlineKeyboardButton> navRow = new ArrayList<>();
        if (page > 0) {
            InlineKeyboardButton prev = new InlineKeyboardButton("⬅️");
            prev.setCallbackData(getHandlerListName() + " " + PREV_COMMAND + (page - 1));
            navRow.add(prev);
        }
        if (vacancyList.size() == rowCount) {
            InlineKeyboardButton next = new InlineKeyboardButton("➡️");
            next.setCallbackData(getHandlerListName() + " " + NEXT_COMMAND + (page + 1));
            navRow.add(next);
        }
        if (!navRow.isEmpty()) {
            rows.add(navRow);
        }

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(rows);
        return markup;
    }

    @Override
    public String getHandlerListName() {
        return Command.COACH.getCommandText();
    }
}
