package com.kuklin.interview_telegram_service.telegram.handlers.interview;

import com.kuklin.interview_telegram_service.entities.TelegramUser;
import com.kuklin.interview_telegram_service.entities.UserEntity;
import com.kuklin.interview_telegram_service.models.MessageResponseDto;
import com.kuklin.interview_telegram_service.services.OpenAiIntegrationService;
import com.kuklin.interview_telegram_service.services.TelegramService;
import com.kuklin.interview_telegram_service.services.TelegramUserService;
import com.kuklin.interview_telegram_service.telegram.handlers.UpdateHandler;
import com.kuklin.interview_telegram_service.telegram.utils.BotState;
import com.kuklin.interview_telegram_service.telegram.utils.Command;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class EndInterviewUpdateHandler implements UpdateHandler {
    private final TelegramService telegramService;
    private final TelegramUserService telegramUserService;
    private final OpenAiIntegrationService openAiIntegrationService;
    private static final String ERROR_MESSAGE = "Произошла ошибка! Не получилось закончить собеседование!";
    private static final String AI_REQUEST_MESSAGE = "Собеседование закончено. " +
            "Проанализируй ответы. Расскажи об ошибках. " +
            "Сделай выводы. Скажи какие темы стоит подтянуть";
    @Override
    public void handle(Update update, UserEntity userEntity) {
        Message requestMessage = update.getMessage();
        Long chatId = requestMessage.getChatId();

        TelegramUser telegramUser = telegramUserService.setBotStateByIdOrGetNull(
                userEntity.getTelegramUserId(), BotState.WAIT);

        if (telegramUser == null) {
            telegramService.sendReturnedMessage(chatId, ERROR_MESSAGE);
            return;
        }

        Long conversationId = telegramUser.getActualAiConversationId();
        MessageResponseDto messageResponseDto =
                openAiIntegrationService.sendMessage(AI_REQUEST_MESSAGE, conversationId);

        telegramService.sendReturnedMessage(chatId, messageResponseDto.getContent());
    }

    @Override
    public String getHandlerListName() {
        return Command.END.getCommandText();
    }
}
