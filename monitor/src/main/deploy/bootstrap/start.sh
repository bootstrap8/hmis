#!/bin/sh

if [[ -f "../setenv.sh" ]];then
. ../setenv.sh
fi

getProcessNo(){
 echo "`ps -ef|grep hmis-monitor|grep -v grep|awk '{print $2}'`"
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

JAVA_OPTIONS="-DAPP_HOME=`pwd`"
while read option
do
   JAVA_OPTIONS=${option}" "${JAVA_OPTIONS}
done < jvm.options

nohup java ${JAVA_OPTIONS} -jar ../../lib/*.jar &
echo -e "monitor started.\n"
