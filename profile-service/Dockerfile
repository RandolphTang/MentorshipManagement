FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY target/*.jar app.jar
COPY src/main/resources/application.yml /app/application.yml

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]