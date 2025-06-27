package com.kuklin.interview_telegram_service.services;

import com.kuklin.interview_telegram_service.entities.coach.Skill;
import com.kuklin.interview_telegram_service.entities.coach.Topic;
import com.kuklin.interview_telegram_service.models.TopicDto;
import com.kuklin.interview_telegram_service.repositories.TopicRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class TopicService {
    private final TopicRepository topicRepository;

    public Set<Topic> createNewTopics(Set<TopicDto> topicDtos, Skill skill) {
        //Определиться как создать связь ManyToMany, и как находить уже существующеие
        List<Topic> topics = new ArrayList<>();
        for (TopicDto dto: topicDtos) {
            Topic topic = new Topic()
                    .setName(dto.getName())
                    .setSkill(skill);
            topics.add(topic);
        }
        return new HashSet<>(topicRepository.saveAll(topics));
    }

    public List<Topic> findTopicsBySkill(Skill skill) {
        return topicRepository.findAllBySkill(skill);
    }
}
