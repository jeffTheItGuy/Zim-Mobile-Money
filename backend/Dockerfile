FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Install Maven + file watcher utilities
RUN apk add --no-cache maven inotify-tools

# Copy POM first for dependency caching
COPY pom.xml ./
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

EXPOSE 8080

# Run Spring Boot in dev mode with auto-reload and debug agent
CMD ["mvn", "spring-boot:run", "-Dspring-boot.run.profiles=dev", "-Dspring-boot.run.jvmArguments=-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"]
