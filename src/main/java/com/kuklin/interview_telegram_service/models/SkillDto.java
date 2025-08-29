package com.kuklin.interview_telegram_service.models;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Set;

@Data
@Accessors(chain = true)
public class SkillDto {
    private String name;
    private String category;
    private Set<TopicDto> topics;
}
