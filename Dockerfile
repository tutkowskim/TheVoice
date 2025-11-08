FROM eclipse-temurin:25.0.1_8-jdk AS build
WORKDIR /app
COPY . /app
RUN ./gradlew --no-daemon clean installDist

FROM eclipse-temurin:25.0.1_8-jdk AS runtime
WORKDIR /app
COPY --from=build /app/build/install/TheVoice /app
RUN chmod +x ./bin/TheVoice

ENTRYPOINT ["sh", "./bin/TheVoice"]