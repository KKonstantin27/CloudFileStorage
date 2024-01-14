# syntax=docker/dockerfile:1

FROM gradle:latest AS BUILD
WORKDIR /usr/app/
COPY . .
RUN gradle build -x test

FROM amazoncorretto:17-alpine
ENV JAR_NAME=cloudFileStorage-1.0.jar
ENV APP_HOME=/usr/app/
ENV TESTCONTAINERS_HOST_OVERRIDE=tcp://host.docker.internal:2375
WORKDIR $APP_HOME
COPY --from=BUILD $APP_HOME .
EXPOSE 8080
ENTRYPOINT exec java -jar $APP_HOME/build/libs/$JAR_NAME