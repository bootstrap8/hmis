#!/bin/sh

if [[ -f "../setenv.sh" ]];then
. ../setenv.sh
fi


ProcessNo=`ps -ef|grep gateway|grep -v grep|awk '{print $2}'`
if [[ -n "${ProcessNo}" ]]; then
  echo -e "Find Process Info ...\n"
  echo "ProcessNo: ${ProcessNo}"
  echo "Process Directory: `pwdx ${ProcessNo}|awk '{print $2}'`"
  echo -e "Waiting Process Kill ...\n"
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
echo -e "gateway started.\n"
