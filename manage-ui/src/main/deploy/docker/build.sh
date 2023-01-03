#!/bin/sh

if [[ -f "../setenv.sh" ]];then
. ../setenv.sh
fi

name=manage-ui
ver=1.0-SNAPSHOT
tag=${name}:${ver}

echo -e "Stop Docker Containers ...\n"
cid=`docker ps -a|grep ${name}|grep ${ver}|awk '{print $1}'`
if [[ -n "${cid}" ]]; then
  docker rm -f ${cid}
  echo "manage-ui,${cid} was stop."
fi

echo -e "Uninstall Docker Images ...\n"
mid=`docker images|grep "${name}"|grep "${ver}"|awk '{print $3}'`
if [[ -n "${mid}" ]]; then
  docker rmi -f ${mid}
  echo "manage-ui,${mid} was uninstalled."
fi

echo -e "Start Docker Images Building ...\n"
docker build -t ${docker_prefix}/${tag} ../../
