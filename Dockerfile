FROM amazoncorretto:21 AS build
WORKDIR /app
COPY . /app
RUN ./gradlew --no-daemon clean installDist

FROM amazoncorretto:21 AS runtime
WORKDIR /app
COPY --from=build /app/build/install/TheVoice /app
ENTRYPOINT ["sh", "./bin/TheVoice"]