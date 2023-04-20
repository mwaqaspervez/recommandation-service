FROM maven:3.8.6-openjdk-18 AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package


FROM openjdk:17-jdk-slim
COPY --from=build /home/app/target/recommendation-0.0.1-SNAPSHOT.jar /usr/local/lib/recommendation-service.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/usr/local/lib/recommendation-service.jar"]
