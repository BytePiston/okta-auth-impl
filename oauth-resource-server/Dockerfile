FROM openjdk:17

ADD target/oauth-resource-server-1.0.0.jar oauth-resource-server.jar

EXPOSE 8090

ENTRYPOINT ["java", "-jar", "/oauth-resource-server.jar"]
