package com.banda.hospital_chatbot.repository;

import com.banda.hospital_chatbot.entity.FAQCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FAQCategoryRepository extends JpaRepository<FAQCategory, Long> {

    List<FAQCategory> findAllByOrderByDisplayOrderAsc();
}
