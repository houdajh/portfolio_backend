package com.devBrain.backend.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class DebugPropsRunner implements CommandLineRunner {

    private final Environment env;

    public DebugPropsRunner(Environment env) {
        this.env = env;
    }

    @Override
    public void run(String... args) {
        System.out.println("QDRANT URL = " + env.getProperty("spring.ai.vectorstore.qdrant.url"));
        System.out.println("QDRANT HOST = " + env.getProperty("spring.ai.vectorstore.qdrant.host"));
        System.out.println("QDRANT PORT = " + env.getProperty("spring.ai.vectorstore.qdrant.port"));
        System.out.println("QDRANT USE_GRPC = " + env.getProperty("spring.ai.vectorstore.qdrant.use-grpc"));
    }
}