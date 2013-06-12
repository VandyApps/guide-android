function Node(id, lat, lng, neighbors) {
  this.id = id;
  this.lat = lat;
  this.lng = lng;

  // Array of neighbor ids
  if (neighbors == null) {
    this.neighbors = new Array();
  } else {
    this.neighbors = neighbors;
  }
}

function createNodeCopy(node) {
  return new Node(node.id, node.lat, node.lng, node.neighbors);
}
  

// Holds a reference to the polyline connecting markers of ids 1 and 2
function PolylineHolder(polyline, id1, id2) {
  this.polyline = polyline;
  this.id1 = id1;
  this.id2 = id2;
}

var map;
var node1;
var STARTING_NODE_ID = 10000;
var curNodeId = STARTING_NODE_ID;

// Array of Node objects
var nodes = new Array(); 

// Array of markers (used to clear map)
var markerArray = new Array();

// Array of PolylineHolders
var polylineHolders = new Array();

function initialize() {
  var mapOptions = {
    center: new google.maps.LatLng(36.14572, -86.80446),
    zoom: 15,
    mapTypeId: google.maps.MapTypeId.HYBRID
  };
  map = new google.maps.Map(document.getElementById("map-canvas"),
      mapOptions);

  google.maps.event.addListener(map, 'click', function(event) {
    var marker = placeMarker(event.latLng, curNodeId);
    var node = new Node(curNodeId, marker.position.lat(), marker.position.lng(), new Array());
    nodes[curNodeId] = node;
    curNodeId++;
  });
}

function placeMarker(location, id) {
  var marker = new google.maps.Marker({
      position: location,
      map: map,
      title: id.toString()
  });
  if (id < 10000) {
    marker.setIcon("http://maps.google.com/mapfiles/ms/icons/blue-dot.png");
  }
  markerArray.push(marker);

  // If a marker is clicked, start a linking operation
  google.maps.event.addListener(marker, 'click', function() {
    if (node1 == null) {
      node1 = nodes[+(marker.title)];
    } else {
      var node2 = nodes[+(marker.title)];

      var ix = node1.neighbors.indexOf(node2.id);
      if(ix != -1) {
        // If the markers were already adjacent, make them unadjacent
        node1.neighbors[ix] = null;
        ix = node2.neighbors.indexOf(node1.id);
        if (ix != -1) {
          node2.neighbors[ix] = null;
        }
        removePolyline(node1.id, node2.id);
      } else if (node1.id != node2.id) {
        // Draw a line on the map connecting the two markers
        var latlng1 = new google.maps.LatLng(node1.lat, node1.lng);
        var latlng2 = new google.maps.LatLng(node2.lat, node2.lng);
        var connector = new google.maps.Polyline({
            path: [latlng1, latlng2],
            map: map
        });

        polylineHolders.push(new PolylineHolder(connector, node1.id, node2.id));

        // Add the two markers to each others' adjacency lists
        node1.neighbors.push(node2.id);
        node2.neighbors.push(node1.id);
      }

      node1 = null;
    }
  });
  return marker;
}

function deleteOverlays() {
  for (i in markerArray) {
    markerArray[i].setMap(null);
  }
  for (i in polylineHolders) {
    polylineHolders[i].polyline.setMap(null);
  }
  markerArray.length = 0;
  polylineHolders.length = 0;
}

function removePolyline(id1, id2) {
  var poly = getLinkingPolyline(id1, id2);
  if (poly != null) {
    // Remove that polyline from the map
    poly.holder.polyline.setPath([]);
    polylineHolders[poly.ix] = null;
  }
}

function writeJSON() {
  var json = "[";
  for (var i in nodes) {
    json += JSON.stringify(nodes[i]) + ",";
  }
  json += "]";
  console.log(json);
}

// Reads the data from the savedNodes object if it is defined and resets the
// state of the script
function readJSON() {
  if (typeof(savedNodes) === "undefined") return;

  deleteOverlays();
  node1 = null;
  nodes.length = 0;

  var max = 0;
  for (var i=0; i<savedNodes.length; i++) {
    var node = createNodeCopy(savedNodes[i]);
    var id = savedNodes[i]["id"];
    nodes[id] = node;

    placeMarker(new google.maps.LatLng(node.lat, node.lng), id);
    if (max < id) max = id;
  }
  curNodeId = max + 1;

  for (var i in nodes) {
    var node = nodes[i];
    for (var j in node.neighbors) {
      if(!isLinked(node.id, node.neighbors[j])) {
        var node2 = nodes[node.neighbors[j]];
        var latlng1 = new google.maps.LatLng(node.lat, node.lng);
        var latlng2 = new google.maps.LatLng(node2.lat, node2.lng);
        var connector = new google.maps.Polyline({
            path: [latlng1, latlng2],
            map: map
        });

        polylineHolders.push(new PolylineHolder(connector, node.id, node2.id));
      }
    }
  }
}

// Returns an object containing the polyline and and its location in the
// polylineHolders array that links the nodes with the given ids.
// Returns null if there is no such polyline.
function getLinkingPolyline(id1, id2) {
  for (var i in polylineHolders) {
    if(isLinkingPolyline(id1, id2, polylineHolders[i])) {
      return {"ix": i, "holder": polylineHolders[i]};
    }
  }
  return null;
}

function isLinked(id1, id2) {
  for (var i in polylineHolders) {
    if(isLinkingPolyline(id1, id2, polylineHolders[i])) return true;
  }
  return false;
}

function isLinkingPolyline(id1, id2, polyline) {
  return polyline != null && 
        (polyline.id1 == id1 || polyline.id1 == id2) &&
        (polyline.id2 == id1 || polyline.id2 == id2);
}

  
google.maps.event.addDomListener(window, 'load', initialize);
