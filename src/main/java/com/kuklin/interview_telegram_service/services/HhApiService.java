package com.kuklin.interview_telegram_service.services;

import com.kuklin.interview_telegram_service.integrations.HhFeignClient;
import com.kuklin.interview_telegram_service.models.HhResponseDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
@Slf4j
public class HhApiService {
    private final HhFeignClient hhFeignClient;

    public HhResponseDto getHhVacancyDtoByHhId(Long vacancyId) {
        //Получение ДТО-вакансии по id, посредством обращения к api
        return hhFeignClient.getVacancyById(vacancyId);
    }

}
