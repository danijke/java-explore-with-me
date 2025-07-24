FROM maven:3.9.7-eclipse-temurin-21 AS build
WORKDIR /app

COPY . .

RUN mvn -B clean package -pl stats-service/stats-server -am -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/stats-service/stats-server/target/stats-server-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 9090
ENTRYPOINT ["java", "-jar", "/app/app.jar"]