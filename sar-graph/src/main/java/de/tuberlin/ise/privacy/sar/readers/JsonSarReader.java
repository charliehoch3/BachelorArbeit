package de.tuberlin.ise.privacy.sar.readers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import com.fasterxml.jackson.databind.JsonNode;
import de.tuberlin.ise.privacy.sar.CSVConverter;
import de.tuberlin.ise.privacy.sar.graph.SarGraph;
import de.tuberlin.ise.privacy.sar.graph.nodes.*;
import org.apache.commons.lang3.tuple.Pair;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.json.simple.JSONArray;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import de.tuberlin.ise.privacy.sar.graph.edges.EdgeType;
import de.tuberlin.ise.privacy.sar.graph.edges.SarEdge;
import de.tuberlin.ise.privacy.sar.utils.SarFileUtils;

public class JsonSarReader {
    SarGraph graph = new SarGraph();
    ArrayList<String> daten = new ArrayList<String>();

    // hier
    Set<Pair<String, String>> parentToElement = new HashSet<>();

    /**
     * read single json file,
     * that contains an array OR a JSON object
     *
     * @param file in json format
     * @return SAR (sub-) graph with content of file
     */
    public SarGraph readJson(File file) {
        System.out.println("Reading json");
        try {
            FileReader fileReader = new FileReader(file);
            JSONParser jsonParser = new JSONParser();
            Object parsedFile = jsonParser.parse(fileReader);

            SarNode root = jsonToNode(null, parsedFile, SarFileUtils.createID());
            graph.addNode(root);
            graph.setRootNode(root);
            System.out.println("root: " + graph.getRootNode());
//            addJsonToNode(null, parsedFile);
//            JSONAware jsonObject = convertObjToJson(parsedFile);
//            convertObjToJsonObject(null, jsonObject);

        } catch (FileNotFoundException e) {
            System.err.println("File not found! Please try again/Check path!");
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            System.err.println("Something went wrong reading file" + file.getAbsolutePath());
            e.printStackTrace();
            return null;
        } catch (ParseException e) {
            System.err.println("Something went wrong parsing JSON file " + file.getAbsolutePath());
            e.printStackTrace();
            return null;
        } catch (NullPointerException e) {
            System.err.println("Nullpointer exception, maybe the outer structure is not an Object? in: " + file.getAbsolutePath());
            e.printStackTrace();

        }
        //System.out.println("LOTTES LISTE");
        //System.out.println(daten);
        //System.out.println("LOTTE ENDE");
        System.out.println("Carstens Liste");
        System.out.println(parentToElement);
        System.out.println("Carstens ende");

        CSVConverter.csvBuilder(parentToElement, file.getName());

        return graph;
    }


    private SarNode jsonToNode(SarNode parent, Object child, String id) {

        //System.out.println(child.getClass().toString());
        if (child instanceof JSONObject jso) {
            MapNode currentNode = new MapNode(id, parent !=null ? parent.getName() + "_" + id : "" + id); //SarFileUtils.createID(id));
            graph.addVertex(currentNode);


            jso.forEach((key, value) -> {
//                String colStr = currentNode.getId() + "_" + key.toString();
                String colStr = key.toString();
                SarNode childNode = jsonToNode(currentNode, value, key.toString());
                graph.addVertex(childNode);
                graph.addEdge(currentNode, childNode, new SarEdge(EdgeType.CONTAINS));
                if(!daten.contains(key.toString())){
                    daten.add(key.toString());
                }
                // hier
                if(parent != null)
                    parentToElement.add(Pair.of(currentNode.getId(), colStr));


                // collect node attributes in parent node
                if (parent != null) {
                    SarNode colNode = graph.getChildByNameOrNull(parent, colStr);
                    if (colNode == null) {
                        colNode = new MapNode(colStr, SarFileUtils.createID(colStr));
                        graph.addVertex(colNode);
                        graph.addContainsEdge(parent, colNode);
                    }
                    graph.addContainsEdge(colNode, childNode);
                }
            });
            return currentNode;
        } else if (child instanceof JSONArray jsa) {
            ListNode currentNode = new ListNode("list",  id);
            graph.addVertex(currentNode);
            jsa.forEach(value -> {
                // Hier id mit lesbarem namen statt zahl verwenden
                SarNode childNode = jsonToNode(currentNode, value, parent != null ? parent.getName() + "_" + id : id); //SarFileUtils.createID());
                graph.addVertex(childNode);
                SarEdge edge = new SarEdge(EdgeType.CONTAINS);
                graph.addEdge(currentNode, childNode, edge);
            });
            return currentNode;
        } else {
            // TODO CARSTEN disable to skip value nodes
            ValueNode currentNode = new ValueNode("value", SarFileUtils.createID(id), child);
            graph.addVertex(currentNode);
            return currentNode;
        }
    }
}
