package com.devBrain.backend.config;

import io.micrometer.observation.ObservationRegistry;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.retry.support.RetryTemplate;

@Configuration
public class GroqConfig {

    @Bean
    public ChatClient groqChatClient(
            @Value("${GROQ_API_KEY}") String apiKey,
            @Value("${GROQ_MODEL:openai/gpt-oss-20b}") String model,
            ToolCallingManager toolCallingManager,
            RetryTemplate retryTemplate,
            ObservationRegistry observationRegistry
    ) {

        OpenAiApi openAiApi = OpenAiApi.builder()
                .baseUrl("https://api.groq.com/openai")
                .apiKey(() -> apiKey)
                .build();

        OpenAiChatModel chatModel = new OpenAiChatModel(
                openAiApi,
                OpenAiChatOptions.builder()
                        .model(model)
                        .build(),
                toolCallingManager,
                retryTemplate,
                observationRegistry
        );

        return ChatClient.create(chatModel);
    }
}