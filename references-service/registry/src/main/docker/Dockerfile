FROM openjdk:8-jre

VOLUME /tmp

ENV SERVER_PORT=8761

EXPOSE $SERVER_PORT

ADD maven/registry-*.jar /app.jar

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]