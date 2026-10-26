---
name: verify-backend
description: Verify devBrain backend changes compile and follow project rules. Use after editing any Java, yaml or pom file, before reporting the work as done.
---

# Verify backend

1. `./mvnw -q clean compile` — must succeed.
2. `git diff` — review your own changes:
    - no entity returned from a controller
    - no key, URL or model name hardcoded
    - new property documented in CLAUDE.md
3. State clearly which behaviour you could NOT verify, since tests need
   a live Postgres and Qdrant.