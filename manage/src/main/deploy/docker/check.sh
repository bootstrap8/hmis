#!/bin/sh

if [[ -f "../setenv.sh" ]];then
. ../setenv.sh
fi

name=manage
ver=1.0-SNAPSHOT
tag=${name}:${ver}

echo -e "Find Docker Containers ...\n"
cid=`docker ps -a|grep ${name}|grep ${ver}|awk '{print $1}'`
if [[ -n "${cid}" ]]; then
  echo "manage,${cid}"
fi