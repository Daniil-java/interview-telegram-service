package com.kuklin.interview_telegram_service.integrations;

import com.kuklin.interview_telegram_service.models.ConversationDto;
import com.kuklin.interview_telegram_service.models.MessageRequestDto;
import com.kuklin.interview_telegram_service.models.MessageResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(
        value = "open-ai-conversation-service",
        url = "http://localhost:8081/api/v1/"
)
public interface OpenAIServiceFeignClient {

    @PostMapping("/messages/")
    MessageResponseDto sendMessage(@RequestBody MessageRequestDto request);

    @PostMapping("/conversations/text")
    ConversationDto createNewConversation(@RequestBody ConversationDto conversationDto);
}
