package com.kuklin.interview_telegram_service.repositories;

import com.kuklin.interview_telegram_service.entities.Interview;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InterviewRepository extends JpaRepository<Interview, Long> {

    Optional<Interview> findInterviewByConversation_Id(Long conversationId);

    @Query("SELECT i FROM Interview i WHERE i.user.id = :userId ORDER BY i.created DESC")
    List<Interview> findAllByUserIdOrderByCreatedDesc(@Param("userId") Long userId, Pageable pageable);

}
