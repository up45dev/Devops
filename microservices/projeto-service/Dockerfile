FROM openjdk:21-jdk-slim

WORKDIR /app

COPY target/projeto-service-*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "/app/app.jar"]