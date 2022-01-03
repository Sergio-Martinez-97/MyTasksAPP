FROM openjdk:11.0.10-jdk
ARG JAR_FILE=target/everis-dar-mytasks-ms.jar
ADD ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]