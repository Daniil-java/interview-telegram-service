package com.kuklin.interview_telegram_service.repositories;

import com.kuklin.interview_telegram_service.entities.coach.Skill;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {
    @EntityGraph(attributePaths = {"vacancies"})
    Optional<Skill> findByName(String name);
    @Query("SELECT s FROM Skill s JOIN s.vacancies v WHERE v.id = :vacancyId")
    List<Skill> findAllByVacancyId(@Param("vacancyId") Long vacancyId);
    @Query("SELECT s FROM Skill s JOIN s.vacancies v WHERE v.id = :vacancyId")
    Page<Skill> findAllByVacancyId(@Param("vacancyId") Long vacancyId, Pageable pageable);
}
