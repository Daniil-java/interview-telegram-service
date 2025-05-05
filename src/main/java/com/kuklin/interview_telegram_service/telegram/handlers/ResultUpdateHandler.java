package com.kuklin.interview_telegram_service.telegram.handlers;

import com.kuklin.interview_telegram_service.entities.ChatMessage;
import com.kuklin.interview_telegram_service.entities.Conversation;
import com.kuklin.interview_telegram_service.entities.Interview;
import com.kuklin.interview_telegram_service.entities.UserEntity;
import com.kuklin.interview_telegram_service.models.MessageRequestDto;
import com.kuklin.interview_telegram_service.services.*;
import com.kuklin.interview_telegram_service.telegram.utils.Command;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ResultUpdateHandler implements UpdateHandler {
    private final InterviewService interviewService;
    private final ConversationService conversationService;
    private final TelegramService telegramService;
    private final ChatMessageService chatMessageService;
    private static final String EMPTY_ERROR = "Сначала закончите хотя бы одно собеседование";
    private static final String AI_REQUEST = """
            Сейчас тебе будут отправлены результаты моих нескольких собеседований.
            Тебе нужно сделать общий анализ. Проанализировать мои результаты, 
            сказать что я достиг, что еще предстоит улучшить.\n 
            """;
    @Override
    public void handle(Update update, UserEntity userEntity) {
        Message requestMessage = update.getMessage();
        Long chatId = requestMessage.getChatId();

        List<Interview> interviewList = interviewService.getLatestResultList(userEntity.getId());
        if (interviewList.isEmpty()) {
            telegramService.sendReturnedMessage(chatId,EMPTY_ERROR);
            return;
        }

        Conversation conversation = conversationService.getNewConversation(userEntity);

        String results = AI_REQUEST + interviewList.stream()
                .map(Interview::getResult)
                .collect(Collectors.joining("\n"));

        MessageRequestDto messageRequestDto = MessageRequestDto.getDefault(results, conversation.getId());
        ChatMessage chatMessage = chatMessageService
                .processUserMessageOrGetNull(messageRequestDto,userEntity);

        telegramService.sendReturnedMessage(chatId, chatMessage.getContent());


    }

    @Override
    public String getHandlerListName() {
        return Command.RESULTS.getCommandText();
    }
}
