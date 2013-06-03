import sys
import json

# Open the .json file containing the array of Places
source = open("places.json")
placeslist = json.load(source)
source.close()

# Initiation
nodeslist = []

# start translating
for place in placeslist:
	node = {}
	node["id"] = place["id"]
	node["lat"] = place["latitude"]
	node["lng"] = place["longitude"]
	node["neighbors"] = []
	nodeslist.append(node)

# insert a place holder in preparation for the rest of the dataset
last = {}
last["id"] = 9999
last["lat"] = 0
last["lng"] = 0
last["neighbors"] = []
nodeslist.append(last)

# Dump everything in a new file named nodes.json
nodesjson = open("nodes.json", 'w')
json.dump(nodeslist, nodesjson, False, True, True, True, None, 3)
nodesjson.close()

