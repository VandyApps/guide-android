import sys
import json

# takes two json containing an array of Nodes and merge the list.
# copy this script file into a folder that also contains a "nodes.json" and
# a "source.json"
# Everything in Source will be appended at the end of Nodes, with the id
# appropriately modified
# Do not use this to append anything to the nodes.json generated from
# places.json, because then the id would be messed up
nodesJson = open("nodes.json",'r')
sourceJson = open("source.json",'r')

nodesList = json.load(nodesJson)
nodesJson.close()

# get the last node's id
lastId = nodesList[len(nodesList) - 1]["id"]

sourceList = json.load(sourceJson)
sourceJson.close()

for source in sourceList:
	lastId = lastId + 1
	source["id"] = lastId
	nodesList.append(source)


mergedJson = open("nodes.json", 'w')
json.dump(nodesList, mergedJson, False, True, True, True, None, 3)

