FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

RUN chmod +x mvnw && ./mvnw package -DskipTests

EXPOSE 8082

CMD ["java", "-jar", "target/quarkus-app/quarkus-run.jar"]