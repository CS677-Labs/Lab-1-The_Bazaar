# Script to generate config file given details of N nodes and value of K (number of neighbors)
# Will take N and K and a driver file with key value pairs as inputs. Note: The nodes are 0-indexed.
# Will write the generated output to a config file
# Example contents of a driver file :
# 0=http://127.0.0.1:5000
# 1=http://127.0.0.1:5001
# 2=http://127.0.0.1:5002
# 3=http://127.0.0.1:5003
# 4=http://127.0.0.1:5004

# Sample output for N=5 and K=2 with the above driver file.
# 0=http://127.0.0.1:5000,1,4
# 1=http://127.0.0.1:5001,2,0
# 2=http://127.0.0.1:5002,3,1
# 3=http://127.0.0.1:5003,4,2
# 4=http://127.0.0.1:5004,0,3


from array import *
from random import *

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


N = int(input("Enter the number of Nodes (N) : "))
configFilePath = input("\n\nEnter the path to the driver file that contains details of all {N} nodes.\nThis file should contain {N} lines and each line should be of the type <NodeNum>=<URL:Port> : ".format(N = N))
K = int(input("\n\nEnter the number of neighbors (K) : "))
neighbors = createNetwork(N, K)
newConfigFilePath = str(N) + "_nodes_" + str(K) + "_neighbors__Config.properties"
with open(newConfigFilePath, 'w') as outputFile, open(configFilePath, 'r') as inputFile :
    for line in inputFile :
        newLine = line.strip('\n')
        nodeNum = int(newLine.split("=")[0])
        for neighbor in neighbors[nodeNum] :
            newLine = newLine + "," + str(neighbor)
        newLine = newLine + "\n"
        outputFile.write(newLine)

print("Config file " + newConfigFilePath + " generated.")
