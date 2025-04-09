package com.kuklin.interview_telegram_service.models;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ConversationDto {
    private Long id;
    private String name;
}
