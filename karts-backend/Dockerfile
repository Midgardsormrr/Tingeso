FROM openjdk:17
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} karts-backend.jar
EXPOSE 8090
ENTRYPOINT ["java","-jar","/karts-backend.jar"]

