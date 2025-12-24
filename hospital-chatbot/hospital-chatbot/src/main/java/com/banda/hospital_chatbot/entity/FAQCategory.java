package com.banda.hospital_chatbot.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class FAQCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String categoryName;

    @Column(nullable = false)
    private Integer displayOrder;
}
