package de.tuberlin.ise.privacy.sar.utils;

import de.tuberlin.ise.privacy.sar.graph.SarGraph;
import de.tuberlin.ise.privacy.sar.graph.edges.SarEdge;
import de.tuberlin.ise.privacy.sar.graph.nodes.SarNode;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.DefaultAttribute;
import org.jgrapht.nio.dot.DOTExporter;
import org.jgrapht.nio.json.JSONExporter;

import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;

public class SarExporter {

    private DefaultDirectedGraph g;

    public SarExporter(DefaultDirectedGraph g) {
        this.g = g;
    }

    public String dotExportAsString() {
        // https://jgrapht.org/guide/UserOverview

        DOTExporter<SarNode, SarEdge> exporter =
                new DOTExporter<>(v -> "\""+v.getId()+"\"");

        exporter.setVertexAttributeProvider((v) -> {
            Map<String, Attribute> map = new LinkedHashMap<>();
            map.put("label", DefaultAttribute.createAttribute(v.toString()));
            map.put("color", DefaultAttribute.createAttribute(getNodeColor(v)));
            return map;
        });

        exporter.setGraphAttributeProvider(() -> {
            Map<String, Attribute> map = new LinkedHashMap<>();
            map.put("layout", DefaultAttribute.createAttribute("sfdp"));
            map.put("overlap", DefaultAttribute.createAttribute("prism"));
            return map;
        });

        Writer writer = new StringWriter();
        exporter.exportGraph(g, writer);
        String dot = writer.toString();
//        System.out.println(s);
        return dot;
    }

    public static boolean filterNode(SarNode node) {
        return true;
    }

    public static String getNodeColor(SarNode node) {
        String klass = node.getClass().getSimpleName();
//        System.out.println(klass);
        switch(klass) {
            case "FolderNode": return "teal";
            case "FileNode": return "blue";
            case "ValueNode": return "green";
            case "MapNode": return "orange";
            case "ListNode": return "red";
            default: return "black";
        }
    }

    public void dotExportToPath(String path) {
                                Path dotPath = Paths.get(path);
        try {
            String ex = dotExportAsString();
            Files.writeString(dotPath, ex);
        } catch (IOException e) {
            System.out.println("could not write graphviz file, maybe create demo directory?");
        }
    }

        /**
     * @param graph
     * @param path  sould be the path to directory, excluding the name (will be added to sargraph.json)
     * @return
     */
    public static void jsonExportToPath(SarGraph graph, String path) {
        FileWriter file;
        try {
            file = new FileWriter(path + "sargraph.json");
            JSONExporter<SarNode, SarEdge> exporter = new JSONExporter<SarNode, SarEdge>();
            //TODO exporter.exportGraph(graph, file);
            file.close();
        } catch (IOException e) {
            System.err.println("Could not convert Sar to Json \n  " + e.getMessage());
        }
        //System.out.println("Written SAR Graph to " + path);
    }


}
