#/bin/bash
set -e
mkdir -p classfiles
function finish {
  echo "Cleanup"
  rm -rf classfiles
  kill $server_id $server_id_2 $client_id_1 $client_id_2 $client_id_3 >/dev/null 2>&1 || echo "No processes to delete."
  rm *.log* >/dev/null 2>&1 || echo "No logs to delete"

}
trap finish EXIT
trap finish RETURN
echo "Compiling java files."
javac -d classfiles ../src/*.java
#
echo "Running test case 1 -> 1 Seller sells Boar, 1 Seller sells Fish (no role in the current transaction), 3 Buyers buy Boar."

rm *.log* >/dev/null 2>&1 || echo "No logs to delete"

echo "Running 1 server as the seller of Boars"
java -classpath classfiles -Djava.rmi.server.codebase=file:classfiles/ Server 5 ../src/config-milestone2.properties Boar 5 >/dev/null 2>&1 &
server_id=$!
sleep 2
if ! (ps | grep "java" | grep "$server_id" >/dev/null 2>&1)
then
	echo "Failed to start the seller node" && return 1
fi

echo "Running 1 server as seller of Fish (Note: The seller of fish has no role in the current transaction)"

java -classpath classfiles -Djava.rmi.server.codebase=file:classfiles/ Server 4 ../src/config-milestone2.properties Fish 1 >/dev/null 2>&1 &
server_id_2=$!
sleep 2
if ! (ps | grep "java" | grep "$server_id_2" >/dev/null 2>&1)
then
	echo "Failed to start the server node" && return 1
fi

echo "Running 3 clients as buyers of Boars"
java -classpath classfiles -Djava.rmi.server.codebase=file:classfiles/ Client 1 ../src/config-milestone2.properties Boar >/dev/null 2>&1 &
client_id_1=$!
sleep 1
if ! (ps | grep "java" | grep "$client_id_1" >/dev/null 2>&1)
then
	echo "Failed to start the client node" && return 1
fi

java -classpath classfiles -Djava.rmi.server.codebase=file:classfiles/ Client 2 ../src/config-milestone2.properties Boar >/dev/null 2>&1 &
client_id_2=$!
sleep 1
if ! (ps | grep "java" | grep "$client_id_2" >/dev/null 2>&1)
then
	echo "Failed to start the client node" && return 1
fi

java -classpath classfiles -Djava.rmi.server.codebase=file:classfiles/ Client 3 ../src/config-milestone2.properties Boar >/dev/null 2>&1 &
client_id_3=$!
sleep 1
if ! (ps | grep "java" | grep "$client_id_3" >/dev/null 2>&1)
then
	echo "Failed to start the client node" && return 1
fi


sleep 10
if (grep -Fq "Bought product Boar" *client.log) &&  (grep -Fq "Restocking" *server.log)
then
    echo "Buyers successfully bought Boars. Seller successfully restocked."
    echo "Test case 1 passed."
else
    echo "Test case 1 failed."
    return 1
fi
kill $server_id $server_id_2 $client_id_1 $client_id_2 $client_id_3 >/dev/null 2>&1 || echo "No processes to delete."
sleep 2
#
#
#--------------------------------------------------------------------------------------------------------------------------------------------
#

echo "Running test case 2 - Simulating race condition"
rm *.log*  >/dev/null 2>&1 || echo "No logs to delete"
echo "Running 1 server as the seller of Boars"

java -classpath classfiles -Djava.rmi.server.codebase=file:classfiles/ Server 5 ../src/config-milestone2.properties Boar 1 Fish >/dev/null 2>&1 &
server_id=$!
sleep 2
if ! (ps | grep "java" | grep "$server_id" >/dev/null 2>&1)
then
	echo "Failed to start the seller node" && return 1
fi

echo "Running 1 server as seller of Fish (Note: The seller of fish has no role in the current transaction)"
java -classpath classfiles -Djava.rmi.server.codebase=file:classfiles/ Server 4 ../src/config-milestone2.properties Fish 1 >/dev/null 2>&1 &
server_id_2=$!
sleep 2
if ! (ps | grep "java" | grep "$server_id_2" >/dev/null 2>&1)
then
	echo "Failed to start the no-role node" && return 1
fi

echo "Running 3 clients as a buyers of Boars"
java -classpath classfiles -Djava.rmi.server.codebase=file:classfiles/ Client 1 ../src/config-milestone2.properties Boar >/dev/null 2>&1 &
client_id_1=$!
java -classpath classfiles -Djava.rmi.server.codebase=file:classfiles/ Client 2 ../src/config-milestone2.properties Boar >/dev/null 2>&1 &
client_id_2=$!
java -classpath classfiles -Djava.rmi.server.codebase=file:classfiles/ Client 3 ../src/config-milestone2.properties Boar >/dev/null 2>&1 &
client_id_3=$!
sleep 2
if ! (ps | grep "java" | grep "$client_id_1" >/dev/null 2>&1)
then
	echo "Failed to start buyer 1" && return 1
fi

if ! (ps | grep "java" | grep "$client_id_2" >/dev/null 2>&1)
then
	echo "Failed to start buyer 2" && return 1
fi

if ! (ps | grep "java" | grep "$client_id_3" >/dev/null 2>&1)
then
	echo "Failed to start buyer 3" && return 1
fi

sleep 5
# Checking if multiple buyers (more than two) found the replies
replies=$(grep -o 'Randomly picking' *client.log | wc -l)
if [ "$replies" -gt 1 ]
then
    echo "Multiple buyers received replies from the seller that has only one item."
fi

# Checking if one buyer received the item
if grep -Fq 'Bought product Boar' *client.log
then
    echo "One buyer bought the item"
    echo "Test case 2 passed."
else
    echo "Test case 2 Failed."
    return 1
fi
kill $server_id $server_id_2 $client_id_1 $client_id_2 $client_id_3 >/dev/null 2>&1 || echo "No processes to delete."
sleep 2

echo "All tests passed"