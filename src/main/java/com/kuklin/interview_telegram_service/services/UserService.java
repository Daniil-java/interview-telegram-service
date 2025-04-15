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
        return userRepository.findUserEntityByTelegramId(telegramId)
                .orElse(null);
    }

    public UserEntity createUserByTelegram(User telegramUser) {
        TelegramUser tgUser = telegramUserService.createOrGetUserByTelegram(telegramUser);

        UserEntity user = new UserEntity()
                .setName(telegramUser.getUserName())
                .setTelegramId(tgUser.getTelegramId());

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

    public UserEntity setJobTittleOrGetNull(UserEntity user, String jobTittle) {
        return userRepository.save(user.setJobTitle(jobTittle));
    }

    public UserEntity setPropertiesOrGetNull(UserEntity user, String properties) {
        return userRepository.save(user.setJobTitle(properties));
    }

}
