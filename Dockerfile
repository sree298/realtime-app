# Use lightweight Java 17 runtime
FROM eclipse-temurin:17-jdk

# Copy built JAR into the container
COPY target/realtime-app.jar /app.jar

# Expose the default Spring Boot port
EXPOSE 8080

# Run the app
ENTRYPOINT ["java", "-jar", "/app.jar"]
