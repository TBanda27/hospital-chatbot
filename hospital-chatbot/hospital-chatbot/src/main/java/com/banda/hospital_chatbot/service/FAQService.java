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
        for (FAQCategory category : categories) {
            menu.append(getNumberEmoji(category.getDisplayOrder()))
                .append(category.getCategoryName())
                .append("\n");
        }
        menu.append("\nReply with the category number:\n");
        return menu.toString();
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

        for (FAQ faq : faqs) {
            list.append(getNumberEmoji(faq.getDisplayOrder()))
                .append(faq.getQuestion())
                .append("\n");
        }
        list.append("\nReply with the question number.\n");
        return list.toString();

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
