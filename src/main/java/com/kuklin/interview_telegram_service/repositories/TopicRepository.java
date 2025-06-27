package com.kuklin.interview_telegram_service.repositories;

import com.kuklin.interview_telegram_service.entities.coach.Skill;
import com.kuklin.interview_telegram_service.entities.coach.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {
    List<Topic> findAllBySkill(Skill skill);
}
