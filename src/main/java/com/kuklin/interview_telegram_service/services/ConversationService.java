package com.kuklin.interview_telegram_service.services;

import com.kuklin.interview_telegram_service.entities.Conversation;
import com.kuklin.interview_telegram_service.entities.UserEntity;
import com.kuklin.interview_telegram_service.repositories.ConversationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConversationService {

    private final ConversationRepository conversationRepository;

    public Conversation getNewConversation(UserEntity user) {
        return getNewConversation(user, null);
    }

    public Conversation getNewConversation(UserEntity user, String conversationName) {
        return conversationRepository.save(
                new Conversation()
                        .setUser(user)
                        .setName(conversationName));
    }

    public Conversation getByIdOrGetNull(Long id) {
        return conversationRepository.findById(id).orElse(null);
    }

    public Conversation setNameForConversation(Conversation conversation, String content) {
        return conversationRepository.save(conversation.setName(content));
    }
}
