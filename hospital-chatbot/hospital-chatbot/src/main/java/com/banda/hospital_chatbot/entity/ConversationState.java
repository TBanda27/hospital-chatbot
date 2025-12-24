package com.banda.hospital_chatbot.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "conversation_state")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConversationState {

    @Id
    @Column(name = "phone_number", nullable = false, length = 20)
    private String phoneNumber;

    @Column(name = "current_step", nullable = false, length = 50)
    private String currentStep;

    @Column(name = "context_data", columnDefinition = "TEXT")
    private String contextData;

    @Column(name = "last_updated", nullable = false)
    private LocalDateTime lastUpdated;

    @PrePersist
    @PreUpdate
    public void prePersistUpdate() {
        lastUpdated = LocalDateTime.now();
    }

    public ConversationState(String phoneNumber, String currentStep) {
        this.phoneNumber = phoneNumber;
        this.currentStep = currentStep;
        this.lastUpdated = LocalDateTime.now();
    }

}
