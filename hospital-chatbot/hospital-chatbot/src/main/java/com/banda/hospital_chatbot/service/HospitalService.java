package com.banda.hospital_chatbot.service;


import com.banda.hospital_chatbot.entity.HospitalInfo;
import com.banda.hospital_chatbot.repository.HospitalRepository;
import org.springframework.stereotype.Service;

@Service
public class HospitalService {

    private final HospitalRepository hospitalRepository;

    public HospitalService(HospitalRepository hospitalRepository) {
        this.hospitalRepository = hospitalRepository;
    }

    public HospitalInfo getHospitalInfo() {
        return hospitalRepository.findAll().stream().findFirst().orElse(null);
    }

}
