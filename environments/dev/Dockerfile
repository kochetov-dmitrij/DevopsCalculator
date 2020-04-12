FROM ubuntu:18.04

EXPOSE 8080
EXPOSE 22
RUN apt update && apt install -y openssh-server sudo


RUN mkdir /root/.ssh
COPY id_rsa.pub /root/.ssh

RUN cat /root/.ssh/id_rsa.pub > /root/.ssh/authorized_keys
RUN mkdir /run/sshd

CMD ["/usr/sbin/sshd", "-D"]
