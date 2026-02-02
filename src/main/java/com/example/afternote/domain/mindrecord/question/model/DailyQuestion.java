package com.example.afternote.domain.mindrecord.question.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "daily_question")
@Getter
@Setter
@NoArgsConstructor
public class DailyQuestion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 255)
    private String content;

    @Column(nullable = false)
    private Boolean isActive = true;

}
