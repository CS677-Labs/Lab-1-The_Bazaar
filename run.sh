#!/bin/bash
set -e
mkdir -p classfiles
function finish {
  echo "Cleanup"
  rm -rf classfiles
  rm -rf *.jar || echo "No jars to cleanup"
  for i in "${local_pids[@]}"
  do
   kill $i || echo "Failed to kill $i. It is probably already killed."
   # or do whatever with individual element of the array
  done

#  # For remote cleanup
  while IFS= read -r line
  do
    newline=(${line//=/ })
    id=${newline[0]}
    network=(${newline[1]//,/ })
    url=(${network[0]//:/ })
    ip="${url[0]}:${url[1]}"
    fullurl=$ip
    ip=$(echo "$fullurl" |sed 's/https\?:\/\///')
    ssh -n ec2-user@"$ip" "kill ${pids[id]}" || echo "Failed to kill process $i."
  done < "network-config.properties"
  rm -rf build/* >/dev/null 2>&1
}
trap finish EXIT
trap finish RETURN
#
#

echo "Compiling java files."
rm -rf build/*
cp -r generateConfigFile.py build && cd build
javac -d classfiles ../src/*.java
echo "Generating jar file"
jar cfe Server.jar Server -C classfiles . && jar cfe Client.jar Client -C classfiles .
echo "Generated jar files"
# Check for the argument. If it's empty then raise error asking for the file.

python generateConfigFile.py $1 $2 $3 network-config.properties

roles[1]="Client"
roles[0]="Server"


while IFS= read -r line
do
  newline=(${line//=/ })
  id=${newline[0]}
  network=(${newline[1]//,/ })
  url=(${network[0]//:/ })
  ip="${url[0]}:${url[1]}"
  port=${url[2]}
  role=${roles[id%2]}
  node_roles[id]=$role
  if [[ "$ip" == *"http://localhost" ]] || [[ "$ip" == *"http://127.0.0.1" ]]
  then
    echo "Running role $role on Localhost...."
    java -Djava.rmi.server.codebase=file:/ -jar ${role}.jar $id network-config.properties Fish 2 >/dev/null 2>&1 &
    pid=$!
    sleep 3
    ps | grep "java" | grep "$pid" | grep -v grep >/dev/null 2>&1
    status=$?
    local_pids[id]=$pid
  else
    fullurl=$ip
    ip=$(echo "$fullurl" |sed 's/https\?:\/\///')
    echo "Running role $role on remote machine $ip."
    dir[id]="temp_$id"
    ssh -n ec2-user@"$ip" "rm -rf temp_$id && mkdir temp_$id && git clone https://github.com/CS677-Labs/Lab-1-The_Bazaar.git || echo "Repo already present" && cd Lab-1-The_Bazaar && javac -d classfiles src/*.java && jar cfe Server.jar Server -C classfiles . && jar cfe Client.jar Client -C classfiles . && cp *.jar ../temp_$id"
    # Ensure there is at least one seller and one buyer in the system.
    scp "network-config.properties" ec2-user@"$ip":"temp_$id"
    # Todo: Run command differently for buyer and server.
    pid=$(ssh -n ec2-user@$ip "cd temp_$id && (java -Djava.rmi.server.codebase=file:/ -jar ${role}.jar $id network-config.properties Fish 2 >/dev/null 2>&1 & echo \$!)")
    sleep 2
    status=0
    ssh -n ec2-user@"$ip" "ps -ef | grep java | grep $pid | grep -v grep" || status=$?
    pids[id]=$pid
  fi
  if [[ "$status" != 0 ]]
  then
	  echo "Failed to start the node $id with ip $ip. Exiting..." && return 1
  fi
done < network-config.properties

echo "Press 'q' to exit"
count=0
while : ; do
read k
if [[ "$k" == "q" ]]
then
  echo "\nQuitting the program\n"
  exit
fi
done