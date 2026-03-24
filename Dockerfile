FROM maven:3.9.9-eclipse-temurin-21

WORKDIR /app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

RUN mvn package -DskipTests

EXPOSE 8082

CMD ["java", "-jar", "target/quarkus-app/quarkus-run.jar"]