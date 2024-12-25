FROM openjdk:21-jdk-slim
VOLUME /tmp
ARG JAR_FILE=target/statistics-server-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app.jar"]