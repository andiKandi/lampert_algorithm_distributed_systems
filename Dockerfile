FROM openjdk:11
COPY target/*-jar-with-dependencies.jar /usr/app/app.jar
COPY src/main/resources/ /usr/app/

ADD https://github.com/ufoscout/docker-compose-wait/releases/download/2.7.3/wait /wait
RUN chmod +x /wait

COPY datasource.sh /run.sh
ENTRYPOINT ["sh","/run.sh"]