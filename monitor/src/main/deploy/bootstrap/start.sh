#!/bin/sh

if [[ -f "../setenv.sh" ]];then
. ../setenv.sh
fi


ProcessNo=`ps -ef|grep hmis-monitor|grep -v grep|awk '{print $2}'`
if [[ -n "${ProcessNo}" ]]; then
  echo -e "\nFind Process Info ..."
  echo "ProcessNo: ${ProcessNo}"
  echo "Process Directory: `pwdx ${ProcessNo}|awk '{print $2}'`"
  echo -e "\nWaiting Process Kill ..."
  sleep 1
  kill ${ProcessNo}
  echo "${ProcessNo} was killed."
fi

JAVA_OPTIONS="-DAPP_HOME=`pwd`"
while read option
do
   JAVA_OPTIONS=${option}" "${JAVA_OPTIONS}
done < jvm.options

nohup java ${JAVA_OPTIONS} -jar ../../lib/*.jar &
echo -e "monitor started.\n"
