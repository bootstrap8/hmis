#!/bin/sh

if [[ -f "../setenv.sh" ]];then
. ../setenv.sh
fi


ProcessNo=`ps -ef|grep hmis-gateway|grep -v grep|awk '{print $2}'`
if [[ -n "${ProcessNo}" ]]; then
  echo -e "Find Process Info ...\n"
  echo "ProcessNo: ${ProcessNo}"
  echo "Process Directory: `pwdx ${ProcessNo}|awk '{print $2}'`"
  echo -e "Waiting Process Kill ...\n"
  sleep 1
  kill ${ProcessNo}
  echo -e "gateway,${ProcessNo} was killed.\n"
fi
