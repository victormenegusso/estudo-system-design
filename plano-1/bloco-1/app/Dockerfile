# Build stage
# Usando Java 21 LTS para garantir compatibilidade com Gradle e Spring Boot
FROM eclipse-temurin:21-jdk-jammy AS build
RUN apt-get update && apt-get install -y findutils
COPY . /app
WORKDIR /app
# Garantir permissão de execução no gradlew
RUN chmod +x gradlew
# Executar o build sem daemon para economizar recursos no container de build
RUN ./gradlew bootJar --no-daemon

# Run stage
FROM eclipse-temurin:21-jre-jammy
EXPOSE 8080
COPY --from=build /app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
