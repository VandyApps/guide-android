
package edu.vanderbilt.vm.gxjc;

import java.io.File;
import java.io.FileWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.json.JSONWriter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class App {

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("Arguments: <xmlfilename> <jsonfilename>");
            System.exit(1);
        }

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document;
        document = builder.parse(new File(args[0]));
        document.getDocumentElement().normalize();

        NodeList featureList = document.getElementsByTagName("feature");

        FileWriter fw = new FileWriter(new File(args[1]));
        JSONWriter jw = new JSONWriter(fw);
        jw.array();

        // Building IDs started at 11 at the time this program was written
        int id = 11;

        for (int i = 0; i < featureList.getLength(); ++i) {
            Node node = featureList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                // Node is an Element
                Element element = (Element)node;
                Node coordNode = element.getElementsByTagName("coordinates").item(0);
                Node typeNode = element.getElementsByTagName("TYPE").item(0);
                Node nameNode = element.getElementsByTagName("FACILITY_NAME").item(0);
                Node urlNode = element.getElementsByTagName("FACILITY_URL").item(0);
                Node remarksNode = element.getElementsByTagName("FACILITY_REMARKS").item(0);

                jw.object().key("id").value(id++);

                if (nameNode != null && nameNode.getTextContent().length() != 0) {
                    jw.key("name").value(WordUtils.capitalizeFully(nameNode.getTextContent()));
                } else {
                    jw.key("name").value("");
                }

                if (typeNode != null && typeNode.getTextContent().length() != 0) {
                    jw.key("category").array().value(WordUtils.capitalizeFully(typeNode.getTextContent())).endArray();
                } else {
                    jw.key("category").array().value("").endArray();
                }

                jw.key("hours").value("");

                if (remarksNode != null && remarksNode.getTextContent().length() != 0) {
                    jw.key("placeDescription").value(remarksNode.getTextContent());
                } else {
                    jw.key("placeDescription").value("");
                }

                if (urlNode != null && urlNode.getTextContent().length() != 0) {
                    jw.key("imagePath").value(
                            "https://www.vanderbilt.edu/map/" + StringUtils.lowerCase(urlNode.getTextContent()));
                } else {
                    jw.key("imagePath").value("");
                }
                
                jw.key("videoPath").value("");
                
                if (coordNode != null && coordNode.getTextContent().length() != 0) {
                    String[] vals = coordNode.getTextContent().split(",");
                    Double x = Double.parseDouble(vals[0]);
                    Double y = Double.parseDouble(vals[1]);
                    double[] latlon = EPSG900913ToLatLon(x, y);
                    jw.key("latitude").value(latlon[0]);
                    jw.key("longitude").value(latlon[1]);
                } else {
                    jw.key("latitude").value("");
                    jw.key("longitude").value("");
                }

                jw.endObject();
            }
        }

        jw.endArray();
        fw.close();
    }
    
    public static double[] EPSG900913ToLatLon(double x, double y) {
        double longitude = x / (6378137.0 * Math.PI / 180);
        double latitude = ((Math.atan(Math.pow(Math.E, (y / 6378137.0))))
                / (Math.PI / 180) - 45) * 2.0;

        return new double[] {latitude, longitude};
    }
    
}
