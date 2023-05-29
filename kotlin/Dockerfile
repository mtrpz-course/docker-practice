FROM gradle:7-jdk11 AS build
COPY --chown=gradle:gradle . /app
WORKDIR /app
RUN gradle buildFatJar --no-daemon

FROM openjdk:11
EXPOSE 8080:8080
RUN mkdir /server
COPY --from=build /app/build/libs/*.jar /server/kotlin-all.jar
ENTRYPOINT ["java","-jar","/server/kotlin-all.jar"]