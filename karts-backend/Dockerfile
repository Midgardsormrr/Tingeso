FROM openjdk:17
ARG JAR_FILE=./target/*.jar
COPY ${JAR_FILE} karts-backend.jar
ENTRYPOINT ["java","-jar","/karts-backend.jar"]
EXPOSE 8080
