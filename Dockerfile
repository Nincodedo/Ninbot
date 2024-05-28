FROM maven:3.9.6-eclipse-temurin-21 AS build

ARG open_telemetry_version=v2.4.0
RUN wget "https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/$open_telemetry_version/opentelemetry-javaagent.jar"
COPY . ./
RUN mvn -B package -P git-commit
RUN cp ninbot-app/target/ninbot-*.jar ninbot.jar
RUN java -Djarmode=tools -jar ninbot.jar extract

FROM eclipse-temurin:21-jre-jammy

LABEL maintainer="Nincodedo"
LABEL source="https://github.com/Nincodedo/Ninbot"
LABEL org.opencontainers.image.source="https://github.com/Nincodedo/Ninbot"
RUN groupadd -r ninbot && useradd -r -s /bin/false -g ninbot ninbot
RUN mkdir -p /app/ninbot && chown -hR ninbot:ninbot /app
WORKDIR /app
COPY --chown=ninbot:ninbot DockerHealthCheck.java /app
COPY --chown=ninbot:ninbot --from=build opentelemetry-javaagent.jar /app/opentelemetry-javaagent.jar
COPY --chown=ninbot:ninbot --from=build ninbot/lib ./ninbot/lib
COPY --chown=ninbot:ninbot --from=build ninbot/ninbot.jar ./ninbot/ninbot.jar
USER ninbot:ninbot
HEALTHCHECK --start-period=20s CMD java /app/DockerHealthCheck.java 2>&1 || exit 1
EXPOSE 8080
ENTRYPOINT ["java"]
CMD ["-javaagent:/app/opentelemetry-javaagent.jar", "-Xss512k", "-Xmx256M", "--enable-preview", "-jar", "/app/ninbot/ninbot.jar"]
