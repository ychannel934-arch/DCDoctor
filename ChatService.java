package com.dcdoctor.service;

import com.dcdoctor.model.Doctor;

public class ChatService {

    private final GeminiService geminiService;

    public ChatService() {
        this.geminiService = new GeminiService();
    }

    public String handleMessage(String message, Doctor doctor) {
        if (message == null || message.trim().isEmpty()) {
            return "Please enter a question.";
        }

        return geminiService.ask(message.trim());
    }
}