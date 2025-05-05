package com.kuklin.interview_telegram_service.models;

import lombok.Data;

@Data
public class SpeechRequest {
    String input;
    String model;
    String voice;
}
