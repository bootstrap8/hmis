#!/bin/sh

if [[ -f "../setenv.sh" ]];then
. ../setenv.sh
fi

getProcessNo(){
 echo "`ps -ef|grep hmis-gateway|grep -v grep|awk '{print $2}'`"
}

ProcessNo=`getProcessNo`
if [[ -n ${ProcessNo} ]]; then
  echo "Find Process Info ..."
  echo "ProcessNo: ${ProcessNo}"
  echo "Process Directory: `pwdx ${ProcessNo}|awk '{print $2}'`"
  sleep 1
  kill ${ProcessNo}
  echo "Waiting Process Exit ..."
  i=1
  while true;
  do
    if [[ -z `getProcessNo` ]];then
      break;
    fi
    sleep 1
    echo "[${i}s]Check Process If Exit..."
    i=$((i+1))
  done
  echo "${ProcessNo} was killed."
fi
