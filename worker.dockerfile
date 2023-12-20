FROM eclipse-temurin:17-jdk-alpine
LABEL maintainer="Ogaga Uti <checkuti@gmail.com>"
LABEL version="0.0.1"
LABEL description="Worker for Spring Boot application for E-commerce"
VOLUME /tmp
COPY build/libs/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]


