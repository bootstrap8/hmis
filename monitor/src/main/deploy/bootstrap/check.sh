#!/bin/sh

if [[ -f "../setenv.sh" ]];then
. ../setenv.sh
fi


ProcessNo=`ps -ef|grep monitor|grep -v grep|awk '{print $2}'`
if [[ -n "${ProcessNo}" ]]; then
  echo -e "\nFind Process Info ..."
  echo "ProcessNo: ${ProcessNo}"
  echo "Process Directory: `pwdx ${ProcessNo}|awk '{print $2}'`"
fi