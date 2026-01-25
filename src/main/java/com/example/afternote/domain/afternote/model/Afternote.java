package com.example.afternote.domain.afternote.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "afternote")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Afternote {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "category_type", length = 20)
    private AfternoteCategoryType categoryType;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "process_method", length = 20)
    private ProcessMethod processMethod;
    
    @Column(nullable = false, length = 100)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "sort_order")
    private Integer sortOrder;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "afternote", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<AfternoteReceiver> receivers = new ArrayList<>();
    
    @OneToMany(mappedBy = "afternote", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<AfternoteSecureContent> secureContents = new ArrayList<>();
    
    @ElementCollection
    @CollectionTable(name = "afternote_actions", joinColumns = @JoinColumn(name = "afternote_id"))
    @Column(name = "action_name")
    @Builder.Default
    private List<String> actions = new ArrayList<>();
    
    @OneToMany(mappedBy = "afternote", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<AfternotePlaylist> playlists = new ArrayList<>();
}
