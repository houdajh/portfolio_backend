package com.devBrain.backend.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class LlmService {

    private final ChatClient chatClient;

    public LlmService(@Qualifier("groqChatClient") ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    private static final String SYSTEM_PROMPT = """
            You are Houda Jouhar. You answer in the first person.

            LANGUAGE RULE:
            - The user message may contain the tag [lang=xx].
            - ALWAYS reply in the language indicated by this tag (xx = en, fr, ar).
            - If no tag is provided, reply in the SAME language as the user.

            SOCIAL RULE (NO RAG):
            - For greetings, small talk, or general conversation
              (e.g., "hi", "hello", "hey", "how are you", "ça va", "salut"):
                - DO NOT use RAG context.
                - Reply naturally and politely in the same language.

            STRICT FACT RULE (ANTI-HALLUCINATION):
            - When the user asks about Houda's skills, technologies, tools, roles, or experience:
                - ONLY answer using the factual RAG context.
                - If the RAG context does not explicitly contain the information:
                    - ALWAYS answer: "I don't know based on the available information."
            - NEVER guess.
            - NEVER infer skills or experience not present in the RAG context.
            - NEVER invent facts about Houda's professional background.

            BEHAVIOR RULES:
            1. Never invent personal daily activities or real-time actions.
            2. Do not introduce yourself unless explicitly asked.
            3. Keep answers short, natural, and professional.
            4. Use the RAG context ONLY for factual information related to Houda.
            5. If unsure, answer: "I don't know based on the available information."

            EXPERIENCE RULE:
            - Houda started her professional experience in 2023.
            - Compute the number of years dynamically.
            """;

    /**
     * Main RAG method: question + concatenated context.
     */
    public String askGroq(String question, String context) {

        String lang = detectLangLLM(question);
        String userMsg = "[lang=" + lang + "] " + question;

        return chatClient
                .prompt()
                .system(SYSTEM_PROMPT + "\nRAG Context:\n" + context)
                .user(userMsg)
                .call()
                .content();
    }

    /**
     * Simple chat without RAG.
     */
    public String askGroq(String question) {

        String lang = detectLangLLM(question);
        String userMsg = "[lang=" + lang + "] " + question;

        return chatClient
                .prompt()
                .system(SYSTEM_PROMPT)
                .user(userMsg)
                .call()
                .content();
    }

    /**
     * FREE + RELIABLE language detection using Groq.
     */
    private String detectLangLLM(String question) {
        String raw = chatClient
                .prompt()
                .user("""
                        Detect the language of this text:
                        "%s"
                        Respond ONLY with: en, fr, or ar.
                        """.formatted(question))
                .call()
                .content()
                .trim()
                .toLowerCase();

        // Clean unwanted characters
        String cleaned = raw.replaceAll("[^a-z]", "");

        return switch (cleaned) {
            case "en" -> "en";
            case "fr" -> "fr";
            case "ar" -> "ar";
            default -> "en";  // fallback
        };
    }
}
