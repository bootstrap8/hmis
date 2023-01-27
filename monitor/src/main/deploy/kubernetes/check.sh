#!/bin/sh

if [[ -f "../setenv.sh" ]];then
. ../setenv.sh
fi

name=monitor

echo -e "\nFind Kubernetes Pods ..."
pods=`kubectl get pods -n ${k8s_ns}|grep ${name}|grep Running`
if [[ -n "${pods}" ]]; then
    echo "${pods}"
fi