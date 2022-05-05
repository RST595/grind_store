FROM openjdk:17-ea-5-jdk-alpine

VOLUME /tmp

ADD /grind_store.jar grind_store.jar

ENTRYPOINT ["java", "-jar", "/grind_store.jar"]
