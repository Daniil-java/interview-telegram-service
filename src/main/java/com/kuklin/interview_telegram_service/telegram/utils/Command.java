package com.kuklin.interview_telegram_service.telegram.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Command {
    START("/start"),
    JOB("/job"),
    IF("/if"),
    START_INTERVIEW("/startinterview"),
    END("/end"),
    HISTORY("/history"),
    MY_RESULTS("/myResults"),
    ERROR("error"),
    INTERVIEW("interview");

    private final String commandText;
}
