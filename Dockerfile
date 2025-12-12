FROM eclipse-temurin:17-jre

WORKDIR /app

# Copy everything
COPY . .

# Build WITHOUT tests
RUN ./mvnw clean package -DskipTests

# Run the app
ENTRYPOINT ["java", "-jar", "target/portfolio-backend-0.0.1-SNAPSHOT.jar"]
