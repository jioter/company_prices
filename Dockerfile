FROM openjdk:17-oracle 
FROM ubuntu:18.04
FROM maven:3.8.3-openjdk-17

WORKDIR app

COPY pom.xml .
COPY src ./src

RUN mvn clean package spring-boot:repackage

CMD ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "target/company-prices-1.jar"]

