package com.kuklin.interview_telegram_service.entities;

import com.kuklin.interview_telegram_service.telegram.utils.BotState;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.telegram.telegrambots.meta.api.objects.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "telegram_users")
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class TelegramUser {
    @Id
    @Column(name = "telegram_id")
    private Long telegramId;
    private String username;
    private String firstname;
    private String lastname;
    private String languageCode;
    @Enumerated(EnumType.STRING)
    private BotState botState;
    private Long actualAiConversationId;
    @UpdateTimestamp
    private LocalDateTime updated;
    @CreationTimestamp
    private LocalDateTime created;

    private TelegramUser setTelegramId(Long telegramId) {
        this.telegramId = telegramId;
        return this;
    }

    public static TelegramUser convertFromTelegram(User user) {
        return new TelegramUser()
                .setTelegramId(user.getId())
                .setUsername(user.getUserName())
                .setFirstname(user.getFirstName())
                .setLastname(user.getLastName())
                .setLanguageCode(user.getLanguageCode());
    }
}
