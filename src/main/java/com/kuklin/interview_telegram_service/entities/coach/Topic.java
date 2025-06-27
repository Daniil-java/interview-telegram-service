package com.kuklin.interview_telegram_service.entities.coach;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.OffsetDateTime;

@Entity
@Table(name = "topics")
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class Topic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private OffsetDateTime updated;

    private OffsetDateTime created;

    @ManyToOne
    @JoinColumn(name = "skill_id")
    private Skill skill;
}
