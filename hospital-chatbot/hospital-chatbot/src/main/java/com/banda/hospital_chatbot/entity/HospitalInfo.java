package com.banda.hospital_chatbot.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "hospital_info")
@Data
public class HospitalInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String hospitalName;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String address;

    @Column(nullable = false, length = 20)
    private String phoneNumber;

    @Column(length = 50)
    private String email;

    @Column(nullable = false)
    private LocalTime openingTime;

    @Column(nullable = false)
    private LocalTime closingTime;

    @Column(nullable = false, length = 20)
    private String emergencyContact;

    private String website;

//    @Column(columnDefinition = "TEXT")
//    private String googleMapsLink;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return  "🏥 " + hospitalName + "\n\n" +
                "📍 Address: " + address + "\n\n" +
                "📞 Phone: " + phoneNumber + "\n" +
                "📧 Email: " + (email != null ? email : "N/A") + "\n\n" +
                "🕐 Opening: " + openingTime + "\n" +
                "🕐 Closing: " + closingTime + "\n\n" +
                "🚨 Emergency: " + emergencyContact;
    }
}
