package com.kuklin.interview_telegram_service.telegram.handlers;

import com.kuklin.interview_telegram_service.entities.Interview;
import com.kuklin.interview_telegram_service.entities.UserEntity;
import com.kuklin.interview_telegram_service.services.InterviewService;
import com.kuklin.interview_telegram_service.services.TelegramService;
import com.kuklin.interview_telegram_service.telegram.utils.Command;
import com.kuklin.interview_telegram_service.telegram.utils.ThreadUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HistoryUpdateHandler implements UpdateHandler {
    private final InterviewService interviewService;
    private final TelegramService telegramService;
    @Override
    public void handle(Update update, UserEntity userEntity) {
        Message requestMessage = update.getMessage();
        Long chatId = requestMessage.getChatId();

        List<Interview> interviewList = interviewService.getLatestResultList(userEntity.getId());
        for (Interview i: interviewList) {
            telegramService.sendReturnedMessage(chatId, i.getResult());
            ThreadUtil.sleep(100);
        }
    }

    @Override
    public String getHandlerListName() {
        return Command.HISTORY.getCommandText();
    }
}
