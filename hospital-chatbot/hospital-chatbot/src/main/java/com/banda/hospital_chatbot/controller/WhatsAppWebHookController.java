package com.banda.hospital_chatbot.controller;

import com.banda.hospital_chatbot.entity.HospitalInfo;
import com.banda.hospital_chatbot.service.FAQService;
import com.banda.hospital_chatbot.service.HospitalService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/webhook")
public class WhatsAppWebHookController {

    private final HospitalService hospitalService;
    private final FAQService faqService;

    public WhatsAppWebHookController(HospitalService hospitalService, FAQService faqService) {
        this.hospitalService = hospitalService;
        this.faqService = faqService;
    }

    @PostMapping(value = "/whatsapp", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> handleIncomingMessage(
            @RequestParam(value = "From", required = false) String from,
            @RequestParam(value = "Body", required = false) String body
    ) {
        System.out.println("Incoming WhatsApp message");
        System.out.println("From: " + from);
        System.out.println("Body: " + body);
        System.out.println("-----------------------");
        String responseMessage;

        if (body == null || body.trim().isEmpty()){
            responseMessage = getMainMenu();
        }
        else{
            String trimmedBody = body.trim();
            if(trimmedBody.equals("1")){
                responseMessage = "Coming Soon:\n" + getMainMenu() ;
            }
            else if (trimmedBody.equals("2")){
                HospitalInfo hospitalInfo = hospitalService.getHospitalInfo();
                responseMessage = hospitalInfo != null ? hospitalInfo.toString() : "Hospital information is currently unavailable.";
            }
            else if(trimmedBody.equals("6")){
                responseMessage = faqService.formatCategoryMenu();
            }
            else{
                responseMessage = "Invalid Option: Please select a valid option.\n\n" + getMainMenu();
            }
        }

        String twimlResponse = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<Response>\n" +
                "    <Message>" + escapeXml(responseMessage) + "</Message>\n" +
                "</Response>";

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_XML)
                .body(twimlResponse);
    }

    private String getMainMenu(){
        return "🏥 *Welcome to Harare Hospital Chatbot*\n\n" +
                "Please select an option:\n" +
                "1️⃣ Book Appointment\n" +
                "2️⃣ Hospital Information\n" +
                "3️⃣ Services & Facilities\n" +
                "4️⃣ Emergency Contact\n" +
                "5️⃣ Current Campaigns\n" +
                "6️⃣ FAQs\n\n" +
                "Reply with the number of your choice.";
    }

    private String escapeXml(String text) {
        if (text == null) return "";

        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}
