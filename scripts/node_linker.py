#########################################################################
# An REPL program for adding adjacency data to the Node list. 
# PLEASE MAKE BACKUP FOR THE JSON, OR YOU MIGHT LOSE YOUR DATA
#
# Usage:
#       nodelinker.py [INPUT_FILE] [OUTPUT_FILE]
#
#   where INPUT_FILE is the file containing the nodes Json
#
# the REPL will start, and you can start adding and removing the 
# adjacency data like this:
#
#           l 12 34
#
#           s 12 34
#
# where l and s are keywords to indicate linkage and unlinkage respectively,
# and 12 and 34 are the IDs of two nodes that you want to be linked.
# Press Enter, and the REPL will be ready to accept another input after
# it has modified the list.
#
# Press Enter to exit the loop.
#
# If you want the same facility for removing adjacency data, just tell
# me, or make another script yourself by copying this one and modifying
# key parts
#########################################################################

from sys import argv
import json

print "Starting node_linker"

# Open the input file
inFile = open(argv[1], 'r')
NodeList = json.load(inFile)
inFile.close()

print "I Can hAz " + str(len(NodeList)) + " data points."
print "Please input your data"

while True:
    data = raw_input("neighbours: ")
    if data == "":
        break
    
    tokens = data.split()
    
    if tokens[0] == 'l':
        for node in NodeList:
            if node["id"] == int(tokens[1]):
                node["neighbours"].append(int(tokens[2]))
        
            if node["id"] == int(tokens[2]):
                node["neighbours"].append(int(tokens[1]))
        
    elif tokens[0] == 's':
        for node in NodeList:
            if node["id"] == int(tokens[1]):
                node["neighbours"].remove(int(tokens[2]))
        
            if node["id"] == int(tokens[2]):
                node["neighbours"].remove(int(tokens[1]))


# Finalize the json and save it
outFile = open(argv[2], 'w')
json.dump(NodeList, outFile, False, True, True, True, None, 3)
outFile.close()

print "Ending node_linker"




