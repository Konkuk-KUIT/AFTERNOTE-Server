package com.example.afternote.domain.receiver.repository;

import com.example.afternote.domain.receiver.model.Receiver;
import com.example.afternote.domain.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReceivedRepository extends JpaRepository<Receiver, Long> {

}
