FROM openjdk:17-oracle 
FROM ubuntu:18.04
FROM maven:3.8.3-openjdk-17

#WORKDIR /home/oleksandr/Projects/other/company_prices
WORKDIR $(pwd)

# Copy the Maven project file & Download the project dependencies
#COPY pom.xml .
#RUN mvn dependency:go-offline

# Copy the application source code
#COPY src ./src
#COPY src/main/resources/application.yaml application.yaml

#RUN mvn clean install -DskipTests

#COPY target/company_prices-1.jar company_prices_1.jar


COPY pom.xml .
COPY src ./src
COPY src/main/resources/application.yaml application.yaml

RUN mvn clean package
RUN mvn clean package spring-boot:repackage

CMD ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "target/company_prices-1.jar"]


#CMD ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "company_prices_1.jar", "--spring.config.location=file:application.yml"]

#CMD ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "company_prices_1.jar"]
