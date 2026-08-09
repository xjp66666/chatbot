FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /workspace

COPY pom.xml .
RUN mvn -B -DskipTests dependency:go-offline

COPY src ./src
RUN mvn -B -DskipTests package \
    && cp target/*.jar /app.jar

FROM eclipse-temurin:21-jre

WORKDIR /app
COPY --from=build /app.jar ./app.jar

EXPOSE 8080

USER 1000:1000

ENTRYPOINT ["java", "-jar", "app.jar"]
