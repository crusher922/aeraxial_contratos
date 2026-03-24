# ============================================================================
# Aeraxial Contratos — Dockerfile (multi-stage)
# ============================================================================
# Ubicación: aeraxial_contratos/docker/Dockerfile
# Contexto:  aeraxial_contratos/ (raíz, donde está pom.xml y mvnw)
#
# Stage 1 → Maven build (compila el .jar)
# Stage 2 → JVM runtime (ejecuta Quarkus)
# ============================================================================

# ── Stage 1: Build con Maven ─────────────────────────────────────────────────
FROM eclipse-temurin:21-jdk AS builder

WORKDIR /build

# Copiar Maven wrapper y pom (cache de dependencias)
COPY ../mvnw ./
COPY ../.mvn ./.mvn/
COPY ../pom.xml ./

# Dar permisos al wrapper
RUN chmod +x mvnw

# Descargar dependencias (cacheado si pom.xml no cambia)
RUN ./mvnw dependency:go-offline -B

# Copiar código fuente
COPY ../src ./src/

# Build Quarkus (skip tests para rapidez en Docker)
RUN ./mvnw package -DskipTests -B

# ── Stage 2: Runtime JVM ─────────────────────────────────────────────────────
FROM eclipse-temurin:21-jre

ENV LANGUAGE='en_US:en'

WORKDIR /deployments

# Copiar Quarkus app desde el builder
COPY --from=builder /build/target/quarkus-app/lib/ ./lib/
COPY --from=builder /build/target/quarkus-app/*.jar ./
COPY --from=builder /build/target/quarkus-app/app/ ./app/
COPY --from=builder /build/target/quarkus-app/quarkus/ ./quarkus/

# Instalar curl para healthcheck
RUN apt-get update && apt-get install -y --no-install-recommends curl && rm -rf /var/lib/apt/lists/*

EXPOSE 8082

ENV JAVA_OPTS_APPEND="-Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager"
ENV JAVA_APP_JAR="/deployments/quarkus-run.jar"

ENTRYPOINT ["java", "-jar", "/deployments/quarkus-run.jar"]
