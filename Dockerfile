FROM adoptopenjdk:11-jre-hotspot as builder
ARG JAR_FILE=trainerinvoice*.jar
COPY ${JAR_FILE} application.jar
RUN true
RUN java -Djarmode=layertools -jar application.jar extract
RUN true

FROM adoptopenjdk:11-jre-hotspot
RUN apt-get install -y locales
RUN true
RUN locale-gen en_US.UTF-8
RUN true
RUN locale-gen de_DE.UTF-8
RUN true
ENV LANG='de_DE.UTF-8' LANGUAGE='de_DE:de' LC_ALL='de_DE.UTF-8'
RUN true
RUN echo "Europe/Berlin" > /etc/timezone
RUN true
ENV TZ=Europe/Berlin
RUN true
COPY --from=builder dependencies/ ./
RUN true
COPY --from=builder snapshot-dependencies/ ./
RUN true
COPY --from=builder spring-boot-loader/ ./
RUN true
COPY --from=builder application/ ./
RUN true

WORKDIR /app

ENV JAVA_OPTS="-Duser.language=de -Duser.country=DE -Duser.region=DE"
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]
