package com.kuklin.interview_telegram_service.repositories;

import com.kuklin.interview_telegram_service.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findUserEntityByTelegramId(Long telegramId);

}
