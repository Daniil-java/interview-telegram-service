package com.kuklin.interview_telegram_service.services;

import com.kuklin.interview_telegram_service.entities.Conversation;
import com.kuklin.interview_telegram_service.entities.Interview;
import com.kuklin.interview_telegram_service.entities.UserEntity;
import com.kuklin.interview_telegram_service.repositories.InterviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class InterviewService {

    private final InterviewRepository interviewRepository;
    private final ConversationService conversationService;

    public Interview saveInterview(Long conversationId, UserEntity user) {
        Conversation conversation = conversationService.getByIdOrGetNull(conversationId);
        if (conversation == null) {
            log.error("Failed to save conversation!");
            return null;
        }
        Interview interview = new Interview()
                .setConversation(conversation)
                .setJobTittle(user.getJobTitle())
                .setProperties(user.getProperties())
                .setUser(user);

        return interviewRepository.save(interview);
    }

    public Interview saveResult(UserEntity userEntity, Long conversationId, String response) {
        Interview interview = interviewRepository
                .findInterviewByConversation_Id(conversationId).orElse(null);

        if (interview == null) {
            log.error("Failed to save conversation!");
            return null;
        }

        return interviewRepository.save(interview
                .setResult(response)
                .setUser(userEntity));
    }

    public List<Interview> getLatestResultList(Long userId) {
        int listLength = 5;
        Pageable pageable = PageRequest.of(0, listLength);
        return interviewRepository.findAllByUserIdOrderByCreatedDesc(userId, pageable);
    }
}
