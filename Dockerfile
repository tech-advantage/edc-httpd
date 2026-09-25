FROM gradle:9.8.0-jdk25-alpine AS build

ARG HELP_VIEWER_VERSION=3.3.3

RUN mkdir /work
WORKDIR /work
COPY . /work
# Force the documentation base to /app, whatever the local value is
RUN sed -i -e '1i base: /app' -e '/^base:/d' ./src/main/resources/config/webserver.yml
RUN gradle build --no-daemon

# Download the help viewer in the build stage so the runtime image does not need curl
ADD https://github.com/tech-advantage/edc-help-viewer/releases/download/v${HELP_VIEWER_VERSION}/edc-help-viewer.${HELP_VIEWER_VERSION}.zip /tmp/edc-help-viewer.zip
RUN mkdir -p /help && unzip -q /tmp/edc-help-viewer.zip -d /tmp/viewer && mv /tmp/viewer/dist/* /help

FROM eclipse-temurin:25-jre-alpine

EXPOSE 8088

RUN apk upgrade --no-cache

RUN adduser -D edcuser && mkdir -p /app/doc /app/help

COPY --from=build /help /app/help
COPY --from=build /work/build/libs/edc-httpd-java-*-all.jar /app/edc-httpd-java.jar

# The token files are written in the working directory and the index in the user home
RUN chown -R edcuser /app /home && chmod -R 750 /app

VOLUME /home/
WORKDIR /home
USER edcuser

ENTRYPOINT ["java","-jar" ,"/app/edc-httpd-java.jar"]
ENV NOM edc
