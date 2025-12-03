package com.portfolio.backend.config;

import com.portfolio.backend.model.LibraryItem;
import com.portfolio.backend.model.Project;
import com.portfolio.backend.model.TimelineEntry;
import com.portfolio.backend.repository.LibraryItemRepository;
import com.portfolio.backend.repository.ProjectRepository;
import com.portfolio.backend.repository.TimelineEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final ProjectRepository projectRepo;
    private final LibraryItemRepository libraryRepo;
    private final TimelineEntryRepository timelineRepo;

    @Override
    public void run(String... args) {

        /*
        |--------------------------------------------------------------------------
        | PROJECTS SEED
        |--------------------------------------------------------------------------
        */
        if (projectRepo.count() == 0) {

            projectRepo.save(new Project(
                    null,
                    "insightflow-ai",
                    "InsightFlow AI",
                    "AI-powered Retrieval System",
                    "A RAG system using vector embeddings and Spring Boot.",
                    "Complete AI retrieval platform with Qdrant vectors, Spring Boot APIs, React dashboard, and LLM integration.",
                    "Spring Boot,React,RAG,Qdrant,AI",
                    "https://github.com/houadja/insightflow-ai",
                    "https://insightflow-demo.com"
            ));

            projectRepo.save(new Project(
                    null,
                    "tradenet-suite",
                    "TradeNet Microservices Suite",
                    "Banking Workflow Automation",
                    "Distributed backend platform with Kafka + Keycloak.",
                    "Modular microservices backend for financial requests processing, secure authentication, and workflow automation.",
                    "Spring Boot,Kafka,Keycloak,React,Docker",
                    "https://github.com/houadja/tradenet-suite",
                    null
            ));
        }


        /*
        |--------------------------------------------------------------------------
        | LIBRARY ITEMS SEED
        |--------------------------------------------------------------------------
        */
        if (libraryRepo.count() == 0) {

            String baseUrl = "https://yourcompany.atlassian.net/wiki/";

    /*
     |-----------------------------------
     | BACKEND (12 items)
     |-----------------------------------
     */
            libraryRepo.save(new LibraryItem(null, "Backend", "DTO vs Entity — Best Practices",
                    "When and why DTOs are mandatory in clean architecture.",
                    "spring,architecture,clean-code",
                    "Deep explanation about DTO layering.",
                    baseUrl + "backend-dto-vs-entity"));

            libraryRepo.save(new LibraryItem(null, "Backend", "REST Controller Design Patterns",
                    "Build scalable, maintainable REST APIs.",
                    "api,rest,design",
                    "Naming, verbs, payload structure.",
                    baseUrl + "backend-rest-design"));

            libraryRepo.save(new LibraryItem(null, "Backend", "Service Layer Anti-Patterns",
                    "Common mistakes in service architecture.",
                    "backend,clean-code",
                    "Identify and avoid anti-patterns.",
                    baseUrl + "backend-service-antipatterns"));

            libraryRepo.save(new LibraryItem(null, "Backend", "Database Indexing Strategies",
                    "Improve performance with correct indexing.",
                    "db,performance",
                    "Clustered vs non-clustered indexes.",
                    baseUrl + "backend-db-indexing"));

            libraryRepo.save(new LibraryItem(null, "Backend", "Transactional Boundaries",
                    "How to correctly use @Transactional.",
                    "spring,transaction",
                    "Propagation and isolation explained.",
                    baseUrl + "backend-transactional"));

            libraryRepo.save(new LibraryItem(null, "Backend", "Exception Handling Architecture",
                    "Design global error strategies.",
                    "spring,exceptions",
                    "ControllerAdvice best practices.",
                    baseUrl + "backend-exceptions"));

            libraryRepo.save(new LibraryItem(null, "Backend", "Hexagonal Architecture Explained",
                    "Ports & adapters architecture.",
                    "architecture,hexagonal",
                    "Domain isolation principles.",
                    baseUrl + "backend-hexagonal"));

            libraryRepo.save(new LibraryItem(null, "Backend", "Mapping Strategies with MapStruct",
                    "Automatic DTO mapping.",
                    "mapping,mapstruct",
                    "Clean mapping approaches.",
                    baseUrl + "backend-mapstruct"));

            libraryRepo.save(new LibraryItem(null, "Backend", "Pagination Best Practices",
                    "Efficient backend pagination patterns.",
                    "rest,performance",
                    "Offset vs cursor pagination.",
                    baseUrl + "backend-pagination"));

            libraryRepo.save(new LibraryItem(null, "Backend", "Optimistic Locking Explained",
                    "Avoid conflicts with version control.",
                    "jpa,locking",
                    "Solving concurrency problems.",
                    baseUrl + "backend-optimistic-locking"));

            libraryRepo.save(new LibraryItem(null, "Backend", "Caching Strategies (Redis)",
                    "Level 1 & 2 caching, TTL, invalidation.",
                    "caching,redis",
                    "Performance improvements.",
                    baseUrl + "backend-cache"));

            libraryRepo.save(new LibraryItem(null, "Backend", "Batch Processing with Spring",
                    "Handle large background jobs.",
                    "spring,batch",
                    "Chunk processing explained.",
                    baseUrl + "backend-batch"));


    /*
     |-----------------------------------
     | FRONTEND (7 items)
     |-----------------------------------
     */
            libraryRepo.save(new LibraryItem(null, "Frontend", "React Rendering Model",
                    "CSR, SSR, ISR explained.",
                    "react,rendering",
                    "Next.js rendering deep dive.",
                    baseUrl + "frontend-rendering"));

            libraryRepo.save(new LibraryItem(null, "Frontend", "State Management Best Practices",
                    "When to use Redux, Zustand, Context.",
                    "react,state",
                    "Avoid prop drilling.",
                    baseUrl + "frontend-state"));

            libraryRepo.save(new LibraryItem(null, "Frontend", "Reusable UI Components",
                    "Design scalable UI systems.",
                    "ui,components",
                    "Atomic design patterns.",
                    baseUrl + "frontend-ui"));

            libraryRepo.save(new LibraryItem(null, "Frontend", "Forms & Validation",
                    "Zod + React Hook Form best practices.",
                    "react,forms",
                    "Schema validation patterns.",
                    baseUrl + "frontend-forms"));

            libraryRepo.save(new LibraryItem(null, "Frontend", "API Fetching Patterns",
                    "fetch, axios, SWR, React Query.",
                    "api,fetching",
                    "Data synchronization techniques.",
                    baseUrl + "frontend-api"));

            libraryRepo.save(new LibraryItem(null, "Frontend", "Performance Optimization",
                    "Memo, callback, virtualization.",
                    "performance",
                    "Rendering optimizations.",
                    baseUrl + "frontend-performance"));

            libraryRepo.save(new LibraryItem(null, "Frontend", "Accessibility Essentials",
                    "WCAG rules for production apps.",
                    "a11y,frontend",
                    "Make UI accessible to all.",
                    baseUrl + "frontend-accessibility"));


    /*
     |-----------------------------------
     | SECURITY (4 items)
     |-----------------------------------
     */
            libraryRepo.save(new LibraryItem(null, "Security", "JWT Best Practices",
                    "Secure JWT handling techniques.",
                    "jwt,security",
                    "Expiration, rotation, storage.",
                    baseUrl + "security-jwt"));

            libraryRepo.save(new LibraryItem(null, "Security", "Role vs Permission Models",
                    "RBAC vs ABAC explained.",
                    "keycloak,security",
                    "Enterprise-grade authorization.",
                    baseUrl + "security-rbac"));

            libraryRepo.save(new LibraryItem(null, "Security", "OWASP Top 10",
                    "Most critical security risks.",
                    "owasp,security",
                    "Vulnerabilities & mitigations.",
                    baseUrl + "security-owasp"));

            libraryRepo.save(new LibraryItem(null, "Security", "API Security Checklist",
                    "Authentication & authorization rules.",
                    "security,api",
                    "Best practices for secure APIs.",
                    baseUrl + "security-api-checklist"));


    /*
     |-----------------------------------
     | AI & RAG (8 items)
     |-----------------------------------
     */
            libraryRepo.save(new LibraryItem(null, "AI & RAG", "Embeddings Explained",
                    "How embeddings models work.",
                    "ai,rag,embeddings",
                    "Vector math simplified.",
                    baseUrl + "ai-embeddings"));

            libraryRepo.save(new LibraryItem(null, "AI & RAG", "Chunking Strategies",
                    "Chunk size, overlap, heuristics.",
                    "rag,chunking",
                    "Improve retrieval quality.",
                    baseUrl + "ai-chunking"));

            libraryRepo.save(new LibraryItem(null, "AI & RAG", "Vector Databases Comparison",
                    "Qdrant vs Pinecone vs Weaviate.",
                    "ai,vector-db",
                    "Latency, cost, architecture.",
                    baseUrl + "ai-vdb"));

            libraryRepo.save(new LibraryItem(null, "AI & RAG", "Retriever Evaluation",
                    "How to evaluate retrieval accuracy.",
                    "rag,evaluation",
                    "Recall metrics explained.",
                    baseUrl + "ai-retriever"));

            libraryRepo.save(new LibraryItem(null, "AI & RAG", "Prompt Engineering Basics",
                    "Prompt patterns that always work.",
                    "ai,prompt",
                    "Chain-of-thought vs direct.",
                    baseUrl + "ai-prompts"));

            libraryRepo.save(new LibraryItem(null, "AI & RAG", "LLM Safety Essentials",
                    "Mitigating hallucinations.",
                    "ai,safety",
                    "Guardrails & constraints.",
                    baseUrl + "ai-safety"));

            libraryRepo.save(new LibraryItem(null, "AI & RAG", "Response Ranking",
                    "Select best answer from candidates.",
                    "rag,ranking",
                    "Scoring algorithms.",
                    baseUrl + "ai-ranking"));

            libraryRepo.save(new LibraryItem(null, "AI & RAG", "RAG System Architecture",
                    "Building industrial-grade RAG apps.",
                    "rag,architecture",
                    "Pipeline overview.",
                    baseUrl + "ai-rag-architecture"));


    /*
     |-----------------------------------
     | AWS (3 items)
     |-----------------------------------
     */
            libraryRepo.save(new LibraryItem(null, "AWS", "S3 Best Practices",
                    "Secure object storage.",
                    "aws,s3",
                    "Buckets, CORS, policies.",
                    baseUrl + "aws-s3"));

            libraryRepo.save(new LibraryItem(null, "AWS", "EC2 Deployment Guide",
                    "Deploy backend services on EC2.",
                    "aws,ec2",
                    "Security groups & autoscaling.",
                    baseUrl + "aws-ec2"));

            libraryRepo.save(new LibraryItem(null, "AWS", "IAM Essentials",
                    "Identity & access management.",
                    "aws,iam",
                    "Roles, policies, MFA.",
                    baseUrl + "aws-iam"));
        }

        /*
        |--------------------------------------------------------------------------
        | TIMELINE SEED
        |--------------------------------------------------------------------------
        */
        if (timelineRepo.count() == 0) {

            timelineRepo.save(new TimelineEntry(
                    null,
                    2025,
                    "AI Engineering",
                    "Started working on RAG, vector DBs, and LLM engineering.",
                    1
            ));

            timelineRepo.save(new TimelineEntry(
                    null,
                    2024,
                    "Backend Architecture",
                    "Built Spring Boot microservices, Keycloak authentication, workflow automation.",
                    2
            ));

            timelineRepo.save(new TimelineEntry(
                    null,
                    2023,
                    "React Frontend Development",
                    "Developed dashboards, UI components, UX improvements using React & Tailwind.",
                    3
            ));
        }

    }
}
