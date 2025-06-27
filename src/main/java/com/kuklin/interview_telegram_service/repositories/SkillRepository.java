package com.kuklin.interview_telegram_service.repositories;

import com.kuklin.interview_telegram_service.entities.coach.Skill;
import com.kuklin.interview_telegram_service.entities.coach.Vacancy;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {
    @EntityGraph(attributePaths = {"vacancies"})
    Optional<Skill> findByName(String name);
    List<Skill> findAllByVacanciesContains(Vacancy vacancy);
}
