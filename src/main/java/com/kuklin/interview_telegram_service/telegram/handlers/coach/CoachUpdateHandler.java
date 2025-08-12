package com.kuklin.interview_telegram_service.telegram.handlers.coach;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kuklin.interview_telegram_service.entities.UserEntity;
import com.kuklin.interview_telegram_service.entities.coach.Skill;
import com.kuklin.interview_telegram_service.entities.coach.Topic;
import com.kuklin.interview_telegram_service.entities.coach.Vacancy;
import com.kuklin.interview_telegram_service.services.*;
import com.kuklin.interview_telegram_service.telegram.handlers.UpdateHandler;
import com.kuklin.interview_telegram_service.telegram.utils.Command;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CoachUpdateHandler implements UpdateHandler {
    private final TelegramService telegramService;
    private final TelegramUserService telegramUserService;
    private final VacancyService vacancyService;
    private final SkillService skillService;
    private final TopicService topicService;

    @Override
    public void handle(Update update, UserEntity userEntity) {
        Message requestMessage = update.getMessage();
        Long chatId = requestMessage.getChatId();
        String request = requestMessage.getText();

        try {
            Vacancy vacancy = vacancyService
                    .createVacancyName(userEntity, request);

            telegramService.sendReturnedMessage(chatId, getResponseMessage(vacancy));
        } catch (JsonProcessingException e) {
            log.error("Не получилось десериализовать ответ ИИ.", e);
            telegramService.sendReturnedMessage(chatId, "Обратитесь позже");
        }
    }

    private String getResponseMessage(Vacancy vacancy) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(vacancy.getTitle()).append("\n");
        List<Skill> skills = skillService.getSkillsByVacancyId(vacancy);

        for (Skill skill: skills) {
            stringBuilder.append(skill.getName()).append("\n");
            stringBuilder.append(skill.getCategory()).append("\n");
            List<Topic> topics = topicService.findTopicsBySkill(skill);
            for (Topic topic: topics) {
                stringBuilder.append("topic: ").append(topic.getName()).append("\n");
            }
        }

        return stringBuilder.toString();
    }

    @Override
    public String getHandlerListName() {
        return Command.COACH.getCommandText();
    }
}
