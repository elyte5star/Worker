FROM eclipse-temurin:21_35-jdk-alpine
LABEL maintainer="Ogaga Uti <checkuti@gmail.com>"
LABEL version="0.0.1"
LABEL description="Worker for Spring Boot application for E-commerce"
VOLUME /tmp
COPY build/libs/*.jar app.jar
ENTRYPOINT ["java","-XX:+UnlockExperimentalVMOptions","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]


