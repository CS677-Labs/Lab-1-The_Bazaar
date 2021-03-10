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
echo "Running test case 1 -> 1 Seller sells Boar, 1 Seller sells Fish (no role in the current transaction), 3 Buyers buy Boar."

rm *.log.* >/dev/null 2>&1 || echo "No logs to delete"

echo "Running 1 server as the seller of Boars"
java -classpath classfiles -Djava.rmi.server.codebase=file:files/ Server 5 src/config-milestone2.properties Boar 5 &
server_id=$!
sleep 3
if ! (ps | grep " $server_id ")
then
	echo "Failed to start the seller node" && return 1
fi
echo "Running 3 clients as buyers of Boars"
java -classpath classfiles -Djava.rmi.server.codebase=file:files/ Client 1 src/config-milestone2.properties Boar &
client_id=$!
if ! (ps | grep " $client_id ")
then
	echo "Failed to start the client node" && return 1
fi

java -classpath classfiles -Djava.rmi.server.codebase=file:files/ Client 2 src/config-milestone2.properties Boar &
client_id=$!
if ! (ps | grep " $client_id ")
then
	echo "Failed to start the client node" && return 1
fi

java -classpath classfiles -Djava.rmi.server.codebase=file:files/ Client 3 src/config-milestone2.properties Boar &
client_id=$!
if ! (ps | grep " $client_id ")
then
	echo "Failed to start the client node" && return 1
fi
echo "Running 1 server as seller of Fish (Note: The seller of fish has no role in the current transaction)"

java -classpath classfiles -Djava.rmi.server.codebase=file:files/ Server 4 src/config-milestone2.properties Fish 1 &
server_id=$!
if ! (ps | grep " $server_id ")
then
	echo "Failed to start the server node" && return 1
fi

sleep 10
if (grep -Fq "Bought product Boar" client.log) && (grep -Fq "Restocking" server.log)
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
# TODO add test case description
echo "Running test case 2. <...>"
rm *.log.*  >/dev/null 2>&1 || echo "No logs to delete"
echo "Running 1 server as the seller of Boars"
# fixme: Change this approach if required - "Switch to another product else it's always restocked and the second buyer will always have Boars available."
java -classpath classfiles -Djava.rmi.server.codebase=file:files/ Server 5 src/config-milestone2.properties Boar 1 Fish &
server_id=$!

sleep 3
if ! (ps | grep "java" | grep " $server_id ")
then
	echo "Failed to start the seller node" && return 1
fi
echo "Running 3 clients as a buyers of Boars"

java -classpath classfiles -Djava.rmi.server.codebase=file:files/ Client 1 src/config-milestone2.properties Boar &
client_id=$!
if ! (ps | grep " $client_id ")
then
	echo "Failed to start the client node" && return 1
fi

java -classpath classfiles -Djava.rmi.server.codebase=file:files/ Client 2 src/config-milestone2.properties Boar &
client_id=$!
if ! (ps | grep " $client_id ")
then
	echo "Failed to start the client node" && return 1
fi

java -classpath classfiles -Djava.rmi.server.codebase=file:files/ Client 3 src/config-milestone2.properties Boar &
client_id=$!
if ! (ps | grep " $client_id ")
then
	echo "Failed to start the client node" && return 1
fi
echo "Running 1 server as seller of Fish (Note: The seller of fish has no role in the current transaction)"

java -classpath classfiles -Djava.rmi.server.codebase=file:files/ Server 4 src/config-milestone2.properties Fish 1 &
server_id=$!

if ! (ps | grep " $server_id ")
then
	echo "Failed to start the server node" && return 1
fi
sleep 10
if grep -Fq "Could not buy product Boar" client.log
then
    echo "Test case 2 passed."
else
    echo "Test case 2 Failed."
    return 1
fi
kill $server_id $client_id >/dev/null 2>&1 || echo "No processes to delete."
sleep 2

echo "All tests passed"
rm *.log.* >/dev/null 2>&1 || echo "No logs to delete"