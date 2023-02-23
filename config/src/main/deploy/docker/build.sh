#!/bin/sh

if [[ -f "../setenv.sh" ]];then
. ../setenv.sh
fi

name=config
ver=1.0
tag=${name}:${ver}

echo -e "\nStop Docker Containers ..."
cid=`docker ps -a|grep ${name}|grep ${ver}|awk '{print $1}'`
if [[ -n "${cid}" ]]; then
  docker rm -f ${cid}
  echo "config,${cid} was stop."
fi

echo -e "\nUninstall Docker Images ..."
mid=`docker images|grep "${name}"|grep "${ver}"|awk '{print $3}'`
if [[ -n "${mid}" ]]; then
  docker rmi -f ${mid}
  echo "config,${mid} was uninstalled."
fi

echo -e "\nStart Docker Images Building ..."
docker build -t ${docker_prefix}/${tag} ../../
