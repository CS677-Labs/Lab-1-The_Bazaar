#/bin/bash
set -e
mkdir -p classfiles
function finish {
  echo "Cleanup"
  rm -rf classfiles
  kill $server_id $client_id >/dev/null 2>&1 || echo "No processes to delete."
}
trap finish EXIT 1
trap finish RETURN 1
echo "Compiling java files."
javac -d classfiles src/*.java
#
echo "Running test case 1 - Seller sells Fish, Buyer buys Fish."

rm *.log.* >/dev/null 2>&1 || echo "No logs to delete"

echo "Running the server as the seller of Fishes"
java -classpath classfiles -Djava.rmi.server.codebase=file:files/ Server 1 src/config-milestone1.properties Fish 5 &
server_id=$!
sleep 3
if ! (ps | grep " $server_id ")
then
	echo "Failed to start the seller node" && return 1
fi
echo "Running the client as a buyer of Fishes"
java -classpath classfiles -Djava.rmi.server.codebase=file:files/ Client 2 src/config-milestone1.properties Fish &
client_id=$!

sleep 10
if (grep -Fq "Bought product Fish" client.log) && (grep -Fq "Restocking" server.log)
then
    echo "Test case 1 passed."
else
    echo "Test case 1 failed."
    return 1
fi
kill $server_id $client_id >/dev/null 2>&1 || echo "No processes to delete."
sleep 2
#
#
#
echo "Running test case 2. Seller sells only Boars. Buyer buys only Fishes"
rm *.log.*  >/dev/null 2>&1 || echo "No logs to delete"
echo "Running the server as the seller of Boars"
java -classpath classfiles -Djava.rmi.server.codebase=file:files/ Server 1 src/config-milestone1.properties Boar 5 &
server_id=$!

sleep 3
if ! (ps | grep "java" | grep " $server_id ")
then
	echo "Failed to start the seller node" && return 1
fi
echo "Running the client as a buyer of Fishes"
java -classpath classfiles -Djava.rmi.server.codebase=file:files/ Client 2 src/config-milestone1.properties Fish &
client_id=$!
if ! (ps | grep " $client_id ")
then
	echo "Failed to start the client node" && return 1
fi
sleep 10
if grep -Fq "Could not buy product Fish" client.log
then
    echo "Test case 2 passed."
else
    echo "Test case 2 Failed."
    return 1
fi
kill $server_id $client_id >/dev/null 2>&1 || echo "No processes to delete."
sleep 2

#
#
#
echo "Running test case 3 - Both nodes are randomly assigned a role (Seller/Buyer)."
id=$((1 + $RANDOM % 2))
if [ "$id" -eq 1 ]
then
    id2=2
else
    id2=1
fi
echo "Running Node $id as Seller of Fishes."
java -classpath classfiles -Djava.rmi.server.codebase=file:files/ Server $id  src/config-milestone1.properties Fish 5 &
server_id=$!
sleep 3
if ! (ps | grep " $server_id ")
  then
      echo "Failed to start the server node" && return 1
fi

echo "Running Node $id2 as a Buyer of Fishes."
java -classpath classfiles -Djava.rmi.server.codebase=file:files/ Client $id2 src/config-milestone1.properties Fish 5 &
client_id=$!
if ! (ps | grep " $client_id ")
  then
      echo "Failed to start the client node" && return 1
fi
sleep 10
if (grep -Fq "Bought product Fish" client.log) && (grep -Fq "Restocking" server.log)
then
    echo "Test case 3 Passed."
else
    echo "Test case 3 Failed."
    return 1
fi

echo "All tests passed"
rm *.log.* >/dev/null 2>&1 || echo "No logs to delete"