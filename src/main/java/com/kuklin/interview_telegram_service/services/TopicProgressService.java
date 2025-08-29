package com.kuklin.interview_telegram_service.services;

import com.kuklin.interview_telegram_service.entities.UserEntity;
import com.kuklin.interview_telegram_service.entities.coach.Topic;
import com.kuklin.interview_telegram_service.entities.coach.TopicProgress;
import com.kuklin.interview_telegram_service.repositories.TopicProgressRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TopicProgressService {
    private final TopicProgressRepository topicProgressRepository;

    //Создание сущности прогресса топика, при создании топика
    public TopicProgress createProgress(UserEntity user, Topic topic) {
        TopicProgress topicProgress = new TopicProgress()
                .setTopicId(topic.getId())
                .setUserId(user.getId())
                .setConfidenceLevel(0)
                .setIsWeakArea(true)
                ;

        return topicProgressRepository.save(topicProgress);
    }

    //Изменение состояния прогресса. Прогресс определяется ИИ.
    public TopicProgress updateProgress(UserEntity user, Topic topic, int level) {
        //Уровень, при котором знание топика считается плохим
        int weakAreaLevel = 60;
        Optional<TopicProgress> optionalTopicProgress =
                topicProgressRepository.findByUserIdAndTopicId(user.getId(), topic.getId());

        TopicProgress topicProgress;
        //Создание топика в случае его отсутствия.
        if (optionalTopicProgress.isEmpty()) {
            topicProgress = createProgress(user, topic);
        } else {
            topicProgress = optionalTopicProgress.get();
        }

        return topicProgressRepository.save(topicProgress
                .setConfidenceLevel(level)
                .setIsWeakArea(level <= weakAreaLevel)
        );
    }

    public List<TopicProgress> getTopicProgressByUser(UserEntity user) {
        return topicProgressRepository.findAllByUserId(user.getId());
    }
}
