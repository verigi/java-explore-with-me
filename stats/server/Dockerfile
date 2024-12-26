FROM openjdk:21-jdk-slim
WORKDIR /app
COPY target/statistics-server-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 9090
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app.jar"]