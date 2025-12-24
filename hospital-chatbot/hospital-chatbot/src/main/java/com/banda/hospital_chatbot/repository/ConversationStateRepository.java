package com.banda.hospital_chatbot.repository;

import com.banda.hospital_chatbot.entity.ConversationState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface ConversationStateRepository extends JpaRepository<ConversationState, String> {

    int deleteByLastUpdatedBefore(LocalDateTime cutoffDate);
}
