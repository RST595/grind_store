FROM openjdk:11-jdk-slim

VOLUME /tmp

ADD /grind_store.jar grind_store.jar

ENTRYPOINT ["java", "-jar", "/grind_store.jar"]
