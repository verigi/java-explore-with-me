FROM openjdk:21-jdk-slim

WORKDIR /app

COPY target/statistics-server-0.0.1-SNAPSHOT.jar app.jar

ENV SPRING_DATASOURCE_URL=jdbc:postgresql://stats-db:5432/stats-db
ENV SPRING_DATASOURCE_USERNAME=stats
ENV SPRING_DATASOURCE_PASSWORD=stats
ENV SERVER_PORT=9090

EXPOSE 9090

ENTRYPOINT ["java", "-jar", "app.jar"]