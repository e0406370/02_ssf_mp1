# STAGE 1 : BUILD THE SOURCE CODE IMAGE

FROM maven:3-eclipse-temurin-21 AS builder

ARG APP_DIR=/app

WORKDIR ${APP_DIR}

COPY mvnw .
COPY mvnw.cmd .
COPY pom.xml .

COPY .mvn .mvn
COPY src src

RUN mvn package -Dmaven.test.skip=true

#################################################################

# STAGE 2 : BUILD THE SECONDARY IMAGE

FROM openjdk:21-jdk

ARG APP_DIR=/app

WORKDIR ${APP_DIR}

COPY --from=builder /app/target/mp1-0.0.1-SNAPSHOT.jar app.jar

ENV PORT=3050
ENV SERVER_SERVLET_SESSION_TIMEOUT=1800

EXPOSE ${PORT}

ENTRYPOINT SERVER_PORT=${PORT} java -jar app.jar