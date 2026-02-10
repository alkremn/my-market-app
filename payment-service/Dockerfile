FROM eclipse-temurin:21-alpine AS build
WORKDIR /app

# Copy parent POM and module POMs
COPY pom.xml .
COPY payment-api/pom.xml payment-api/
COPY payment-service/pom.xml payment-service/
COPY market-app/pom.xml market-app/

# Copy Maven wrapper
COPY mvnw .
COPY .mvn .mvn
RUN chmod +x mvnw

RUN ./mvnw dependency:go-offline -pl payment-service -am -B

# Copy source files
COPY payment-api/src payment-api/src
COPY payment-service/src payment-service/src

RUN ./mvnw -pl payment-service -am clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/payment-service/target/*.jar payment-service.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "payment-service.jar"]