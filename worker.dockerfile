# FROM eclipse-temurin:21_35-jdk-alpine
# LABEL maintainer="Ogaga Uti <checkuti@gmail.com>"
# LABEL version="0.0.1"
# LABEL description="Worker for Spring Boot application for E-commerce"
# VOLUME /tmp
# COPY build/libs/*.jar app.jar
# ADD src/config.yml config.yml
# ENTRYPOINT ["java","-jar","/app.jar"]


FROM gradle:8.4 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle clean build --no-daemon 

FROM eclipse-temurin:21_35-jdk-alpine
RUN mkdir /app
COPY --from=build /home/gradle/src/build/libs/*.jar /app/app.jar
ENTRYPOINT ["java", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseCGroupMemoryLimitForHeap", "-Djava.security.egd=file:/dev/./urandom","-jar","/app/app.jar"]