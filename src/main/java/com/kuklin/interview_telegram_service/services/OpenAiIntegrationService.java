package com.kuklin.interview_telegram_service.services;

import com.kuklin.interview_telegram_service.integrations.OpenAIServiceFeignClient;
import com.kuklin.interview_telegram_service.models.ConversationDto;
import com.kuklin.interview_telegram_service.models.MessageRequestDto;
import com.kuklin.interview_telegram_service.models.MessageResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpenAiIntegrationService {

    private final OpenAIServiceFeignClient openAIServiceFeignClient;

    public MessageResponseDto sendMessage(String content, Long conversationId) {
        return openAIServiceFeignClient.sendMessage(
                MessageRequestDto.getDefault(content, conversationId));
    }

    public Long getNewConversationId() {
        return openAIServiceFeignClient.createNewConversation(new ConversationDto()).getId();
    }
}
