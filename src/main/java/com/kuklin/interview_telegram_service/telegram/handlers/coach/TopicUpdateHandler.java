package com.kuklin.interview_telegram_service.telegram.handlers.coach;

import com.kuklin.interview_telegram_service.entities.UserEntity;
import com.kuklin.interview_telegram_service.entities.coach.Skill;
import com.kuklin.interview_telegram_service.entities.coach.Topic;
import com.kuklin.interview_telegram_service.services.SkillService;
import com.kuklin.interview_telegram_service.services.TelegramService;
import com.kuklin.interview_telegram_service.services.TopicService;
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
public class TopicUpdateHandler implements UpdateHandler {

    private final TopicService topicService;
    private final SkillService skillService;
    private final TelegramService telegramService;
    private final TopicInterviewUpdateHandler topicInterviewUpdateHandler;
    public static final String ID_LIST_COMMAND = "/topicId";
    private static final String NEXT_COMMAND = "/prevPage#";
    private static final String PREV_COMMAND = "/nextPage#";
    private static final int LIST_PAGE_ROW_COUNT = 5;

    @Override
    public void handle(Update update, UserEntity userEntity) {
        if (update.hasCallbackQuery()) {
            processCallback(update.getCallbackQuery());
        }
    }

    private void processCallback(CallbackQuery callbackQuery) {
        String data = callbackQuery.getData();
        long skillId = extractSkillId(data);
        int page = extractPage(data);
        Skill skill = skillService.getSkillByIdOrNull(skillId);
        List<Topic> topicList = topicService.findTopicsBySkill(
                skill,
                PageRequest.of(page, LIST_PAGE_ROW_COUNT, Sort.by("id")));

        telegramService.sendEditMessage(
                callbackQuery.getMessage().getChatId(),
                getTopicInfo(skill, topicList),
                callbackQuery.getMessage().getMessageId(),
                getInlineMessageVacancyListButtons(topicList, skillId, LIST_PAGE_ROW_COUNT, page));
    }

    private long extractSkillId(String data) {
        String temp = data.substring(getHandlerListName().length() + 1);
        int start = SkillUpdateHandler.ID_LIST_COMMAND.length();
        int slashIndex = temp.indexOf("/", start);
        if (slashIndex == -1) slashIndex = temp.length();
        return Long.parseLong(temp.substring(start, slashIndex));
    }

    private int extractPage(String data) {
        if (data.contains(NEXT_COMMAND) || data.contains(PREV_COMMAND)) {
            return Integer.parseInt(
                    data.substring(
                            data.contains(NEXT_COMMAND)
                                    ? data.indexOf(NEXT_COMMAND) + NEXT_COMMAND.length()
                                    : data.indexOf(PREV_COMMAND) + PREV_COMMAND.length()
                    )
            );
        } else {
            return 0;
        }
    }

    private String getTopicInfo(Skill skill, List<Topic> topicList) {
        return "Специальность: " + skill.getName() + "\n" +
                topicList.stream()
                        .map(Topic::getName)
                        .collect(Collectors.joining("\n"));
    }

    private InlineKeyboardMarkup getInlineMessageVacancyListButtons(
            List<Topic> topicList, long skillId, int rowCount, int page
    ) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        //Кнопки с навыками
        for (Topic topic : topicList) {
            InlineKeyboardButton skillButton = new InlineKeyboardButton(
                    String.format("[%s]: %s", topic.getId(), topic.getName())
            );
            skillButton.setCallbackData(
                    topicInterviewUpdateHandler.getHandlerListName()
                            + " " + ID_LIST_COMMAND + topic.getId()
            );
            rows.add(Collections.singletonList(skillButton));
        }

        //Навигация
        List<InlineKeyboardButton> navRow = new ArrayList<>();
        if (page > 0) {
            InlineKeyboardButton prevButton = new InlineKeyboardButton("⬅️");
            prevButton.setCallbackData(
                    getHandlerListName() + " " +
                            SkillUpdateHandler.ID_LIST_COMMAND + skillId +
                            PREV_COMMAND + (page - 1)
            );
            navRow.add(prevButton);
        }
        if (topicList.size() == rowCount) {
            InlineKeyboardButton nextButton = new InlineKeyboardButton("➡️");
            nextButton.setCallbackData(
                    getHandlerListName() + " " +
                            SkillUpdateHandler.ID_LIST_COMMAND + skillId +
                            NEXT_COMMAND + (page + 1)
            );
            navRow.add(nextButton);
        }
        if (!navRow.isEmpty()) {
            rows.add(navRow);
        }

        //Кнопка "BACK"
        InlineKeyboardButton backButton = new InlineKeyboardButton("BACK");
        backButton.setCallbackData(Command.COACH.getCommandText());
        rows.add(Collections.singletonList(backButton));

        inlineKeyboardMarkup.setKeyboard(rows);
        return inlineKeyboardMarkup;
    }


    @Override
    public String getHandlerListName() {
        return Command.TOPIC.getCommandText();
    }
}
