FROM openjdk:8-jdk-stretch

RUN curl -L -o sbt-1.3.8.deb https://dl.bintray.com/sbt/debian/sbt-1.3.8.deb
RUN dpkg -i sbt-1.3.8.deb
RUN rm sbt-1.3.8.deb
RUN apt-get install sbt
RUN sbt sbtVersion

RUN curl -L -o scala-2.12.8.tgz https://downloads.lightbend.com/scala/2.12.8/scala-2.12.8.tgz
RUN tar xvf scala-2.12.8.tgz
RUN mv scala-2.12.8 /usr/lib
RUN ln -s /usr/lib/scala-2.12.8 /usr/lib/scala
RUN echo "export PATH=$PATH:/usr/lib/scala/bin" >> ~/.bashrc
RUN rm scala-2.12.8.tgz

RUN curl -sL https://deb.nodesource.com/setup_12.x | bash -
RUN apt-get install nodejs
RUN npm install -g npm@6.8
RUN node -v
RUN npm -v

RUN apt update && apt install -y \
  unzip \
  vim \
  git \
  curl

EXPOSE 8000
EXPOSE 9000
EXPOSE 5000
EXPOSE 8888

RUN mkdir -p /home/leksande_1167341/projekt
VOLUME [ "/home/leksande_1167341/projekt" ]
RUN ln -s /usr/lib/scala/bin/scala bin/scala

