package com.kuklin.interview_telegram_service.models.openai;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Usage {
    private int promptTokens;
    private int completionTokens;
    private int totalTokens;
}
