FROM maven:3.8.6-eclipse-temurin-19 AS build

COPY . ./
RUN mvn -B package -P git-commit
RUN cp ninbot-app/target/ninbot-*.jar ninbot.jar
RUN java -Djarmode=layertools -jar ninbot.jar extract
RUN wget https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v1.20.2/opentelemetry-javaagent.jar

FROM eclipse-temurin:19-jre-focal

LABEL maintainer="Nincodedo"
LABEL source="https://github.com/Nincodedo/Ninbot"
LABEL org.opencontainers.image.source = "https://github.com/Nincodedo/Ninbot"
RUN groupadd -r ninbot && useradd -r -s /bin/false -g ninbot ninbot
RUN mkdir /app && chown -hR ninbot:ninbot /app
WORKDIR /app
COPY --chown=ninbot:ninbot --from=build dependencies/ ./
COPY --chown=ninbot:ninbot --from=build spring-boot-loader/ ./
COPY --chown=ninbot:ninbot --from=build snapshot-dependencies/ ./
COPY --chown=ninbot:ninbot --from=build application/ ./
COPY --chown=ninbot:ninbot DockerHealthCheck.java /app
COPY --chown=ninbot:ninbot --from=build opentelemetry-javaagent.jar /app/opentelemetry-javaagent.jar
USER ninbot:ninbot
HEALTHCHECK --start-period=20s CMD java /app/DockerHealthCheck.java 2>&1 || exit 1
EXPOSE 8080
ENTRYPOINT ["java"]
CMD ["-javaagent:/app/opentelemetry-javaagent.jar", "-Xss512k", "-Xmx256M", "--enable-preview", "org.springframework.boot.loader.JarLauncher"]
