package com.kuklin.interview_telegram_service.entities.coach;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.OffsetDateTime;

@Entity
@Table(name = "vacancy")
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class Vacancy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tittle;

    private String sourceUrl;

    private Long userId;

    private OffsetDateTime updated;

    private OffsetDateTime created;
}
