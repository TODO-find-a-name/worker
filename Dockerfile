FROM adoptopenjdk/openjdk11:jdk-11.0.8_10-ubuntu
WORKDIR /app

# stuff needed by webrtc
RUN apt-get update -y
RUN apt-get install -y libpulse0 libx11-dev pulseaudio
RUN adduser root pulse-access

# startup script that also starts stuff needed by webrtc
COPY ./docker-startup.sh .
RUN chmod +x ./docker-startup.sh

# downloading gradle depencencies
COPY gradlew .
COPY gradlew.bat .
COPY gradle.properties .
COPY gradle ./gradle
COPY settings.gradle.kts .
COPY build.gradle.kts .
RUN chmod +x ./gradlew
RUN ./gradlew dependencies

# building the project inside the image (java webrtc has os-dependent dependencies resolved in the previous step)
COPY ./src ./src
RUN ./gradlew clean fatJar

# placing the fat jar in the workdir
RUN mv ./build/libs/worker-fatjar.jar ./worker-fatjar.jar

# deleting everything except the fatjar and the startup script
RUN find . -maxdepth 1 ! -name 'worker-fatjar.jar' ! -name 'docker-startup.sh' ! -name '.' -exec rm -rf {} +

ENTRYPOINT ./docker-startup.sh