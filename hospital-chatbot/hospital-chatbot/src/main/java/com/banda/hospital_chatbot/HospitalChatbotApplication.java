package com.banda.hospital_chatbot;

import com.banda.hospital_chatbot.entity.HospitalInfo;
import com.banda.hospital_chatbot.service.HospitalService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HospitalChatbotApplication implements CommandLineRunner {
    private final HospitalService hospitalService;

    public HospitalChatbotApplication(HospitalService hospitalService) {
        this.hospitalService = hospitalService;
    }

    public static void main(String[] args) {
		SpringApplication.run(HospitalChatbotApplication.class, args);
	}


    @Override
    public void run(String... args) throws Exception {
        System.out.println("Hospital Chatbot Application Started...");
        HospitalInfo hospitalInfo = hospitalService.getHospitalInfo();
        if (hospitalInfo !=  null){
            System.out.println(hospitalInfo);
            System.out.println("+++++++++++++++++++++++++++++++++++++++++");
        }
        else {
            System.out.println("Hospital Info Not Found");
        }
    }
}
