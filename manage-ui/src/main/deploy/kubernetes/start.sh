#!/usr/bin/env bash

if [[ -f "../setenv.sh" ]];then
. ../setenv.sh
fi

if [[ -z "$k8s_api_version" ]]; then
    export k8s_api_version=extensions/v1beta1
fi
echo "k8s_api_version:$k8s_api_version"

envsubst < service.yml | kubectl create -f -
