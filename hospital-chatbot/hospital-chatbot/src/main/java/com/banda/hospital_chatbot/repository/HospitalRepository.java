package com.banda.hospital_chatbot.repository;

import com.banda.hospital_chatbot.entity.HospitalInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HospitalRepository extends JpaRepository<HospitalInfo, Long> {
}
