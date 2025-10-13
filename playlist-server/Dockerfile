FROM openjdk:23
WORKDIR /app

COPY target/*.jar /app/app.jar

EXPOSE 8081

CMD ["java", "-jar", "app.jar"]
