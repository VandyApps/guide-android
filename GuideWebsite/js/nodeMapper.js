function Node(id, lat, lng) {
  this.id = id;
  this.lat = lat;
  this.lng = lng;

  // Array of neighbor ids
  this.neighbors = new Array();
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

// Map of markers to nodes

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
    placeMarker(event.latLng);
  });
}

function placeMarker(location) {
  var marker = new google.maps.Marker({
      position: location,
      map: map,
      title: curNodeId.toString()
  });

  var node = new Node(curNodeId, marker.position.lat(), marker.position.lng());
  nodes[node.id] = node;

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

  // google.maps.event.addListener(marker, 'dblclick', function() {

  curNodeId++;
}

function removePolyline(id1, id2) {
  for (var i=0; i<polylineHolders.length; i++) {
    if (polylineHolders[i] != null && 
        (polylineHolders[i].id1 == id1 || polylineHolders[i].id1 == id2) &&
        (polylineHolders[i].id2 == id1 || polylineHolders[i].id2 == id2)) {
      // Remove that polyline from the map
      polylineHolders[i].polyline.setPath([]);
      polylineHolders[i] = null;
      return;
    }
  }
}

function writeJSON() {
  console.log(JSON.stringify(nodes.slice(STARTING_NODE_ID)));
}

google.maps.event.addDomListener(window, 'load', initialize);
