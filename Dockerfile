FROM openjdk:17

VOLUME /tmp

ADD /grind_store.jar grind_store.jar

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/grind_store.jar"]
