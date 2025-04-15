package com.kuklin.interview_telegram_service.services;

import com.kuklin.interview_telegram_service.entities.Model;
import com.kuklin.interview_telegram_service.exceptions.ErrorResponseException;
import com.kuklin.interview_telegram_service.exceptions.ErrorStatus;
import com.kuklin.interview_telegram_service.models.enums.ChatModel;
import com.kuklin.interview_telegram_service.repositories.ModelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ModelService {
    private final ModelRepository modelRepository;

    public Model findModelOrThrowError(ChatModel chatModel) {
        return modelRepository.findByModelName(chatModel.getModel())
                .orElseThrow(() -> new ErrorResponseException(ErrorStatus.PROVIDER_NOT_FOUND));
    }
}
