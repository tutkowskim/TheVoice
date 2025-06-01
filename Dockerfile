FROM eclipse-temurin:21.0.7_6-jdk AS build
WORKDIR /app
COPY . /app
RUN ./gradlew --no-daemon clean installDist

FROM eclipse-temurin:21.0.7_6-jdk AS runtime
WORKDIR /app
COPY --from=build /app/build/install/TheVoice /app
RUN chmod +x ./bin/TheVoice

ENTRYPOINT ["sh", "./bin/TheVoice"]