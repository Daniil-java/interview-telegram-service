package com.kuklin.interview_telegram_service.services;

import com.kuklin.interview_telegram_service.entities.ChatMessage;
import com.kuklin.interview_telegram_service.entities.Conversation;
import com.kuklin.interview_telegram_service.entities.Model;
import com.kuklin.interview_telegram_service.entities.UserEntity;
import com.kuklin.interview_telegram_service.exceptions.ErrorResponseException;
import com.kuklin.interview_telegram_service.exceptions.ErrorStatus;
import com.kuklin.interview_telegram_service.models.AiResponse;
import com.kuklin.interview_telegram_service.models.MessageRequestDto;
import com.kuklin.interview_telegram_service.models.MessageResponseDto;
import com.kuklin.interview_telegram_service.models.enums.MessageStatus;
import com.kuklin.interview_telegram_service.repositories.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatMessageService {
    private final ChatMessageRepository chatMessageRepository;
    private final ConversationService conversationService;
    private final ModelService modelService;
    private final OpenAiIntegrationService openAiIntegrationService;

    public String sendDaemonMessage(String content) throws ErrorResponseException {
        try {
            return openAiIntegrationService.fetchDaemonResponse(content);
        } catch (Exception e) {
            log.error("AI Connection error!");
            throw new ErrorResponseException(ErrorStatus.AI_CONNECTION_ERROR);
        }
    }

    public ChatMessage processUserMessageOrGetNull(MessageRequestDto messageRequestDto, UserEntity user) throws ErrorResponseException {
        //Конвертация дто в сущность
        ChatMessage userMessage = makeUserMessage(messageRequestDto);

        if (userMessage == null) throw new NullPointerException();

        //Получение контекста беседы
        List<ChatMessage> chatMessageList =
                chatMessageRepository.findAllByConversation_Id(messageRequestDto.getConversationId());

        if (user.getBalance().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ErrorResponseException(ErrorStatus.USER_INSUFFICIENT_FUNDS);
        }

        try {
            //Получение провайдера по параметру приходящего запроса и исполнение запроса
            AiResponse response = openAiIntegrationService.fetchResponse(
                    userMessage, chatMessageList);

            //Конвертация ответа в сущность
            ChatMessage assistantMessage = ChatMessage.newAssistantMessage(
                    response,
                    userMessage
            );

            userMessage.setStatus(MessageStatus.DONE);
            //Вычитание токенов с баланса пользователя
            user.setBalance(user.getBalance()
                    .subtract(assistantMessage.getInputToken()
                            .add(assistantMessage.getOutputToken()))
            );
            chatMessageRepository.save(userMessage);

            assistantMessage.setStatus(MessageStatus.DONE);
            return chatMessageRepository.save(assistantMessage);
        } catch (Exception e) {
            setStatusAndErrorDetails(userMessage, MessageStatus.ERROR, e.getMessage());
            throw new ErrorResponseException(ErrorStatus.AI_CONNECTION_ERROR);
        }
    }


    private ChatMessage makeUserMessage(MessageRequestDto messageRequestDto) {
        Conversation conversation = conversationService
                .getByIdOrGetNull(messageRequestDto.getConversationId());

        Model model = modelService.findModelOrThrowError(messageRequestDto.getModel());

        if (conversation == null) return null;

        if (conversation.getName() == null) {
            conversation = conversationService.setNameForConversation(conversation, messageRequestDto.getContent());
        }

        ChatMessage userMessage = ChatMessage.newUserMessage(messageRequestDto, conversation, model);

        return chatMessageRepository.save(userMessage).setConversation(conversation);
    }

    private void setStatusAndErrorDetails(ChatMessage userMessage, MessageStatus messageStatus, String errorStatus) {
        userMessage.setStatus(messageStatus);
        userMessage.setErrorDetails(errorStatus);
        chatMessageRepository.save(userMessage);
    }
}
