# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

Java 17, Spring Boot 3.3.4, Spring AI 1.0.3, built with the Maven wrapper.

- Build: `./mvnw clean package -DskipTests` (the Dockerfile does the same, then runs `java -jar target/portfolio-backend-0.0.1-SNAPSHOT.jar`)
- Run: `./mvnw spring-boot:run`
- Test: `./mvnw test`; single test: `./mvnw test -Dtest=DevBrainApplicationTests`
- No linter is configured.

The only test is a `@SpringBootTest` `contextLoads` test. It needs the env vars below and a reachable Postgres and Qdrant.

## Configuration

- Env vars are read from the environment (a git-ignored `.env` holds local values): `SPRING_DATASOURCE_URL/USERNAME/PASSWORD`, `OPENAI_API_KEY` (embeddings only, `text-embedding-3-small`), `GROQ_API_KEY`, `GROQ_MODEL` (default `openai/gpt-oss-20b`), `SPRING_AI_VECTORSTORE_QDRANT_URL`, `SPRING_AI_VECTORSTORE_QDRANT_API_KEY`, `GITHUB_TOKEN`, `RAG_ADMIN_TOKEN` (no default, maps to `app.rag.admin-token`), `SERVER_PORT` (default 8080).
- `app.llm.max-answer-chars` (set in `application.yaml`, default 600, not an env var): hard cap on LLM answer length; `AnswerLimiter` truncates longer answers at a sentence boundary and appends a follow-up invitation (en/fr/ar).
- `app.rag.unavailable-message` (set in `application.yaml`, not an env var): the answer `RagService` returns, with HTTP 200 and empty sources, when the vector search fails (e.g. Qdrant unreachable).
- `application.yaml` uses `ddl-auto: validate`; `application-prod.yml` uses `update` and hardcodes the Qdrant URL.
- `application-index.yml` at the repo root is untracked and not on the classpath; it is a stray alternate config (transformers embeddings, host/port Qdrant).
- `spring.main.lazy-initialization` is `true`, so bean wiring errors can surface on first use rather than at startup.

## Architecture

Package `com.devBrain.backend` is layered controller → service → repository. JPA/Postgres holds only `Profile`, `Project` and `TimelineEntry`; the knowledge base and the PDF library live in a GitHub repo (`houdajh/rag-data`), not the DB.

RAG pipeline (spans several files):
1. `POST /api/rag/admin/reindex` (requires an `X-Admin-Token` header matching `app.rag.admin-token`, else 401, enforced by `AdminTokenInterceptor` on `/api/rag/admin/**`) → `RagIndexer` lists and downloads markdown via `GitHubContentService`, chunks it (1200 chars, 200 overlap), and calls `QdrantSearchService.index`. An in-memory `indexed` flag makes later calls a no-op until the process restarts.
2. `RagService.ask` runs a Qdrant similarity search for `topK` chunks, concatenates them as context, and calls `LlmService.askGroq(question, context)`.
3. `LlmService` uses Groq through the OpenAI-compatible API. `GroqConfig` builds a dedicated `groqChatClient` (base URL `https://api.groq.com/openai`); the OpenAI starter is used only for embeddings. It has a persona system prompt ("You are Houda Jouhar", first person, anti-hallucination rules) and makes an extra LLM call per question to detect the language (en/fr/ar).
4. `QdrantConfig` builds the vector store by hand over gRPC (port 6334, TLS) from the URL host. It sets `initializeSchema(true)`, overriding `initialize-schema: false` in the yaml.

`app.rag.enabled` gates `RagService` and `RagController` (`/api/rag/ask`). `AskController` (`POST /api/ask`) takes `RagService` as optional and falls back to chat-only Groq when the flag is false.

`LibraryService` / `LibraryController` browse PDF folders in the GitHub repo through the GitHub REST API (`app.library.github.*`). `WebConfig` allows CORS from any origin on `/api/**`. `DebugPropsRunner` prints Qdrant properties at startup.

## Conventions

- Controllers stay thin: no business logic, delegate to a service.
- Never return a JPA entity from a controller — map to a DTO.
- Constructor injection only, never `@Autowired` on fields.
- No secret, URL, API key or model name hardcoded in Java — add a property
  and read it from the environment, like the existing config does.
- New config property: document it in the Configuration section above.

## Do not touch

- `.env` — real API keys. Never read it, never print it, never echo its values.
- `application-prod.yml` — ask me before changing anything in it.
- `target/` — build output.

## Verifying work

Tests need a live Postgres and Qdrant plus the env vars, so they usually
cannot run locally. Verify with `./mvnw -q clean compile` instead and say
explicitly what you could not test.