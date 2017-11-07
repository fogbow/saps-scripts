FROM ubuntu

WORKDIR /home/ubuntu

RUN mkdir config

COPY config/ ./config
COPY ./ ./

RUN apt-get update
RUN apt-get install git -y
RUN apt-get install nano -y
RUN apt-get install software-properties-common -y
RUN apt-get install curl
RUN add-apt-repository ppa:openjdk-r/ppa
RUN apt-get update
RUN apt-get install openjdk-7-jdk -y
RUN apt-get update
RUN apt-get install wget

RUN cd /home/ubuntu/ && git clone https://github.com/ufcg-lsd/SEBAL.git
RUN cd /home/ubuntu/SEBAL && tar -xvzf target.tar.gz
