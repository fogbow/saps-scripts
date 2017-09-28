FROM ubuntu

WORKDIR /home/ubuntu

RUN mkdir execs

COPY ./ execs/

RUN apt-get update
RUN apt-get install nano -y
RUN apt-get install software-properties-common -y
RUN apt-get install curl
RUN add-apt-repository ppa:openjdk-r/ppa
RUN apt-get update
RUN apt-get install openjdk-7-jdk -y
