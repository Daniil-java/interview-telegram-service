package com.kuklin.interview_telegram_service.services;

import com.kuklin.interview_telegram_service.entities.UserEntity;
import com.kuklin.interview_telegram_service.entities.coach.Skill;
import com.kuklin.interview_telegram_service.entities.coach.Vacancy;
import com.kuklin.interview_telegram_service.models.SkillDto;
import com.kuklin.interview_telegram_service.repositories.SkillRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class SkillService {
    private final SkillRepository skillRepository;
    private final TopicService topicService;

    public List<Skill> getSkillsByVacancy(Vacancy vacancy) {
        return skillRepository.findAllByVacancyId(vacancy.getId());
    }

    public Skill getSkillByIdOrNull(Long skillId) {
        return skillRepository.findById(skillId).orElse(null);
    }

    public List<Skill> getPagingSkillsByVacancyId(Long vacancyId, Pageable paging) {
        return skillRepository.findAllByVacancyId(vacancyId, paging).getContent();
    }

    public Set<Skill> createNewSkillsOrGetExists(Set<SkillDto> skillDtos, Vacancy vacancy, UserEntity user) {
        Set<Skill> set = new HashSet<>();
        for (SkillDto dto: skillDtos) {
            //Проверка существувования такого навыка
            Optional<Skill> optionalSkill = skillRepository.findByName(dto.getName());
            if (optionalSkill.isPresent()) {
                //Добавление связи ManyToMany
                Skill skill = optionalSkill.get();

                skill.getVacancies().add(vacancy);
                skillRepository.save(skill);
            } else {
                Skill skill = new Skill()
                        .setName(dto.getName())
                        .setCategory(dto.getCategory())
                        .setVacancies(new HashSet<>(List.of(vacancy)));
                skill = skillRepository.save(skill);

                topicService.createNewTopics(dto.getTopics(), skill, user);

            }
        }
        return set;
    }
}
