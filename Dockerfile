FROM maven:3.9.9-eclipse-temurin-24 AS build

WORKDIR /app/build

COPY . ./
RUN mvn -B package -P git-commit
RUN cp ninbot-app/target/ninbot-*.jar ninbot.jar
RUN java -Djarmode=tools -jar ninbot.jar extract --layers --launcher

FROM eclipse-temurin:21-jre-noble

LABEL maintainer="Nincodedo"
LABEL source="https://github.com/Nincodedo/Ninbot"
LABEL org.opencontainers.image.source="https://github.com/Nincodedo/Ninbot"

WORKDIR /home/app

RUN groupadd -r ninbot \
    && useradd -r -s /bin/false -g ninbot ninbot \
    && chown -hR ninbot:ninbot /home/app

USER ninbot:ninbot

COPY --chmod=755 health-check DockerHealthCheck.java ./
COPY --from=build /app/build/ninbot/dependencies/ ./
COPY --from=build /app/build/ninbot/spring-boot-loader/ ./
COPY --from=build /app/build/ninbot/snapshot-dependencies/ ./
COPY --from=build /app/build/ninbot/application/ ./
HEALTHCHECK --start-period=20s CMD ["/home/app/health-check"]
EXPOSE 8080
ENTRYPOINT ["java"]
CMD ["-Xss512k", "-Xmx256M", "--enable-preview", "org.springframework.boot.loader.launch.JarLauncher"]
