FROM amazoncorretto:17 AS build
WORKDIR /app
COPY . /app
RUN ./gradlew --no-daemon clean build || true

FROM amazoncorretto:17-alpine AS runtime
WORKDIR /app
COPY --from=build /app/build/libs/TheVoice.jar /app/TheVoice.jar
ENTRYPOINT ["java", "-jar", "/app/TheVoice.jar"]