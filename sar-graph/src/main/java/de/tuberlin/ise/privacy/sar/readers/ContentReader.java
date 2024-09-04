package de.tuberlin.ise.privacy.sar.readers;

import de.tuberlin.ise.privacy.sar.errors.UnknownFileExtensionException;
import de.tuberlin.ise.privacy.sar.graph.SarGraph;
import de.tuberlin.ise.privacy.sar.graph.edges.EdgeType;
import de.tuberlin.ise.privacy.sar.graph.edges.SarEdge;
import de.tuberlin.ise.privacy.sar.graph.nodes.ContentNode;
import de.tuberlin.ise.privacy.sar.graph.nodes.FileNode;
import de.tuberlin.ise.privacy.sar.graph.nodes.SarNode;
import de.tuberlin.ise.privacy.sar.graph.nodes.ValueNode;
import de.tuberlin.ise.privacy.sar.utils.FileType;
import de.tuberlin.ise.privacy.sar.utils.SarFileUtils;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultDirectedGraph;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Set;

import static java.nio.file.Files.readString;

public class ContentReader {

    private SarGraph graph;

    public ContentReader(SarGraph graph) {
        this.graph = graph;
    }

    /**
     * read all file nodes and construct new subgraphs based on file contents
     */
    public void readAllFileContents() {
        Object[] sarNodes = graph.vertexSet().toArray(); //could use graph traversals if we had time for this...
        for (Object nodeObj : sarNodes) {
            SarNode node = (SarNode) nodeObj;
            if (node.getClass().equals(FileNode.class)) {
                FileNode fileNode = (FileNode) node;
                addContentForFile(new File(fileNode.getId()));
            }
        }
    }


    private void addContentForFile(File file) {
        System.out.println("Adding content to graph from file " + file.getAbsolutePath());

        SarNode hookNode = graph.getFileToNode().get(file.getAbsolutePath());
        SarGraph contentGraph = fileToGraph(file);
        Graphs.addGraph(graph, contentGraph);
        graph.addEdge(hookNode, contentGraph.getRootNode(), new SarEdge(EdgeType.CONTAINS));

        System.out.println("Add content for file " + file.getAbsolutePath() + " finished.");
    }

    public static SarGraph fileToGraph(File file) {
        String fileName = file.getName();
        FileType fileType = FileType.getType(file);

        SarGraph fileGraph = new SarGraph(); //TODO: doesnt work, just for testing

        try {
            switch (fileType) {
                case JSON:
                    JsonSarReader json = new JsonSarReader();
                    fileGraph = json.readJson(file);
                    break;
                case CSV:
                    fileGraph = (new CsvSarReader()).readCsv(file);
                    break;
                case HTML:
                    HtmlSarReader html = new HtmlSarReader();
                    fileGraph = html.readHtml(file);
                    break;
                case TEXT:
                    fileGraph.addVertex(textFileToNode(file));
                    break;
//            case UNKNOWN:
//                throw new UnknownFileExtensionException("Couldn't handle file: " + fileName + " Because the file type is unknown");
                default:
                    System.out.println("File " + fileName + " has an unusual file type, which we do not support (yet). Consequently, its content is not considered in the graph");
                    break;
            }

            assert(fileGraph.getRootNode() != null);

        } catch (IOException e) {
//            throw new RuntimeException(e);
            System.err.println("Error Reading file " + fileName + ": " + e);
        }

        return fileGraph;
    }

    /**
     * return a graph containing a single value node with the text (for now)
     */
    public static ValueNode textFileToNode(File f) throws IOException {

        String content = readString(f.toPath());
        ValueNode n = new ValueNode(f.getName() + ".content", SarFileUtils.createID(), content);

        System.out.println("Read text: " + content); // debug
        return n;
    }


}
