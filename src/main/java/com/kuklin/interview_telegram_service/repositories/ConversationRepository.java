package com.kuklin.interview_telegram_service.repositories;

import com.kuklin.interview_telegram_service.entities.Conversation;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    @EntityGraph(attributePaths = "user")
    Optional<Conversation> findById(Long id);
}
