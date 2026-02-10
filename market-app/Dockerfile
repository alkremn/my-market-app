FROM eclipse-temurin:21-alpine AS build
WORKDIR /app

# Copy parent POM and module POMs
COPY pom.xml .
COPY payment-api/pom.xml payment-api/
COPY market-app/pom.xml market-app/
COPY payment-service/pom.xml payment-service/

# Copy Maven wrapper
COPY mvnw .
COPY .mvn .mvn
RUN chmod +x mvnw

RUN ./mvnw dependency:go-offline -pl market-app -am -B

COPY payment-api/src payment-api/src
COPY market-app/src market-app/src

RUN ./mvnw -pl market-app -am clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/market-app/target/*.jar market-app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "market-app.jar"]