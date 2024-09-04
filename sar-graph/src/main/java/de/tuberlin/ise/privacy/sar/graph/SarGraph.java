package de.tuberlin.ise.privacy.sar.graph;

import de.tuberlin.ise.privacy.sar.graph.edges.EdgeType;
import de.tuberlin.ise.privacy.sar.graph.edges.SarEdge;
import de.tuberlin.ise.privacy.sar.graph.nodes.SarNode;

import org.jgrapht.Graph;
import org.jgrapht.graph.AsSubgraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.EdgeReversedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;

import java.io.File;
import java.util.*;

public class SarGraph extends DefaultDirectedGraph<SarNode, SarEdge> {
    private DefaultDirectedGraph<SarNode, SarEdge> graph = null;
    private SarNode rootNode = null;
    private Map<String, SarNode> fileToNode = new LinkedHashMap<String, SarNode>();
    String service = null; // TODO: convert to general metadata class

    public SarGraph() {
        super(SarEdge.class);
        graph = this;
        service = "Super SAR Graph";
    }

    public SarGraph(String provider) {
        super(SarEdge.class);
        graph = this;
        service = provider;
    }

    public Map<String, SarNode> getFileToNode() {
        return fileToNode;
    }

//    public SarGraph(String serviceName) {
//        this();
//        service = serviceName;
//    }

//    public SarGraph(File file, String serviceName) {
//        this(serviceName);
//        if (file.isDirectory()) {
//            rootNode = new FolderNode(file.getName(), file.getAbsolutePath(), file);
//            addNode(rootNode);
//            addChilds(file);
//        } else {
//            System.err.println("Argument " + file.getAbsolutePath() + " can not be converted into a graph. Maybe there is no root directory?");
//            return;
//        }
//    }


    public SarNode getRootNode() {
        return rootNode;
    }

    public void setRootNode(SarNode rootNode) {
        this.rootNode = rootNode;
    }

    public void addNode(SarNode node) {
        fileToNode.putIfAbsent(node.getId(), node);
        graph.addVertex(node);
    }

    public void addContainsEdge(File file) {
        File parent = file.getParentFile();
        SarNode child = fileToNode.get(file.getAbsolutePath());
        SarNode parentNode = fileToNode.get(parent.getAbsolutePath());
        SarEdge edge = new SarEdge(EdgeType.CONTAINS);
        if (child != null && parentNode != null) {
            graph.addEdge(parentNode, child, edge);
        } else {
            System.out.println("Could not add edge between source node " + child + " and target node " + parentNode);
        }
    }

    public SarNode getChildByNameOrNull(SarNode startNode, String name) {

        for (SarEdge edge : iterables().outgoingEdgesOf(startNode)) {
            SarNode node = edge.getTargetNode();
//            System.out.println("edge: " + node.getName());
            if (node.getName().equals(name)) {
                return node;
            }
        }

        return null;
    }

    public void addContainsEdge(SarNode source, SarNode target) {
        addEdge(source, target, new SarEdge(EdgeType.CONTAINS));
    }

    public SarNode[] getAncNodes(SarNode node) {
        Set<SarNode> children = getSubSet(node);
        children.remove(node);
        SarNode[] array = new SarNode[children.size()];
        return children.toArray(array);
    }

    Set<SarNode> getSubSet(SarNode node) {
        Set<SarNode> children = new HashSet<SarNode>();
        BreadthFirstIterator<SarNode, SarEdge> iterator = new BreadthFirstIterator<>(graph, node);
        while (iterator.hasNext()) {
            SarNode child = iterator.next();
            if (!child.equals(node)) {
                children.add(child);
            }
        }
        return children;
    }

    private int getNodeDepth(SarNode node, Graph<SarNode, SarEdge> referenceGraph) {
        System.out.println("Sar Node " + node);
        BreadthFirstIterator<SarNode, SarEdge> iterator = new BreadthFirstIterator<SarNode, SarEdge>(referenceGraph);
        //exclude self, otherwise it returns nullpointer for depth = 0
        iterator.next();
        if (iterator.hasNext()) {
            return iterator.getDepth(node);
        } else {
            return 0;
        }
    }

    private int getSubTreeDepth(SarNode node, Graph<SarNode, SarEdge> subGraph) {
        Graph<SarNode, SarEdge> reversedGraph = new EdgeReversedGraph<SarNode, SarEdge>(subGraph);
        System.out.println("Reversed Graph \n" + reversedGraph);
        return getNodeDepth(node, reversedGraph);
    }

    private int getNodeHeight(SarNode node) {
//        node.addStat("closenessCentrality", nodeCentrality);
//        node.addStat("totalAncestors", getAncNodes(node).length);
//        node.addStat("depth", getNodeDepth(node, graph));
        if (graph.outgoingEdgesOf(node).size() > 1) {
            Set<SarNode> subNodes = getSubSet(node);
            Graph<SarNode, SarEdge> subGraph = new AsSubgraph<SarNode, SarEdge>(graph, subNodes);
            System.out.println("Subgraph \n" + subGraph);
//            node.addStat("height", );
            return getSubTreeDepth(node, subGraph);
        } else return 0;

    }

    public String toString() {
        StringBuilder answer = new StringBuilder("Sar graph " + service + " has the following edges: " + graph.edgeSet().size() + "\n");
        for (SarEdge edge : graph.edgeSet()) {
            answer.append(edge.toString());
            answer.append("\n");
        }
        return answer.toString();
    }
}
