FROM openjdk:11 AS build

ARG FILE_JAR=target/*.jar

COPY ${FILE_JAR} tenant-api.jar

ENTRYPOINT ["java", "-jar", "tenant-api.jar"]

EXPOSE 7878