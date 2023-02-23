#!/bin/sh

if [[ -f "../setenv.sh" ]];then
. ../setenv.sh
fi



if [[ -n "${docker_registry_user}" ]];then
docker login -u "${docker_registry_user}" -p "${docker_registry_pwd}" ${docker_registry}
fi

name=config
ver=1.0
tag=${name}:${ver}

imageId=`docker images | grep ${name} \
| grep ${ver} \
| awk '{print $3}'`

echo -e "\nPush Docker Info (${docker_prefix}/${tag})"
docker push ${docker_prefix}/${tag}