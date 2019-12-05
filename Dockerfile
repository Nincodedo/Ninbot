FROM maven:3.6-jdk-8-alpine AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -e -B dependency:resolve
COPY src ./src
COPY .git ./.git
RUN mvn verify -P git-commit,integration --no-transfer-progress

FROM openjdk:8-jre-alpine
COPY --from=build /app/target/ninbot-1.0-SNAPSHOT.jar /
RUN apk add --no-cache curl
HEALTHCHECK CMD curl --request GET --url http://localhost:8090/actuator/health || exit 1
CMD ["java", "-jar", "/ninbot-1.0-SNAPSHOT.jar"]
