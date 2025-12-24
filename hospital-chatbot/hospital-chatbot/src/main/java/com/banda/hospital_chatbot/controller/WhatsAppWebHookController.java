package com.banda.hospital_chatbot.controller;

import com.banda.hospital_chatbot.entity.FAQ;
import com.banda.hospital_chatbot.entity.FAQCategory;
import com.banda.hospital_chatbot.entity.HospitalInfo;
import com.banda.hospital_chatbot.service.ConversationStateService;
import com.banda.hospital_chatbot.service.FAQService;
import com.banda.hospital_chatbot.service.HospitalService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/webhook")
public class WhatsAppWebHookController {

    private final HospitalService hospitalService;
    private final FAQService faqService;
    private final ConversationStateService conversationStateService;

    public WhatsAppWebHookController(HospitalService hospitalService, FAQService faqService, ConversationStateService conversationStateService) {
        this.hospitalService = hospitalService;
        this.faqService = faqService;
        this.conversationStateService = conversationStateService;
    }

    @PostMapping(value = "/whatsapp", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> handleIncomingMessage(
            @RequestParam(value = "From", required = false) String from,
            @RequestParam(value = "Body", required = false) String body
    ) {

        String senderPhoneNumber = from.substring(9);
        System.out.println("Incoming WhatsApp Message From: " + senderPhoneNumber);
        System.out.println("Body: "+ body);
//        Get user's current state from the database
        String currentState = conversationStateService.getState(senderPhoneNumber);
        System.out.println("Current State: " + currentState);
        System.out.println("-----------------------------------");
        String responseMessage;

        if (body == null || body.trim().isEmpty()){
            conversationStateService.clearState(senderPhoneNumber);
            responseMessage = getMainMenu();
        }
        else{
            String trimmedBody = body.trim();
            int choice = 0;
            try{
                choice = Integer.parseInt(trimmedBody);
            }catch(NumberFormatException e){
                System.out.println("Invalid user input: ");
                getMainMenu();
            }

            if(choice == 0) {
                if (currentState.equals("FAQ_CATEGORIES")) {
                    // From categories, go back to main menu
                    conversationStateService.clearState(senderPhoneNumber);
                    responseMessage = getMainMenu();
                } else if (currentState.startsWith("FAQ_QUESTIONS_")) {
                    // From questions, go back to categories
                    conversationStateService.setState(senderPhoneNumber, "FAQ_CATEGORIES");
                    responseMessage = faqService.formatCategoryMenu();
                } else {
                    // Default: go back to main menu
                    conversationStateService.clearState(senderPhoneNumber);
                    responseMessage = getMainMenu();
                }
            }
            else if (currentState.equals("MAIN_MENU")){
                responseMessage = handleMainMenuChoice(senderPhoneNumber, choice);
            }
            else if(currentState.equals("FAQ_CATEGORIES")){
                responseMessage = handleFAQCategoryChoice(senderPhoneNumber, choice);
            }
            else if(currentState.startsWith("FAQ_QUESTIONS_")){
                String categoryIdStr = currentState.replace("FAQ_QUESTIONS_", "");
                if (categoryIdStr.isEmpty()) {
                    // Fallback if state was corrupted
                    conversationStateService.setState(senderPhoneNumber, "FAQ_CATEGORIES");
                    responseMessage = faqService.formatCategoryMenu();
                } else {
                    Long categoryId = Long.parseLong(categoryIdStr);
                    responseMessage = handleFAQQuestionChoice(senderPhoneNumber, categoryId, choice);
                }
            }
            else
            {
                conversationStateService.clearState(senderPhoneNumber);
                responseMessage = getMainMenu();
            }
        }


        System.out.println("Response Message: " + responseMessage);
        System.out.println("===================================");

        String twimlResponse = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<Response>\n" +
                "    <Message>" + escapeXml(responseMessage) + "</Message>\n" +
                "</Response>";

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_XML)
                .body(twimlResponse);
    }

    /**
     * Handle main menu choice
     */
    private String handleMainMenuChoice(String phoneNumber, int choice) {
        switch (choice) {
            case 1:
                return "📅 Appointment booking coming soon!\n\n0️⃣ Back to Main Menu";

            case 2:
                // Hospital information
                HospitalInfo info = hospitalService.getHospitalInfo();
                return info != null ? info.toString() + "\n\n0️⃣ Back to Main Menu" : "Harare Hospital info not available\n\n0️⃣ Back to Main Menu";

            case 3:
                return "💊 Services & Facilities coming soon!\n\n0️⃣ Back to Main Menu";

            case 4:
                // Emergency contact
                HospitalInfo emergencyInfo = hospitalService.getHospitalInfo();
                return emergencyInfo != null
                        ? "🚨 *Emergency Contact*\n\n" + emergencyInfo.getEmergencyContact() + "\n\n0️⃣ Back to Main Menu"
                        : "Emergency contact not available\n\n0️⃣ Back to Main Menu";

            case 5:
                return "🎯 Current Campaigns coming soon!\n\n0️⃣ Back to Main Menu";

            case 6:
                // FAQ categories
                conversationStateService.setState(phoneNumber, "FAQ_CATEGORIES");
                return faqService.formatCategoryMenu();

            default:
                return "❌ Invalid option. \nPlease select 1-6.\n\n" + getMainMenu();
        }
    }

    /**
     * Handle FAQ category choice
     */
    private String handleFAQCategoryChoice(String phoneNumber, int choice) {
        if (choice < 1 || choice > 5) {
            return "❌ Invalid category. \nPlease select 1-5.\n\n" + faqService.formatCategoryMenu();
        }
        FAQCategory category = faqService.getFAQCategoryByMenuPosition(choice);
        if (category == null) {
            return "❌ Category not found.\n\n" + faqService.formatCategoryMenu();
        }
        // Save state with category ID
        conversationStateService.setState(phoneNumber, "FAQ_QUESTIONS_" + category.getId());
        return faqService.formatFAQList(category.getId());
    }

    private String handleFAQQuestionChoice(String phoneNumber, Long categoryId, int choice) {
        List<FAQ> faqs = faqService.getFAQByCategoryID(categoryId);
        System.out.println("Number of FAQs found: " + faqs.size());
        System.out.println("User choice: " + choice);

        if (choice < 1 || choice > faqs.size()) {
            System.out.println("Choice out of range!");
            return "❌ Invalid question. \nPlease select 1-" + faqs.size() + ".\n\n"
                    + faqService.formatFAQList(categoryId);
        }

        // Get the FAQ at that position (choice is 1-based, list is 0-based)
        FAQ faq = faqs.get(choice - 1);
        System.out.println("FAQ retrieved: " + (faq != null ? faq.getQuestion() : "NULL"));

        if (faq == null) {
            System.out.println("FAQ is null!");
            return "❌ Question not found.\n\n" + faqService.formatFAQList(categoryId);
        }
        // Show answer and keep state so user can select another question
        return faqService.formatFAQAnswer(faq);
    }

    private String getMainMenu(){
        return "🏥 *Welcome to Harare Hospital Chatbot*\n\n" +
                "Please select an option:\n\n" +
                "1️⃣ Book Appointment\n" +
                "2️⃣ Hospital Information\n" +
                "3️⃣ Services & Facilities\n" +
                "4️⃣ Emergency Contact\n" +
                "5️⃣ Current Campaigns\n" +
                "6️⃣ FAQs\n\n" +
                " ";
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
