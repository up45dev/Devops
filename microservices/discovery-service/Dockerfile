FROM openjdk:21-jdk-slim

WORKDIR /app

COPY target/discovery-service-*.jar app.jar

EXPOSE 8761

ENTRYPOINT ["java", "-jar", "/app/app.jar"]