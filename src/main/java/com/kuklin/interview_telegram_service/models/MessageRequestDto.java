package com.kuklin.interview_telegram_service.models;

import com.kuklin.interview_telegram_service.models.enums.ChatModel;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class MessageRequestDto {
    @NotNull(message = "У сообщения должно быть содержание")
    private String content;
    @NotNull
    private Long conversationId;
    private Float temperature;
    private ChatModel model;

    public static MessageRequestDto getDefault(String content, Long conversationId) {
        return new MessageRequestDto()
                .setContent(content)
                .setConversationId(conversationId)
                .setModel(ChatModel.GPT4O);
    }
}
