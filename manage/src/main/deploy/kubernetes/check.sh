#!/bin/sh

if [[ -f "../setenv.sh" ]];then
. ../setenv.sh
fi

name=manage

echo -e "Find Kubernetes Pods ...\n"
pods=`kubectl get pods -n ${k8s_ns}|grep ${name}|grep Running`
if [[ -n "${pods}" ]]; then
    echo "${pods}"
fi