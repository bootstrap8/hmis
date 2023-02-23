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
  docker stop ${cid}
  docker rm -f ${cid}
  echo "config,${cid} was stop."
fi
