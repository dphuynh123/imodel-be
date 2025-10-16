# Use a lightweight base image with JDK 17 (or 21 if you’re using it)
FROM eclipse-temurin:21-jdk-alpine AS builder

# Set working directory
WORKDIR /app

# Copy the JAR file into the container
COPY target/imodel-0.0.1-SNAPSHOT.jar app.jar

# Define environment variables (you can override these in docker-compose or docker run)
ENV JWT_SECRET_KEY=JWT_SECRET_KEY
ENV GEMINI_API_KEY=GEMINI_API_KEY

# Expose the Spring Boot default port
EXPOSE 8080

# Run the application with system properties
ENTRYPOINT ["sh", "-c", "java -DJWT_SECRET_KEY=$JWT_SECRET_KEY -DGEMINI_API_KEY=$GEMINI_API_KEY -jar app.jar"]
