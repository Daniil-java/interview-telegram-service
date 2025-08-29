package com.kuklin.interview_telegram_service.telegram.handlers.progress;

import com.kuklin.interview_telegram_service.entities.UserEntity;
import com.kuklin.interview_telegram_service.entities.coach.Skill;
import com.kuklin.interview_telegram_service.entities.coach.Topic;
import com.kuklin.interview_telegram_service.entities.coach.TopicProgress;
import com.kuklin.interview_telegram_service.entities.coach.Vacancy;
import com.kuklin.interview_telegram_service.services.*;
import com.kuklin.interview_telegram_service.telegram.handlers.UpdateHandler;
import com.kuklin.interview_telegram_service.telegram.utils.Command;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProgressUpdateHandler implements UpdateHandler {
    private final TelegramService telegramService;
    private final TopicProgressService topicProgressService;
    private final SkillService skillService;
    private final TopicService topicService;
    private final VacancyService vacancyService;

    @Override
    @Transactional(readOnly = true)
    public void handle(Update update, UserEntity userEntity) {
        Message requestMessage = update.getMessage();
        Long chatId = requestMessage.getChatId();

        telegramService.sendReturnedMessage(chatId, generateReportForUser(userEntity));
    }

    public String generateReportForUser(UserEntity user) {
        // 1) Получаем все прогрессы по топикам для пользователя
        List<TopicProgress> progresses = topicProgressService.getTopicProgressByUser(user);

        // собираем мапу: topicId → confidenceLevel
        Map<Long, Integer> progressByTopic = progresses.stream()
                .collect(Collectors.toMap(
                        TopicProgress::getTopicId,
                        TopicProgress::getConfidenceLevel,
                        (a, b) -> a
                ));

        // 2) Загружаем вакансии через VacancyService
        List<Vacancy> vacancies = vacancyService.getVacanciesByUser(user);

        StringBuilder out = new StringBuilder("User: ").append(user.getName()).append("\n");

        for (Vacancy vacancy : vacancies) {
            // 3) Скилы для вакансии
            List<Skill> skills = skillService.getSkillsByVacancy(vacancy);

            // 4) Средний прогресс по вакансии
            double vacancyAvg = skills.stream()
                    .mapToDouble(skill -> {
                        List<Topic> topics = topicService.findTopicsBySkill(skill);
                        return topics.stream()
                                .mapToInt(t -> progressByTopic.getOrDefault(t.getId(), 0))
                                .average()
                                .orElse(0);
                    })
                    .average()
                    .orElse(0);

            out.append(vacancy.getTitle()).append("[")
                    .append(vacancy.getId())
                    .append("]: ")
                    .append(String.format("%.0f%%", vacancyAvg))
                    .append("\n");

            // 5) Детализация по скилам и топикам
            for (Skill skill : skills) {
                List<Topic> topics = topicService.findTopicsBySkill(skill);

                double skillAvg = topics.stream()
                        .mapToInt(t -> progressByTopic.getOrDefault(t.getId(), 0))
                        .average()
                        .orElse(0);

                out.append("  ")
                        .append(skill.getName())
                        .append(": ")
                        .append(String.format("%.0f%%", skillAvg))
                        .append("\n");

                for (Topic topic : topics) {
                    int level = progressByTopic.getOrDefault(topic.getId(), 0);
                    out.append("    ")
                            .append(topic.getName())
                            .append(": ")
                            .append(level)
                            .append("%\n");
                }
            }
        }

        return out.toString();
    }

    @Override
    public String getHandlerListName() {
        return Command.PROGRESS.getCommandText();
    }
}
