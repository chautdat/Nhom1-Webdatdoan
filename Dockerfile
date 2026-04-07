FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY mvnw ./
COPY .mvn .mvn
COPY pom.xml ./
COPY src ./src

RUN chmod +x mvnw && ./mvnw -q -DskipTests package

FROM eclipse-temurin:21-jre

WORKDIR /app

ENV SPRING_PROFILES_ACTIVE=docker

RUN mkdir -p /app/uploads /app/logs

COPY --from=build /app/target/*.jar /app/app.jar

EXPOSE 3000

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
