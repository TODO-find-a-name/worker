FROM adoptopenjdk/openjdk11:jdk-11.0.8_10-ubuntu

# stuff needed by webrtc
RUN apt-get update && apt-get install -y libpulse0 libx11-dev pulseaudio
RUN adduser root pulse-access

# ---- BEGIN: node 22 and npm 10 installation ----
# Install dependencies for Node.js installation
RUN apt-get update && apt-get install -y curl gnupg build-essential && rm -rf /var/lib/apt/lists/*

# Add NodeSource APT repository for Node.js 22
RUN curl -fsSL https://deb.nodesource.com/setup_22.x | bash -

# Install Node.js and npm
RUN apt-get update && apt-get install -y nodejs \
    && npm install -g npm@10 \
    && rm -rf /var/lib/apt/lists/*

# ---- END: node 22 and npm 10 installation ----

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

# ----- BEGIN: node js module dependencies -----
WORKDIR /app/modules/node_js_client_module
COPY modules/node_js_client_module/package.json ./package.json
COPY modules/node_js_client_module/package-lock.json ./package-lock.json
COPY modules/node_js_client_module/tsconfig.json ./tsconfig.json
COPY modules/node_js_client_module/webpack.config.js ./webpack.config.js
RUN npm install --frozen-lockfile
# ----- END: node module dependencies -----

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
COPY modules/node_js_client_module/src ./src
RUN npm run build
RUN mv ./dist/bundle.js ./bundle.js
# deleting everything except the bundle and the startup script
RUN find . -maxdepth 1 ! -name 'bundle.js' ! -name 'run_prod.sh' ! -name '.' -exec rm -rf {} +
# ----- END: node module deploy -----

WORKDIR /app
# startup script that also starts stuff needed by webrtc
COPY ./docker-startup.sh .
RUN chmod +x ./docker-startup.sh

ENTRYPOINT ./docker-startup.sh