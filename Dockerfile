FROM maven:3.6.3-openjdk-15 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -B dependency:resolve
COPY src ./src
COPY .git ./.git
RUN mvn package -P git-commit --no-transfer-progress

FROM openjdk:15-slim
COPY --from=build /app/target/ninbot*.jar /ninbot.jar
RUN apt-get update && apt-get install curl -y
HEALTHCHECK CMD curl --request GET --url http://localhost:8090/actuator/health || exit 1
CMD ["java", "-Xss512k", "-Xmx256M", "--enable-preview", "-jar", "/ninbot.jar"]
