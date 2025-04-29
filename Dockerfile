FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app/build

COPY . ./
RUN mvn -B package -P git-commit && \
    cp ninbot-app/target/ninbot-*.jar ninbot.jar && \
    java -Djarmode=tools -jar ninbot.jar extract --layers --launcher

FROM eclipse-temurin:21-jre-noble

LABEL maintainer="Nincodedo"
LABEL source="https://github.com/Nincodedo/Ninbot"
LABEL org.opencontainers.image.source="https://github.com/Nincodedo/Ninbot"

WORKDIR /home/app

RUN groupadd -r ninbot \
    && useradd -r -s /bin/false -g ninbot ninbot \
    && chown -hR ninbot:ninbot /home/app

COPY --chown=ninbot:ninbot --chmod=755 DockerHealthCheck.java ./
COPY --chown=ninbot:ninbot --chmod=755 --from=build /app/build/ninbot/dependencies/ ./
COPY --chown=ninbot:ninbot --chmod=755 --from=build /app/build/ninbot/spring-boot-loader/ ./
COPY --chown=ninbot:ninbot --chmod=755 --from=build /app/build/ninbot/snapshot-dependencies/ ./
COPY --chown=ninbot:ninbot --chmod=755 --from=build /app/build/ninbot/application/ ./
USER ninbot:ninbot
HEALTHCHECK --start-period=20s CMD java /home/app/DockerHealthCheck.java 2>&1 || exit 1
EXPOSE 8080
ENTRYPOINT ["java"]
CMD ["-Xss512k", "-Xmx256M", "--enable-preview", "org.springframework.boot.loader.launch.JarLauncher"]
