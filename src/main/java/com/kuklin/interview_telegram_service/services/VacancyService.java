package com.kuklin.interview_telegram_service.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kuklin.interview_telegram_service.entities.UserEntity;
import com.kuklin.interview_telegram_service.entities.coach.Vacancy;
import com.kuklin.interview_telegram_service.exceptions.ErrorResponseException;
import com.kuklin.interview_telegram_service.exceptions.ErrorStatus;
import com.kuklin.interview_telegram_service.models.MessageRequestDto;
import com.kuklin.interview_telegram_service.models.SkillDto;
import com.kuklin.interview_telegram_service.models.enums.ChatModel;
import com.kuklin.interview_telegram_service.repositories.VacancyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class VacancyService {

    private final VacancyRepository vacancyRepository;
    private final SkillService skillService;
    private final ChatMessageService chatMessageService;
    private static final String REQUEST = """
            Я отправлю тебе название должности или вакансии.
            Твоя задача разложить вакансию на навыки.
            А навыки на темы, изучив которые можно научиться навыку.
            Пиши только hardskills, не надо общих слов.
            Не пиши ничего лишнего, никаких объяснений. 
            В качестве ответа, ты должен использовать ТОЛЬКО JSON. 
            Отправь только чистый JSON, без форматирования или обрамляющих блоков. 
            Cообщение следующего формата
            [\n
            {\n
            \"name\": \"Java\", \n
            \"category\":\"Programming Language\", \n
            \"topics\":[{\n\"name\":\"Spring Framework\"}, {\n\"name\":\"Java Core\"}]
            },\n
            ...\n
            ]\n
            Название вакансии или должности: %s
            """;

    public Vacancy createVacancyName(UserEntity user, String title) throws JsonProcessingException {
        //Формирование сообщения для сервиса общения с ИИ
        MessageRequestDto messageRequestDto = new MessageRequestDto()
                .setContent(String.format(REQUEST, title))
                .setModel(ChatModel.GPT4O);
        String jsonAnswer =
                chatMessageService.sendServiceMessage(user, messageRequestDto);

        //Десериализация объекта
        Set<SkillDto> skillDtos = new ObjectMapper().readValue(
                jsonAnswer,
                new TypeReference<Set<SkillDto>>() {}
        );

        Vacancy vacancy = new Vacancy()
                .setTitle(title)
                .setUserId(user.getId());
        vacancy = vacancyRepository.save(vacancy);

        skillService.createNewSkillsOrGetExists(skillDtos, vacancy, user);

        return vacancy;
    }

    public List<Vacancy> getVacanciesByUser(UserEntity user, Pageable paging) {
        return vacancyRepository.findAllByUserId(user.getId(), paging);
    }

    public List<Vacancy> getVacanciesByUser(UserEntity user) {
        return vacancyRepository.findAllByUserId(user.getId());
    }

    public Vacancy getVacancyById(Long vacancyId) {
        return vacancyRepository.findById(vacancyId)
                .orElseThrow(() -> new ErrorResponseException(ErrorStatus.VACANCY_NOT_FOUND));
    }
}
