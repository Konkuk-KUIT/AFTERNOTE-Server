package com.example.afternote.domain.afternote.repository;

import com.example.afternote.domain.afternote.model.AfternotePlaylist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AfternotePlaylistRepository extends JpaRepository<AfternotePlaylist, Long> {
}
