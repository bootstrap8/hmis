#!/bin/sh

if [[ -f "../setenv.sh" ]];then
. ../setenv.sh
fi



if [[ -n "${docker_registry_user}" ]];then
docker login -u "${docker_registry_user}" -p "${docker_registry_pwd}" ${docker_registry}
fi

name=manage
ver=1.0-SNAPSHOT
tag=${name}:${ver}

imageId=`docker images | grep ${name} \
| grep ${ver} \
| awk '{print $3}'`

echo -e "Push Docker Info (${docker_prefix}/${tag})\n"
docker push ${docker_prefix}/${tag}