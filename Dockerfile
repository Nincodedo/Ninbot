FROM maven:3.6.3-jdk-14 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -B dependency:resolve
COPY src ./src
COPY .git ./.git
RUN mvn package -P git-commit --no-transfer-progress

FROM adoptopenjdk/openjdk14:alpine-slim
COPY --from=build /app/target/ninbot*.jar /ninbot.jar
RUN apk add --no-cache curl
HEALTHCHECK CMD curl --request GET --url http://localhost:8090/actuator/health || exit 1
CMD ["java", "--enable-preview", "-jar", "/ninbot.jar"]
