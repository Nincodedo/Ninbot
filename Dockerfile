FROM maven:3.8.5-eclipse-temurin-18 AS build
COPY pom.xml .
COPY .mvn ./.mvn
RUN mvn -B dependency:resolve
COPY src ./src
COPY .git ./.git
RUN mvn package -P git-commit

FROM openjdk:18-jdk-slim-buster

LABEL maintainer="Nincodedo"
LABEL source="https://github.com/Nincodedo/Ninbot"
LABEL org.opencontainers.image.source = "https://github.com/Nincodedo/Ninbot"
RUN mkdir /app
RUN groupadd -r ninbot && useradd -r -s /bin/false -g ninbot ninbot
WORKDIR /app
COPY --chown=ninbot:ninbot --from=build target/ninbot*.jar /app/ninbot.jar
COPY DockerHealthCheck.java /app
USER ninbot
HEALTHCHECK --start-period=20s CMD java /app/DockerHealthCheck.java 2>&1 || exit 1
EXPOSE 8080
ENTRYPOINT ["java"]
CMD ["-Xss512k", "-Xmx256M", "--enable-preview", "-jar", "/app/ninbot.jar"]
