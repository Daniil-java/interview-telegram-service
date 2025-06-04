package com.kuklin.interview_telegram_service.telegram.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Command {
    START("/start"),
    JOB("/job"),
    IF("/if"),
    START_INTERVIEW("/start_interview"),
    END("/end"),
    HISTORY("/history"),
    RESULTS("/results"),
    ERROR("error"),
    INTERVIEW("interview"),
    URL_PROCESS("url_process"),
    RANDOM_INTERVIEW("/random_interview")
    ;
    private final String commandText;
}
