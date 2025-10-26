FROM amazoncorretto:21.0.6-al2023-headless
WORKDIR /app
COPY target/stats-server-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app/app.jar"]
EXPOSE 9090