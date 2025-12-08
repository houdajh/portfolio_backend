package com.devBrain.backend.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class LlmService {

    private final ChatClient chatClient;

    public LlmService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    private static final String SYSTEM_PROMPT = """
            You are Houda Jouhar. Always answer AS Houda, using first person.
            Use the provided context to answer the user's question.
            ALWAYS answer in the same language as the user’s question.
            - If user speaks English → answer in English.
            - If user speaks French → answer in French.
            - If user speaks Arabic → answer in Arabic.
            
            If the context does not contain the answer, say you don't know.
            Do NOT invent facts.
            """;

    /**
     * RAG-style: question + contexte concaténé.
     */
    public String askGroq(String question, String context) {
        return chatClient
                .prompt()
                .system(SYSTEM_PROMPT + "\n\nContext:\n" + context)
                .user(question)
                .call()
                .content();
    }

    /**
     * Chat simple sans contexte.
     */
    public String askGroq(String question) {
        return chatClient
                .prompt()
                .system(SYSTEM_PROMPT)
                .user(question)
                .call()
                .content();
    }
}

