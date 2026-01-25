FROM eclipse-temurin:21-alpine AS build
WORKDIR /app

# Copy parent POM and module POMs
COPY pom.xml .
COPY market-app/pom.xml market-app/
COPY payment-service/pom.xml payment-service/

# Copy Maven wrapper
COPY mvnw .
COPY .mvn .mvn

# Copy source for market-app only
COPY market-app/src market-app/src

RUN chmod +x mvnw && ./mvnw -pl market-app -am clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/market-app/target/*.jar market-app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "market-app.jar"]