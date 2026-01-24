# =========================
# Build stage
# =========================
FROM gradle:8.7-jdk21 AS build
WORKDIR /app

# Copy Gradle config first for caching
COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle

# Download dependencies (cached layer)
RUN ./gradlew dependencies --no-daemon

# Copy source
COPY src ./src

# Build bootable JAR
RUN ./gradlew clean bootJar --no-daemon

# =========================
# Runtime stage
# =========================
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy ONLY the bootable jar
COPY --from=build /app/build/libs/cart-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENV SPRING_PROFILES_ACTIVE=dev

ENTRYPOINT ["java","-jar","app.jar"]

#docker build -t cart-market-backend .