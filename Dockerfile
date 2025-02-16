FROM maven:3.8.7-openjdk-18-slim AS build

WORKDIR /app

COPY pom.xml .

RUN mvn dependency:go-offline

COPY src ./src

RUN mvn clean package 

FROM openjdk:17-alpine

WORKDIR /app

COPY --from=build /app/target/PriceAnomalyDetection-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
