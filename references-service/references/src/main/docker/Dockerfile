FROM openjdk:8-jre

VOLUME /tmp

ENV SERVER_PORT=8080

EXPOSE $SERVER_PORT

ADD maven/references-*.jar /app.jar

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]