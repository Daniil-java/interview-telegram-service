package com.kuklin.interview_telegram_service.entities.coach;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.OffsetDateTime;

@Entity
@Table(name = "topicsProgress")
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class TopicProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;

    @ManyToOne
    @JoinColumn(name = "topicId")
    private Topic topic;

    private Integer confidenceLevel;

    private OffsetDateTime lastReviewed;

    private Boolean isWeakArea;

    private OffsetDateTime updated;

    private OffsetDateTime created;
}
