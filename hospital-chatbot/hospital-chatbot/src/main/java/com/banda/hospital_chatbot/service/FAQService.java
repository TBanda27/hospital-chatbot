package com.banda.hospital_chatbot.service;

import com.banda.hospital_chatbot.entity.FAQ;
import com.banda.hospital_chatbot.entity.FAQCategory;
import com.banda.hospital_chatbot.repository.FAQCategoryRepository;
import com.banda.hospital_chatbot.repository.FAQRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FAQService {

    private final FAQRepository faqRepository;
    private final FAQCategoryRepository faqCategoryRepository;

    public FAQService(FAQRepository faqRepository, FAQCategoryRepository faqCategoryRepository) {
        this.faqRepository = faqRepository;
        this.faqCategoryRepository = faqCategoryRepository;
    }

    public List<FAQCategory> getAllFAQCategories() {
        return faqCategoryRepository.findAllByOrderByDisplayOrderAsc();
    }

    public List<FAQ> getFAQsByCategoryId(Long categoryId) {
        return faqRepository.findByCategoryIdOrderByDisplayOrderAsc(categoryId);
    }

    public String formatCategoryMenu(){
        List<FAQCategory> categories = getAllFAQCategories();
        StringBuilder menu = new StringBuilder("Please select a category:\n\n");
        int displayNumber = 1;
        for (FAQCategory category : categories) {
            menu.append(getNumberEmoji(displayNumber))
                .append(category.getCategoryName())
                .append("\n");
            displayNumber++;
        }
        menu.append("\n0️⃣ Back to Main Menu");
        return menu.toString();
    }

    public FAQCategory getFAQCategoryByMenuPosition(int choice) {
        List<FAQCategory> categories = getAllFAQCategories();
        if(choice < 1 || choice>categories.size()){
            return null;
        }
        return categories.get(choice -1 );
    }

    public FAQ getFAQById(Long faqId) {
        return faqRepository.findById(faqId).orElse(null);
    }

    public List<FAQ> getFAQByCategoryID(Long categoryId){
        return faqRepository.findByCategoryIdOrderByDisplayOrderAsc(categoryId);
    }

    public String formatFAQList(Long categoryId){
        List<FAQ> faqs = getFAQsByCategoryId(categoryId);
        if(faqs.isEmpty()){
            return "No FAQs available in this category.";
        }
        FAQCategory category = faqCategoryRepository.findById(categoryId).orElse(null);
        if(category == null) {
            return "Category not found.";
        }

        StringBuilder list = new StringBuilder("❓ *")
                .append(category.getCategoryName())
                .append("*\n\n");

        int displayNumber = 1;
        for (FAQ faq : faqs) {
            list.append(getNumberEmoji(displayNumber))
                .append(faq.getQuestion())
                .append("\n");
            displayNumber++;
        }
        list.append("\n0️⃣ Back to Categories");
        return list.toString();
    }

    public String formatFAQAnswer(FAQ faq) {
        if (faq == null) {
            return "FAQ not found.";
        }

        StringBuilder answer = new StringBuilder();
        answer.append("❓ *Question:* ")
                .append(faq.getQuestion())
                .append("\n\n");

        answer.append("💬 *Answer:*\n")
                .append(faq.getAnswer())
                .append("\n\n");
        answer.append("0️⃣ Back to Questions");
        return answer.toString();
    }

    private String getNumberEmoji(int number){
        return switch (number) {
            case 1 -> "1️⃣";
            case 2 -> "2️⃣";
            case 3 -> "3️⃣";
            case 4 -> "4️⃣";
            case 5 -> "5️⃣";
            case 6 -> "6️⃣";
            case 7 -> "7️⃣";
            case 8 -> "8️⃣";
            case 9 -> "9️⃣";
            case 10 -> "🔟";
            default -> number + ".";
        };
    }
}
