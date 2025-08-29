package com.kuklin.interview_telegram_service.entities.coach;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.OffsetDateTime;

@Entity
@Table(name = "questions")
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer interviewId;

    @ManyToOne
    @JoinColumn(name = "topicId")
    private Topic topic;

    @Column(columnDefinition = "text")
    private String answer;

    private Integer score;

    private OffsetDateTime updated;

    private OffsetDateTime created;
}
