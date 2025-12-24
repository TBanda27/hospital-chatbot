package com.banda.hospital_chatbot.service;

import com.banda.hospital_chatbot.entity.ConversationState;
import com.banda.hospital_chatbot.repository.ConversationStateRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ConversationStateService {

    private final ConversationStateRepository conversationStateRepository;
    public ConversationStateService(ConversationStateRepository conversationStateRepository) {
        this.conversationStateRepository = conversationStateRepository;
    }

    public String getState(String phoneNumber){
        Optional<ConversationState> stateOpt = conversationStateRepository.findById(phoneNumber);
        return stateOpt.map(ConversationState::getCurrentStep).orElse("MAIN_MENU");
    }

    public void setState(String phoneNumber, String step){
        ConversationState conversationState = conversationStateRepository.findById(phoneNumber)
                .orElse(new ConversationState(phoneNumber, step));
        conversationState.setCurrentStep(step);
        conversationStateRepository.save(conversationState);
    }

    public void setState(String phoneNumber, String step, String contextData) {
        ConversationState state = conversationStateRepository.findById(phoneNumber)
                .orElse(new ConversationState(phoneNumber, step));

        state.setCurrentStep(step);
        state.setContextData(contextData);
        conversationStateRepository.save(state);
    }

    public void clearState(String phoneNumber){
        conversationStateRepository.deleteById(phoneNumber);
    }

    @Scheduled(fixedRate = 3600000) // Every hour
    public void cleanupOldStates(){
        LocalDateTime now = LocalDateTime.now().minusHours(1);
        int deletedCount = conversationStateRepository.deleteByLastUpdatedBefore(now);

        if(deletedCount > 0){
            System.out.printf("Cleaned up %d old conversation states.%n", deletedCount);
        }
    }

}
