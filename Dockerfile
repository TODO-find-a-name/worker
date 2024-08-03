FROM alessandrotalmi/worker-base-image

# ----- BEGIN: core dependencies-----
WORKDIR /app
# downloading gradle depencencies
COPY gradlew .
COPY gradlew.bat .
COPY gradle.properties .
COPY gradle ./gradle
COPY settings.gradle.kts .
COPY build.gradle.kts .
RUN chmod +x ./gradlew
RUN ./gradlew dependencies
# ----- END: core dependencies -----

# ----- BEGIN: core deploy -----
WORKDIR /app
# building the project inside the image (java webrtc has os-dependent dependencies resolved in a previous step)
COPY ./src ./src
RUN ./gradlew clean fatJar

# placing the fat jar in the workdir
RUN mv ./build/libs/worker-fatjar.jar ./worker-fatjar.jar

# deleting everything except the fatjar and the modules
RUN find . -maxdepth 1 ! -name 'worker-fatjar.jar' ! -name 'modules' ! -name '.' -exec rm -rf {} +
# ----- END: core deploy -----

# ----- BEGIN: node js module deploy -----
WORKDIR /app/modules/node_js_client_module
ENV NODE_MODULE_SCRIPT_PATH="/app/modules/node_js_client_module/run_prod.sh"
COPY modules/node_js_client_module/run_prod.sh ./run_prod.sh
RUN chmod +x ./run_prod.sh
COPY modules/node_js_client_module/dist/bundle.js ./bundle.js
# deleting everything except the bundle and the startup script
# ----- END: node module deploy -----

WORKDIR /app
# startup script that also starts stuff needed by webrtc
COPY ./docker-startup.sh .
RUN chmod +x ./docker-startup.sh

ENTRYPOINT ./docker-startup.sh