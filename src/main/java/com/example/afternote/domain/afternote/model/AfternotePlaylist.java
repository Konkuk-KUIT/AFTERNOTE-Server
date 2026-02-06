package com.example.afternote.domain.afternote.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "afternote_playlist")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class AfternotePlaylist {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "afternote_id", nullable = false)
    private Afternote afternote;
    
    @Column(nullable = false, length = 100)
    private String title;
    
    @Column(length = 500)
    private String atmosphere;
    
    @Embedded
    private MemorialVideo memorialVideo;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "DATETIME")
    private LocalDateTime createdAt;
    
    @OneToMany(mappedBy = "playlist", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<AfternotePlaylistItem> items = new ArrayList<>();
    
    /**
     * PATCH 업데이트: null이 아닌 필드만 업데이트
     */
    public void update(String atmosphere, MemorialVideo memorialVideo) {
        if (atmosphere != null) {
            this.atmosphere = atmosphere;
        }
        if (memorialVideo != null) {
            this.memorialVideo = memorialVideo;
        }
    }
    
    @Embeddable
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    @Builder
    public static class MemorialVideo {
        
        @Column(name = "video_url", length = 500)
        private String videoUrl;
        
        @Column(name = "thumbnail_url", length = 500)
        private String thumbnailUrl;
    }
}
