package com.kuklin.interview_telegram_service.models.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MessageStatus {
    NEW, PROCESSING, DONE, ERROR;
}
