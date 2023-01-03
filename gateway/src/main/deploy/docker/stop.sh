#!/bin/sh

if [[ -f "../setenv.sh" ]];then
. ../setenv.sh
fi

name=gateway
ver=1.0-SNAPSHOT
tag=${name}:${ver}

echo -e "Stop Docker Containers ...\n"
cid=`docker ps -a|grep ${name}|grep ${ver}|awk '{print $1}'`
if [[ -n "${cid}" ]]; then
  docker rm -f ${cid}
  echo "gateway,${cid} was stop."
fi