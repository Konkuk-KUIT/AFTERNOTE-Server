package com.example.afternote.domain.receiver.model;

import com.example.afternote.domain.afternote.model.AfternoteReceiver;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "receiver")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Receiver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 50)
    private String relation;  // 관계 (친구, 가족 등)

    @Column(length = 20)
    private String phone;  // 전화번호

    @Column(length = 50)
    private String email;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public Receiver(String name, String relation, String phone, String email, Long userId) {
        this.name = name;
        this.relation = relation;
        this.phone = phone;
        this.email = email;
        this.userId = userId;
        this.sortOrder = 0;
        this.createdAt = LocalDateTime.now();
    }

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AfternoteReceiver> afternoteReceivers = new ArrayList<>();
}