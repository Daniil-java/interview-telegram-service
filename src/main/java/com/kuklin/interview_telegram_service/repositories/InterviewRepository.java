package com.kuklin.interview_telegram_service.repositories;

import com.kuklin.interview_telegram_service.entities.Interview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InterviewRepository extends JpaRepository<Interview, Long> {
}
