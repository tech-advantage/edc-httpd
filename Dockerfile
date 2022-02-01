FROM gradle:jdk17-alpine

RUN mkdir /work
WORKDIR /work
COPY . /work
RUN tail -n +2 ./src/main/resources/config/webserver.yml > ./src/main/resources/config/temp.yml \
&& mv ./src/main/resources/config/temp.yml ./src/main/resources/config/webserver.yml
RUN sed -i 's/^/base: \/app\n/' ./src/main/resources/config/webserver.yml
RUN gradle build

FROM openjdk:17-alpine3.13
EXPOSE 8088

RUN apk upgrade -U -a
RUN mkdir /app
RUN mkdir /app/doc
VOLUME /home/

COPY --from=0 /work/build/libs/edc-httpd-java-1.3.0.jar /app/edc-httpd-java-1.3.0.jar
WORKDIR /home
ENTRYPOINT ["java","-jar" ,"/app/edc-httpd-java-1.3.0.jar"]
ENV NOM edc


