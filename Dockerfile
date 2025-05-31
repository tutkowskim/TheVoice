FROM amazoncorretto:17 AS build
WORKDIR /app
COPY . /app
RUN ./gradlew --no-daemon clean installDist

FROM amazoncorretto:17-alpine AS runtime
WORKDIR /app
COPY --from=build /app/build/install/TheVoice /app
ENTRYPOINT ["sh", "./bin/TheVoice"]