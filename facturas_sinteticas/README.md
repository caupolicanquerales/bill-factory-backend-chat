# Facturas Sintéticas (facturas_sinteticas)

Small Spring Boot backend that generates synthetic invoice data and interacts with Qdrant/vector store.

**Project Overview**
- Java + Spring Boot reactive project.
- Uses Reactor Netty, WebClient, and Server-Sent Events (SSE) in some flows.

**Prerequisites**
- Java 17+ (or compatible JDK used in project)
- Maven (project includes `mvnw` wrapper so Maven isn't strictly required locally)

**Build**
Run from project root:

```bash
./mvnw clean package
```

**Run**
Run with the wrapper or your installed Maven:

```bash
./mvnw spring-boot:run
# or
java -jar target/facturas_sinteticas-0.0.1-SNAPSHOT.jar
```

**Tests**

```bash
./mvnw test
```

**Important files & packages**
- `src/main/java/com/capo/facturas_sinteticas/FacturasSinteticasApplication.java`: application entry point.
- `src/main/java/com/capo/facturas_sinteticas/controller/GenerationSyntheticDataController.java`: main controller for generation endpoints.
- `src/main/java/com/capo/facturas_sinteticas/controller/QdrantController.java`: controller related to Qdrant interactions.
- `src/main/java/com/capo/facturas_sinteticas/service/`: core services (RAG, storing files, prompt execution, SSE service, converters).
- `src/main/resources/application.properties`: runtime configuration.

**Configuration**
- Edit `application.properties` to configure ports, external endpoints, and credentials used by the application.

**Development notes**
- The project is reactive; many services use Reactor types and non-blocking WebClient.
- SSE streams are implemented under `service/sse`.
- Utility classes available under `utils/`.

**Contributing**
- Open an issue or submit a pull request.
- Keep changes small and focused; include tests when adding features.

**License**
- None specified. Add a LICENSE file if needed.
