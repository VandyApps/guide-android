import sys
import json

nodesjson = open("nodes.json", 'w')

source = open("places.json")

placeslist = json.load(source)
nodeslist = []

for place in placeslist:
	node = {}
	node["id"] = place["id"]
	node["latitude"] = place["latitude"]
	node["longitude"] = place["longitude"]
	node["neighbours"] = []
	nodeslist.append(node)

json.dump(nodeslist, nodesjson, False, True, True, True, None, 3)

