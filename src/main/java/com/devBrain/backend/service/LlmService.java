package com.devBrain.backend.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class LlmService {

    private final ChatClient chatClient;

    public LlmService(
            @Qualifier("groqChatClient") ChatClient chatClient,
            @Value("${app.llm.max-answer-chars}") int maxAnswerChars
    ) {
        this.chatClient = chatClient;
        this.maxAnswerChars = maxAnswerChars;
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
            3. Use the RAG context ONLY for factual information related to Houda.
            4. If unsure, answer: "I don't know based on the available information."

            EXPERIENCE RULE:
            - Houda started her professional experience in 2023.
            - Compute the number of years dynamically.
            """;

    // Kept apart from SYSTEM_PROMPT because it is appended AFTER the RAG context,
    // so it is the last thing the model reads before answering.
    private static final String LENGTH_RULE = """

            LENGTH AND STYLE RULE (final, overrides everything above):
            - Answer in AT MOST 3 sentences and about 60 words, in a conversational tone.
            - Do NOT recite everything the context contains: pick only the most relevant points.
            - NEVER use markdown tables, headings, or bullet/numbered lists. Write plain prose only.
            - If the question needs more detail than fits, answer the essentials
              and end with a short invitation to ask a follow-up question (in the user's language).
            - The fixed fallback "I don't know based on the available information." may stand alone as a single sentence.
            """;

    private static final String USER_REMINDER =
            "(Reply in at most 3 sentences, about 60 words, plain prose, no lists.)";

    private final int maxAnswerChars;

    private static String buildSystemPrompt(String context) {
        String ragPart = context != null ? "\nRAG Context:\n" + context + "\n" : "";
        return SYSTEM_PROMPT + ragPart + LENGTH_RULE;
    }

    private static String buildUserMessage(String lang, String question) {
        return "[lang=" + lang + "] " + question + "\n\n" + USER_REMINDER;
    }

    /**
     * Main RAG method: question + concatenated context.
     */
    public String askGroq(String question, String context) {

        String lang = detectLangLLM(question);

        String answer = chatClient
                .prompt()
                .system(buildSystemPrompt(context))
                .user(buildUserMessage(lang, question))
                .call()
                .content();

        return AnswerLimiter.limit(answer, maxAnswerChars, lang);
    }

    /**
     * Simple chat without RAG.
     */
    public String askGroq(String question) {

        String lang = detectLangLLM(question);

        String answer = chatClient
                .prompt()
                .system(buildSystemPrompt(null))
                .user(buildUserMessage(lang, question))
                .call()
                .content();

        return AnswerLimiter.limit(answer, maxAnswerChars, lang);
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
