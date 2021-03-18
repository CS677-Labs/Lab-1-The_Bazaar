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
  done < "config-milestone3.properties"
  rm -rf build/* >/dev/null 2>&1
  rm *.log* >/dev/null 2>&1 || echo "No logs to delete"
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

python3 generateConfigFile.py 3 2 ../$1 config-milestone3.properties

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
    java -Djava.rmi.server.codebase=file:/ -jar ${role}.jar $id config-milestone3.properties Fish 2 >/dev/null 2>&1 &
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
    scp "config-milestone3.properties" ec2-user@"$ip":"temp_$id"
    # Todo: Run command differently for buyer and server.
    pid=$(ssh -n ec2-user@$ip "cd temp_$id && (java -Djava.rmi.server.codebase=file:/ -jar ${role}.jar $id config-milestone3.properties Fish 2 >/dev/null 2>&1 & echo \$!)")
    sleep 2
    status=0
    ssh -n ec2-user@"$ip" "ps -ef | grep java | grep $pid | grep -v grep" || status=$?
    pids[id]=$pid
  fi
  if [[ "$status" != 0 ]]
  then
	  echo "Failed to start the node $id with ip $ip. Exiting..." && return 1
  fi
done < config-milestone3.properties

sleep 10

echo "Validating the system"
clientSuccess=0
sellerSuccess=0
while IFS= read -r line
do
  newline=(${line//=/ })
  id=${newline[0]}
  network=(${newline[1]//,/ })
  url=(${network[0]//:/ })
  ip="${url[0]}:${url[1]}"
  if [[ "${node_roles[id]}" == "Client" ]]
  then
    if [[ "$ip" == *"localhost" ]] || [[ "$ip" == *"127.0.0.1" ]]
    then
      # Todo: Check for the particular log file for this id.
      grep -Fq "Bought product Fish" Node_${id}_client.log* || echo "Buyer with id $id did not buy the product."
    else
      fullurl=$ip
      ip=$(echo "$fullurl" |sed 's/https\?:\/\///')
      ssh -n ec2-user@"$ip" "cd ${dir[id]} && (grep -Fq \"Bought product Fish\" *.log)" && clientSuccess=1 || echo "Buyer with id $id did not buy the product."
    fi
  else
    if [[ "$ip" == *"localhost" ]] || [[ "$ip" == *"127.0.0.1" ]]
    then
      grep -Fq "Restocking" Node_${id}_server.log* || echo "Seller with ID $id did not restock"
    else
      fullurl=$ip
      ip=$(echo "$fullurl" |sed 's/https\?:\/\///')
      ssh -n ec2-user@"$ip" "cd ${dir[id]} && grep -Fq Restocking *.log" && sellerSuccess=1 || echo "Seller with ID $id did not restock"
    fi
  fi
done < config-milestone3.properties

if [[ "$clientSuccess" == 0 ]] || [[ "$sellerSuccess" == 0 ]]
then
  echo "Test failed for milestone 3." 
  return 1
fi
echo "Test passed for milestone 3."
