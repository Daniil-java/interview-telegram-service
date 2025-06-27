package com.kuklin.interview_telegram_service.models;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class VacancyDto {
    private String tittle;

    private String sourceUrl;

    private Integer userId;
}
