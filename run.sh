#/bin/bash
set -e
mkdir -p classfiles
function finish {
  echo "Cleanup"
  rm -rf classfiles
  rm -rf *.jar || grep "No jars to cleanup"
  for i in "${local_pids[@]}"
  do
   kill $i || echo "Failed to kill $i. It is probably already killed."
   # or do whatever with individual element of the array
  done

#  # For remote cleanup
#  while IFS= read -r line
#  do
#    line=(${line//=/ })
#    id=${line[0]}
#    ssh root@$id "kill ${pids[id]}" || echo "Failed to kill process $i."
#  done < "config-milestone3.properties"
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
  line=(${line//=/ })
  id=${line[0]}
  network=(${line[1]//,/ })
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
    ps | grep "java" | grep "$pid" >/dev/null 2>&1
    status=$?
    local_pids[id]=$pid
  else
    echo "Running role $role on remote machine $ip."
    dir[id]="temp_$id"
    ssh root@"$line" "rm -rf temp* && mkdir temp_$id"
    # Ensure there is at least one seller and one buyer in the system.
    scp role.jar "config-milestone3.properties" root@"$ip":/"temp_$id"
    # Todo: Run command differently for buyer and server.
    pid=$(ssh root@$ip "java -Djava.rmi.server.codebase=file:/ -jar ${role}.jar $role $id network-config.properties Fish 2 >/dev/null 2>&1 & && echo $?")
    sleep 2
    ssh root@"$line" "ps | grep \"java\" | grep \"$pid\" >/dev/null 2>&1"
    status=$?
    pids[id]=pid
  fi
  if [[ "$status" != 0 ]]
  then
	  echo "Failed to start the node $id with ip $ip. Exiting..." && return 1
  fi
done < "network-config.properties"

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