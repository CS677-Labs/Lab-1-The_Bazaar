#/bin/bash
set -e
mkdir -p classfiles
function finish {
  echo "Cleanup"
  rm -rf classfiles
  kill $server_id $client_id >/dev/null 2>&1 || echo "No processes to delete."
}
trap finish EXIT 1
echo "Compiling java files."
javac -d classfiles src/*.java
#
echo "Running test case 1 - Seller sells Fish, Buyer buys Fish."

rm *.log.* >/dev/null 2>&1 || echo "No logs to delete"

echo "Running the server as the seller of Fishes"
java -classpath classfiles -Djava.rmi.server.codebase=file:files/ Server 1 src/config.properties Fish 5 &
server_id=$!
sleep 3
if ! (ps | grep " $server_id ")
then
	echo "Failed to start the seller node" && exit 1
fi
echo "Running the client as a buyer of Fishes"
java -classpath classfiles -Djava.rmi.server.codebase=file:files/ Client 2 src/config.properties Fish &
client_id=$!

sleep 10
if (grep -Fq "Bought product Fish" client.log) && (grep -Fq "Restocking" server.log)
then
    echo "Test case 1 passed."
else
    echo "Test case 1 failed."
    exit 1
fi
kill $server_id $client_id >/dev/null 2>&1 || echo "No processes to delete."
sleep 2
#
#
#
echo "Running test case 2. Seller sells only Boars. Buyer buys only Fishes"
rm *.log.*  >/dev/null 2>&1 || echo "No logs to delete"
echo "Running the server as the seller of Boars"
java -classpath classfiles -Djava.rmi.server.codebase=file:files/ Server 1 src/config.properties Boar 5 &
server_id=$!

sleep 3
if ! (ps | grep "java" | grep " $server_id ")
then
	echo "Failed to start the seller node" && exit 1
fi
echo "Running the client as a buyer of Fishes"
java -classpath classfiles -Djava.rmi.server.codebase=file:files/ Client 2 src/config.properties Fish &
client_id=$!
if ! (ps | grep " $client_id ")
then
	echo "Failed to start the client node" && exit 1
fi
sleep 10
if grep -Fq "Bought product Fish" client.log
then
    echo "Test case 2 Failed."
    exit 1
else
    echo "Test case 2 passed."

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
java -classpath classfiles -Djava.rmi.server.codebase=file:files/ Server $id  src/config.properties Fish 5 &
server_id=$!
sleep 3
if ! (ps | grep " $server_id ")
  then
      echo "Failed to start the server node" && exit 1
fi

echo "Running Node $id2 as a Buyer of Fishes."
java -classpath classfiles -Djava.rmi.server.codebase=file:files/ Client $id2 src/config.properties Fish 5 &
client_id=$!
if ! (ps | grep " $client_id ")
  then
      echo "Failed to start the client node" && exit 1
fi
sleep 10
if (grep -Fq "Bought product Fish" client.log) && (grep -Fq "Restocking" server.log)
then
    echo "Test case 3 Passed."
    exit 1
else
    echo "Test case 3 Failed."
fi

echo "All tests passed"
rm *.log.* >/dev/null 2>&1 || echo "No logs to delete"