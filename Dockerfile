FROM maven:3.6.3-jdk-13 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -e -B dependency:resolve
COPY src ./src
COPY .git ./.git
RUN mvn package -P git-commit --no-transfer-progress

FROM adoptopenjdk/openjdk13:jre-13.0.1_9-alpine
COPY --from=build /app/target/ninbot-1.0-SNAPSHOT.jar /
RUN apk add --no-cache curl
HEALTHCHECK CMD curl --request GET --url http://localhost:8090/actuator/health || exit 1
RUN java -version
CMD ["java", "--enable-preview", "-jar", "/ninbot-1.0-SNAPSHOT.jar"]
