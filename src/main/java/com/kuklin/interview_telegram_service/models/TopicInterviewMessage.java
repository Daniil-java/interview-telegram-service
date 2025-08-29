package com.kuklin.interview_telegram_service.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class TopicInterviewMessage {
    private String content;
    @JsonProperty("isEnd")
    private boolean isEnd;
    private int score;
}
