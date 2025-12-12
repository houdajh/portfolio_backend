FROM eclipse-temurin:17-jdk

WORKDIR /app

# Copy everything
COPY . .

# 🔥 CRITICAL: fix permissions BEFORE running mvnw
RUN chmod +x mvnw

# Build without tests
RUN ./mvnw clean package -DskipTests

EXPOSE 8080

CMD ["java", "-jar", "target/portfolio-backend-0.0.1-SNAPSHOT.jar"]
