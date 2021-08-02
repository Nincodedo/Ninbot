FROM maven:3.8.1-openjdk-17 AS build
COPY pom.xml .
COPY .mvn ./.mvn
RUN mvn -B dependency:resolve
COPY src ./src
COPY .git ./.git
RUN mvn package -P git-commit

FROM adoptopenjdk/openjdk16:debianslim-jre
LABEL maintainer="Nincodedo"
LABEL source="https://github.com/Nincodedo/Ninbot"
RUN mkdir /app
RUN groupadd -r ninbot && useradd -r -s /bin/false -g ninbot ninbot
WORKDIR /app
COPY --chown=ninbot:ninbot --from=build target/ninbot*.jar /app/ninbot.jar
RUN apt-get update && apt-get install curl -y --no-install-recommends && apt-get clean && rm -rf /var/lib/apt/lists/*
USER ninbot
HEALTHCHECK --start-period=20s CMD curl --fail --silent http://localhost:8080/actuator/health 2>&1 | grep UP || exit 1
EXPOSE 8080
ENTRYPOINT ["java"]
CMD ["-Xss512k", "-Xmx256M", "--enable-preview", "-jar", "/app/ninbot.jar"]
