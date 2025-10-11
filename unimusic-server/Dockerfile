FROM openjdk:23
WORKDIR /app

COPY target/*.jar /app/app.jar
COPY musicas /app/musicas

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]