FROM openjdk:21-jdk-slim

WORKDIR /app

COPY target/admin-ui-service-*.jar app.jar

EXPOSE 8090

ENTRYPOINT ["java", "-jar", "/app/app.jar"]