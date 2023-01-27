#!/bin/sh

if [[ -f "../setenv.sh" ]];then
. ../setenv.sh
fi

name=monitor
ver=1.0-SNAPSHOT
tag=${name}:${ver}

echo -e "\nFind Docker Containers ..."
cid=`docker ps -a|grep ${name}|grep ${ver}|awk '{print $1}'`
if [[ -n "${cid}" ]]; then
  echo "monitor,${cid}"
fi