FROM maven:3.9.6-eclipse-temurin-21 AS build

COPY . ./
RUN mvn -B package -P git-commit
RUN cp ninbot-app/target/ninbot-*.jar ninbot.jar
RUN java -Djarmode=tools -jar ninbot.jar extract

FROM eclipse-temurin:21-jre-noble

LABEL maintainer="Nincodedo"
LABEL source="https://github.com/Nincodedo/Ninbot"
LABEL org.opencontainers.image.source="https://github.com/Nincodedo/Ninbot"
RUN groupadd -r ninbot \
    && useradd -r -s /bin/false -g ninbot ninbot \
    && mkdir -p /app/ninbot \
    && chown -hR ninbot:ninbot /app
WORKDIR /app
COPY --chown=ninbot:ninbot --chmod=755 DockerHealthCheck.java /app
COPY --chown=ninbot:ninbot --chmod=755 --from=build ninbot/lib ./ninbot/lib
COPY --chown=ninbot:ninbot --chmod=755 --from=build ninbot/ninbot.jar ./ninbot/ninbot.jar
USER ninbot:ninbot
HEALTHCHECK --start-period=20s CMD java /app/DockerHealthCheck.java 2>&1 || exit 1
EXPOSE 8080
ENTRYPOINT ["java"]
CMD ["-Xss512k", "-Xmx256M", "--enable-preview", "-jar", "/app/ninbot/ninbot.jar"]
