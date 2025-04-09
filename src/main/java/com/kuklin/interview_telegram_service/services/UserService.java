package com.kuklin.interview_telegram_service.services;

import com.kuklin.interview_telegram_service.entities.TelegramUser;
import com.kuklin.interview_telegram_service.entities.UserEntity;
import com.kuklin.interview_telegram_service.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.User;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final TelegramUserService telegramUserService;

    public UserEntity getUserByTelegramIdOrNull(Long telegramId) {
        return userRepository.findUserEntityByTelegramUserId(telegramId)
                .orElse(null);
    }

    public UserEntity createUserByTelegram(User telegramUser) {
        TelegramUser tgUser = telegramUserService.createOrGetUserByTelegram(telegramUser);

        UserEntity user = new UserEntity()
                .setName(telegramUser.getUserName())
                .setTelegramUserId(tgUser.getTelegramId());

        return userRepository.save(user);
    }

    public UserEntity getOrCreateUser(User telegramUser) {
        UserEntity userEntity = getUserByTelegramIdOrNull(telegramUser.getId());
        if (userEntity != null) {
            return userEntity;
        } else {
            return createUserByTelegram(telegramUser);
        }
    }

    public UserEntity setJobTittleOrGetNull(Long userId, String jobTittle) {
        UserEntity userEntity = userRepository.findById(userId).orElse(null);
        if (userEntity == null) return null;
        return userRepository.save(userEntity.setJobTittle(jobTittle));
    }

    public UserEntity setPropertiesOrGetNull(Long userId, String properties) {
        UserEntity userEntity = userRepository.findById(userId).orElse(null);
        if (userEntity == null) return null;
        return userRepository.save(userEntity.setJobTittle(properties));
    }

}
