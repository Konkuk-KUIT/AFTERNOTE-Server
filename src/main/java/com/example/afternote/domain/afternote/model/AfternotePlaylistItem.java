package com.example.afternote.domain.afternote.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "afternote_playlist_item")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class AfternotePlaylistItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "playlist_id", nullable = false)
    private AfternotePlaylist playlist;
    
    @Column(name = "song_title", nullable = false, length = 200)
    private String songTitle;
    
    @Column(length = 100)
    private String artist;
    
    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;
}
