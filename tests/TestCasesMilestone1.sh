#/bin/bash
set -e
mkdir -p classfiles
function finish {
  echo "Cleanup"
  rm -rf classfiles
  kill $server_id $client_id >/dev/null 2>&1 || echo "No processes to delete."
  rm *.log* >/dev/null 2>&1 || echo "No logs to delete"
  rm -rf build/* >/dev/null 2>&1
}
trap finish EXIT
trap finish RETURN

rm -rf build/*
cp -r generateConfigFile.py build && cd build
javac -d classfiles ../src/*.java
echo "Generating jar file"
jar cfe Server.jar Server -C classfiles . && jar cfe Client.jar Client -C classfiles .
echo "Generated jar files"

echo "Running test case 1 - Seller sells Fish, Buyer buys Fish."

rm *.log* >/dev/null 2>&1 || echo "No logs to delete"

echo "Running the server as the seller of Fishes"
java -Djava.rmi.server.codebase=file:/ -jar Server.jar 1 ../src/config-milestone1.properties Fish 5 >/dev/null 2>&1 &
server_id=$!
sleep 3
if ! (ps | grep "java" | grep "$server_id" >/dev/null 2>&1)
then
	echo "Failed to start the seller node" && return 1
fi

echo "Running the client as a buyer of Fishes"
java -jar Client.jar 2 ../src/config-milestone1.properties Fish >/dev/null 2>&1 &
client_id=$!
sleep 3
if ! (ps | grep "java" | grep "$client_id" >/dev/null 2>&1)
then
	echo "Failed to start the seller node" && return 1
fi

echo "Sleeping for 70 seconds...."
sleep 70

if (grep -Fq "Bought product Fish" *client.log) && (grep -Fq "Restocking" *server.log)
then
    echo "Buyer successfully bought the fish. Seller successfully restocked."
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
rm *.log*  >/dev/null 2>&1 || echo "No logs to delete"
echo "Running the server as the seller of Boars"
java -Djava.rmi.server.codebase=file:/ -jar Server.jar 1 ../src/config-milestone1.properties Boars 5 >/dev/null 2>&1 &
server_id=$!
sleep 3
if ! (ps | grep "java" | grep "$server_id" >/dev/null 2>&1)
then
	echo "Failed to start the seller node" && return 1
fi

echo "Running the client as a buyer of Fishes"
java -jar Client.jar 2 ../src/config-milestone1.properties Fish >/dev/null 2>&1 &
client_id=$!
sleep 3
if ! (ps | grep "java" | grep "$client_id" >/dev/null 2>&1)
then
	echo "Failed to start the client node" && return 1
fi
sleep 3

if grep -Fq "Bought product Fish" *client.log
then
    echo "Test case 2 Failed."
    return 1
else
    echo "Buyer could not buy the fish since the seller only sells Boars."
    echo "Test case 2 passed."
fi
kill $server_id $client_id >/dev/null 2>&1 || echo "No processes to delete."
sleep 2

##
##
##
echo "Running test case 3 - Both nodes are randomly assigned a role (Seller/Buyer)."
id=$((1 + $RANDOM % 2))
if [ "$id" -eq 1 ]
then
    id2=2
else
    id2=1
fi
rm *.log*  >/dev/null 2>&1 || echo "No logs to delete"
echo "Running Node $id as Seller of Fishes."
java -Djava.rmi.server.codebase=file:/ -jar Server.jar $id ../src/config-milestone1.properties Fish 5 >/dev/null 2>&1 &

server_id=$!
sleep 3
if ! (ps | grep "java" | grep "$server_id" >/dev/null 2>&1)
  then
      echo "Failed to start the server node" && return 1
fi

echo "Running Node $id2 as a Buyer of Fishes."
java -jar Client.jar $id2 ../src/config-milestone1.properties Fish >/dev/null 2>&1 &
client_id=$!
sleep 3
if ! (ps | grep "java" | grep "$client_id" >/dev/null 2>&1)
  then
      echo "Failed to start the client node" && return 1
fi

echo "Sleeping for 70 seconds....."
sleep 70
if (grep -Fq "Bought product Fish" *client.log) && (grep -Fq "Restocking" *server.log)
then
    echo "Randomly assigned Buyer successfully bought the fish. Seller successfully restocked."
    echo "Test case 3 Passed."
else
    echo "Test case 3 Failed."
    return 1
fi

echo "All tests passed"
