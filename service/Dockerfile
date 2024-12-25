FROM openjdk:21-jdk-slim

WORKDIR /app

COPY target/ewm-service-0.0.1-SNAPSHOT.jar app.jar

ENV SPRING_DATASOURCE_URL=jdbc:postgresql://ewm-db:5432/ewm-db
ENV SPRING_DATASOURCE_USERNAME=ewm
ENV SPRING_DATASOURCE_PASSWORD=ewm
ENV SERVER_PORT=8080

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]