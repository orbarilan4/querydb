FROM azul/zulu-openjdk-alpine:13-jre

COPY /build/libs/querydb-all.jar /tmp
COPY config.conf /tmp
RUN chmod -R 777 /tmp/config.conf &&\
	chmod -R 777 /tmp/querydb-all.jar

ENTRYPOINT java -jar /tmp/querydb-all.jar "/tmp/config.conf"