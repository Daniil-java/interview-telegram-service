package com.kuklin.interview_telegram_service.services;

import com.kuklin.interview_telegram_service.entities.ChatMessage;
import com.kuklin.interview_telegram_service.integrations.OpenAiFeignClient;
import com.kuklin.interview_telegram_service.models.AiResponse;
import com.kuklin.interview_telegram_service.models.enums.ProviderVariant;
import com.kuklin.interview_telegram_service.models.openai.OpenAiChatCompletionRequest;
import com.kuklin.interview_telegram_service.models.openai.OpenAiChatCompletionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class OpenAiIntegrationService {

    private final OpenAiFeignClient openAiFeignClient;
    private final String aiKey;

    public OpenAiIntegrationService(@Value("${GENERATION_TOKEN}") String aiKey, OpenAiFeignClient openAiFeignClient) {
        this.aiKey = aiKey;
        this.openAiFeignClient = openAiFeignClient;
    }

    public AiResponse fetchResponse(ChatMessage userMessage, List<ChatMessage> chatMessageList) {
        OpenAiChatCompletionRequest request = OpenAiChatCompletionRequest.makeRequest(
                chatMessageList, userMessage.getModel(), userMessage.getTemperature());

        OpenAiChatCompletionResponse response =
                openAiFeignClient.generate("Bearer " + aiKey, request);

        return response.toAiResponse(userMessage.getModel());
    }

    public String fetchDaemonResponse(String content) {
        OpenAiChatCompletionRequest request = OpenAiChatCompletionRequest
                .makeDefaultRequest(content);

        OpenAiChatCompletionResponse response =
                openAiFeignClient.generate("Bearer " + aiKey, request);

        return response.getContent();
    }

    public ProviderVariant getProviderName() {
        return ProviderVariant.OPENAI;
    }

}
