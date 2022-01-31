FROM openjdk:17-alpine3.13


EXPOSE 8088
RUN apk upgrade -U -a


RUN mkdir /app
WORKDIR /app
COPY . /app
RUN ./gradlew install
WORKDIR /home
ENTRYPOINT ["java","-jar" ,"/app/build/libs/edc-httpd-java-1.3.0.jar"]
ENV NOM edc


