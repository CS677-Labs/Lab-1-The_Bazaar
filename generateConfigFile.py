# Script to generate config file given details of N nodes and value of K (number of neighbors)
#
# Will take the values of N, K driver-file, and config-file as input in the command line args
#
# Usage - python generateConfigFile.py <N> <K> <driver-path> <config-path>
# Example - python generateConfigFile.py 10 4 /tmp/driverFile /tmp/config.properties
#   
# Params :
#       N - Number of nodes
#       K - Number of neighbors per node
#       driver-file -   Full path of the driver file.
#                       The driver file is a file with the IPs of the machines to be used as input
#                       Will write the generated output to a config file
#                       Example contents of a river file :
#                       http://127.0.0.1
#                       http://172.2.3.4
#                       http://172.123.323.23
#
#       config-file - Full path to the config file that is to be generated
#
# Sample output for N=5 and K=2 with the above driver file.
# 0=http://127.0.0.1:5000,1,4
# 1=http://172.2.3.4:5001,2,0
# 2=http://172.123.323.23:5002,3,1
# 3=http://127.0.0.1:5003,4,2
# 4=http://172.2.3.4:5004,0,3


from array import *
from random import *
import sys

def createNetwork(N, K) :
    if K>=N:
        raise ValueError('K must be less than N')

    neighbors = []
    for i in range(N) :
        neighborsOfi = []
        neighborsOfi.append(i+1 if i+1 < N else 0) 
        neighborsOfi.append(i-1 if i-1 >= 0 else N-1)
        neighbors.append(neighborsOfi)

    for i in range (2,K) :
        for node in range(N) :
            if(len(neighbors[node]) > i) :
                continue

            randomNeighbor = randrange(N)
            numTries = 0
            while len(neighbors[randomNeighbor]) > i or (randomNeighbor in neighbors[node] or randomNeighbor == node) and numTries < 2*N :
            # If the randomly generated neighbor already has at least i+1 neighbors
                randomNeighbor = randrange(N)
                numTries += 1

            if len(neighbors[randomNeighbor]) <= i and len(neighbors[node]) <= i and randomNeighbor not in neighbors[node] and randomNeighbor != node :
                neighbors[node].append(randomNeighbor)
                neighbors[randomNeighbor].append(node)

    return neighbors

N = int(sys.argv[1])
K = int(sys.argv[2])
configFilePath = sys.argv[3]
newConfigFilePath = sys.argv[4]

neighbors = createNetwork(N, K)
machines = []
with open(configFilePath, 'r') as inputFile :
    for line in inputFile :
        lineWithoutSpaces = line.strip()
        if len(lineWithoutSpaces) < 9 :
            continue

        if lineWithoutSpaces.find("http") == -1 :
            lineWithoutSpaces = "http://" + lineWithoutSpaces

        while lineWithoutSpaces.rfind("/") == (len(lineWithoutSpaces) - 1) :
            lineWithoutSpaces = lineWithoutSpaces[:-1]
        
        machines.append(lineWithoutSpaces)
        
m = 0
port = 5000
with open(newConfigFilePath, 'w') as outputFile :
    for i in range(N) :
        line = str(i) + "=" + machines[m] + ":" + str(port)
        for neighbor in neighbors[i] :
            line = line + "," + str(neighbor)
        line = line + "\n"
        outputFile.write(line)
        port += 1
        m = (m + 1) % len(machines)

print("Config file " + newConfigFilePath + " generated.")
