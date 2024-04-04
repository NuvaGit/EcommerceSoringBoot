# Start with a Maven image that includes Java 17
FROM maven:3.8.4-openjdk-17 as build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn dependency:go-offline

RUN mvn package -DskipTests

FROM openjdk:17-alpine

COPY --from=build /app/target/*.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
