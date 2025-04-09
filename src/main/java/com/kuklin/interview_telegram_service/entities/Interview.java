package com.kuklin.interview_telegram_service.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "interviews")
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class Interview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String jobTittle;
    private String result;
    private String analysis;
    @UpdateTimestamp
    private LocalDateTime updated;
    @CreationTimestamp
    private LocalDateTime created;
}
