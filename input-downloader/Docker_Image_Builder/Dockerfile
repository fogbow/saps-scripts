FROM ubuntu

WORKDIR /home/ubuntu

RUN mkdir execs
RUN mkdir config

COPY config/ ./config
COPY ./ execs/

RUN apt-get update
RUN apt-get install git -y
RUN apt-get install nano -y
RUN apt-get install software-properties-common -y
RUN apt-get install curl
RUN add-apt-repository ppa:openjdk-r/ppa
RUN apt-get update
RUN apt-get install openjdk-7-jdk -y
RUN apt-get update

RUN cd /home/ubuntu/execs && git clone https://github.com/ufcg-lsd/SEBAL.git
RUN cd /home/ubuntu/execs/SEBAL && tar -xvzf target.tar.gz
