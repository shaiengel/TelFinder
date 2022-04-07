FROM openjdk:8-jre-alpine3.9

EXPOSE 8080
WORKDIR /app

RUN mkdir -p /app/config
COPY ./config/* /app/config/
COPY TelFinder-1.0-SNAPSHOT.jar /app/

CMD ["java", "-jar", "TelFinder-1.0-SNAPSHOT.jar"]
