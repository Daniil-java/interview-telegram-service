package com.kuklin.interview_telegram_service.services;

import com.kuklin.interview_telegram_service.entities.UserEntity;
import com.kuklin.interview_telegram_service.entities.coach.Skill;
import com.kuklin.interview_telegram_service.entities.coach.Topic;
import com.kuklin.interview_telegram_service.models.TopicDto;
import com.kuklin.interview_telegram_service.repositories.TopicRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TopicService {
    private final TopicRepository topicRepository;
    private final TopicProgressService topicProgressService;

    @Transactional
    public Set<Topic> createNewTopics(Set<TopicDto> topicDtos, Skill skill, UserEntity user) {
        //Определиться как создать связь ManyToMany, и как находить уже существующеие
        Set<Topic> topicHashSet = topicDtos.stream()
                .map(dto -> new Topic()
                        .setName(dto.getName())
                        .setSkill(skill))
                .collect(Collectors.toSet());

        for (Topic topic: topicHashSet) {
            topic = topicRepository.save(topic);
            topicProgressService.createProgress(user, topic);
        }
        return topicHashSet;
    }

    public Topic getTopicByIdOrNull(Long topicId) {
        return topicRepository.findById(topicId).orElse(null);
    }

    public List<Topic> findTopicsBySkill(Skill skill, Pageable pageable) {
        return topicRepository.findAllBySkill(skill, pageable);
    }
    public List<Topic> findTopicsBySkill(Skill skill) {
        return topicRepository.findAllBySkill(skill);
    }

    public Topic save(Topic topic) {
        return topicRepository.save(topic);
    }
}
