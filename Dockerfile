FROM maven:3.6.3-openjdk-15 AS build
COPY pom.xml .
RUN mvn -B dependency:resolve
COPY src ./src
COPY .git ./.git
RUN mvn package -P git-commit --no-transfer-progress

FROM openjdk:15-slim
LABEL maintainer="Nincodedo"
LABEL source="https://github.com/Nincodedo/Ninbot"
COPY --from=build target/ninbot*.jar /ninbot.jar
RUN apt-get update && apt-get install curl -y
HEALTHCHECK --start-period=20s CMD curl --fail --silent http://localhost:8080/actuator/health 2>&1 | grep UP || exit 1
EXPOSE 8080
ENTRYPOINT ["java"]
CMD ["-Xss512k", "-Xmx256M", "--enable-preview", "-jar", "/ninbot.jar"]
