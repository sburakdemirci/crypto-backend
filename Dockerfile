FROM openjdk:19-jdk-alpine

WORKDIR /app

COPY target/*.jar /app/crypto.jar
#COPY src/main/resources/application.yml /app/application.yml

# Set the command to run the Spring Boot application
CMD ["java", "-Dserver.port=${PORT}", "-jar", "crypto.jar"]
