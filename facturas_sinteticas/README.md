# Facturas Sintéticas

Clear, concise documentation for the Facturas Sintéticas backend.

**Description:**
- Small Spring Boot (WebFlux) backend that generates synthetic invoice-related data and supports RAG (retrieval-augmented generation) and vector-store interactions (examples use Qdrant).

**Highlights:**
- Reactive: built with Spring WebFlux and Reactor for non-blocking IO.
- SSE: Server-Sent Events are used for streaming responses in some flows.
- Modular services: file converters, RAG ingestion, prompt execution, SSE service, and utility helpers.

**Tech stack**
- Java 17
- Spring Boot (WebFlux)
- Reactor (Flux/Mono)
- Maven (wrapper `mvnw` included)

**Quick start (build & run)**
From the project root run:

```bash
./mvnw clean package
./mvnw spring-boot:run
# or run the built jar:
java -jar target/facturas_sinteticas-0.0.1-SNAPSHOT.jar
```

**Run tests and generate coverage**
Run unit tests and produce a JaCoCo report:

```bash
./mvnw -B org.jacoco:jacoco-maven-plugin:0.8.8:prepare-agent test org.jacoco:jacoco-maven-plugin:0.8.8:report
```

Reports are generated at `target/site/jacoco/index.html` and `target/site/jacoco/jacoco.xml`.

**Project layout (important files)**
- `src/main/java/com/capo/facturas_sinteticas/FacturasSinteticasApplication.java`: application entry point
- `src/main/java/com/capo/facturas_sinteticas/controller/GenerationSyntheticDataController.java`: main generation endpoints
- `src/main/java/com/capo/facturas_sinteticas/controller/QdrantController.java`: Qdrant/file ingestion endpoints
- `src/main/java/com/capo/facturas_sinteticas/service/`: core services (RAG, converters, SSE, prompt execution)
- `src/main/java/com/capo/facturas_sinteticas/configurations/`: HTTP client and Netty configuration
- `src/main/resources/application.properties`: runtime configuration

**Testing & coverage goals**
- Unit tests: JUnit 5 + Mockito + reactor-test (StepVerifier)
- Current workflow includes a comprehensive unit-test suite; aim for >=75% line coverage. Run the JaCoCo command above to measure coverage.

**Development notes**
- When modifying reactive code, prefer non-blocking APIs and Reactor test utilities (StepVerifier).
- The `FilePartConverterService` centralizes DataBuffer->byte[]/String conversion—use it to avoid duplication.

**Contributing**
- Open issues or PRs. Provide tests for new behavior and keep changes focused.

**License**
- No license file included. Add `LICENSE` if you want to publish under a specific license.

If you want, I can add a short architecture diagram, examples of API calls, or create curl snippets for key endpoints. Which would you like next?
