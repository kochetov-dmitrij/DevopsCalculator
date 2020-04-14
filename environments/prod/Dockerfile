FROM ubuntu:18.04

EXPOSE 8080
EXPOSE 22
RUN apt update && apt install -y openssh-server sudo locales python
RUN sed -i -e 's/# en_US.UTF-8 UTF-8/en_US.UTF-8 UTF-8/' /etc/locale.gen && locale-gen
ENV LANG en_US.UTF-8  
ENV LANGUAGE en_US:en  
ENV LC_ALL en_US.UTF-8     

RUN mkdir /root/.ssh
COPY id_rsa.pub /root/.ssh

RUN cat /root/.ssh/id_rsa.pub > /root/.ssh/authorized_keys
RUN mkdir /run/sshd

CMD ["/usr/sbin/sshd", "-D"]
