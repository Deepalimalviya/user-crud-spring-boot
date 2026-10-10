FROM eclipse-temurin:21-jre
WORKDIR /app
ARG JAR_FILE=target/user-crud-1.0.0.jar
COPY ${JAR_FILE} app.jar
RUN useradd --system --uid 10001 appuser
USER 10001
EXPOSE 8080
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "/app/app.jar"]