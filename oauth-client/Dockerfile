FROM openjdk:17

ADD target/oauth-client-1.0.0.jar oauth-client.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/oauth-client.jar"]
