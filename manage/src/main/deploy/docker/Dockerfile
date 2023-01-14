FROM java:8

#设置时区
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

#设置工作目录
WORKDIR /opt/app/manage

#设置编码集
ENV LANG C.UTF-8

#设置docker容器内交互端口
#EXPOSE 8080

ENTRYPOINT ["/bin/bash", "/opt/app/manage/deploy/docker/bootstrap.sh"]

#复制所有生成数据
COPY / .

#新建用户hbq
RUN groupadd --gid 1000 hbq && useradd -d /home/hbq -g hbq -s /bin/sh hbq && chown hbq:hbq /opt/app/manage && chmod -R 755 /opt/app/manage

#使用hbq执行后续操作
USER hbq
