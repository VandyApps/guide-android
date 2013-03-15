from sys import argv
import json

# merge_list.py [ORIGINAL] [EXTRA EXTRA EXTRA ...]
# takes jsons containing an array of Nodes and merge the list.
# Everything in EXTRAs will be appended at the end of ORIGINAL, with the ids
# appropriately modified

# Open ORIGINAL on which EXTRA will be appended
nodesJson = open(argv[1], 'r')
nodesList = json.load(nodesJson)
nodesJson.close()

for extraName in argv[2:]:
    # get the last node's id
    lastId = nodesList[len(nodesList) - 1]["id"]
    
    # Open the EXTRA which will be appended to ORIGINAL
    sourceJson = open(extraName, 'r')
    sourceList = json.load(sourceJson)
    sourceJson.close()
    
    # Appending
    for source in sourceList:
	    lastId = lastId + 1
	    source["id"] = lastId
	    nodesList.append(source)

# dump everything in ORIGINAL
mergedJson = open(argv[1], 'w')
json.dump(nodesList, mergedJson, False, True, True, True, None, 3)

