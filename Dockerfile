# STAGE 1 : BUILD THE SOURCE CODE IMAGE

FROM maven:3-eclipse-temurin-21 AS builder

ARG APP_DIR=/app

WORKDIR ${APP_DIR}

COPY mp1/mvnw .
COPY mp1/mvnw.cmd .
COPY mp1/pom.xml .

COPY mp1/.mvn .mvn
COPY mp1/src src

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