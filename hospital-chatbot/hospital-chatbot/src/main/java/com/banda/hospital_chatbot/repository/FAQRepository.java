package com.banda.hospital_chatbot.repository;

import com.banda.hospital_chatbot.entity.FAQ;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FAQRepository extends JpaRepository<FAQ, Long> {

    List<FAQ> findAllByOrderByDisplayOrderAsc();
    List<FAQ> findByCategoryIdOrderByDisplayOrderAsc(Long categoryId);
}
