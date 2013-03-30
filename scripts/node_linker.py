#########################################################################
# An REPL program for adding adjacency data to the Node list. 
# PLEASE MAKE BACKUP FOR THE JSON, OR YOU MIGHT LOSE YOUR DATA
#
# Usage:
#       nodelinker.py [INPUT_FILE] [OUTPUT_FILE]
#
#   where INPUT_FILE is the file containing the nodes Json
#
# the REPL will start, and you can start adding the adjacency
# data like this:
#
#       [ 12 , 34 ]
#
# where 12 and 34 are the IDs of two nodes that you want to be linked.
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
    
    # if the input is done correctly, then it should
    # be evalable to a list with length 2
    data_parsed = eval(data)
    # print str(len(data_parsed))
    
    # link that shit up
    for node in NodeList:
        if node["id"] == data_parsed[0]:
            node["neighbours"].append(data_parsed[1])
    
        if node["id"] == data_parsed[1]:
            node["neighbours"].append(data_parsed[0])

# Finalize the json and save it
outFile = open(argv[2], 'w')
json.dump(NodeList, outFile, False, True, True, True, None, 3)
outFile.close()

print "Ending node_linker"




