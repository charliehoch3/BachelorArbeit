package de.tuberlin.ise.privacy.sar.analyzers;

import de.tuberlin.ise.privacy.sar.graph.SarGraph;
import de.tuberlin.ise.privacy.sar.graph.edges.SarEdge;
import de.tuberlin.ise.privacy.sar.graph.nodes.*;
import org.jgrapht.alg.scoring.ClosenessCentrality;
import org.jgrapht.traverse.BreadthFirstIterator;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SarGraphAnalyzer {


    private SarGraph graph;

    public SarGraphAnalyzer(SarGraph graph) {
        this.graph = graph;
    }

    public void analyze() {
        BreadthFirstIterator<SarNode, SarEdge> iterator = new BreadthFirstIterator<>(graph);
        ClosenessCentrality<SarNode, SarEdge> closenessCentrality = new ClosenessCentrality<>(graph);

        while (iterator.hasNext()) {
            SarNode node = iterator.next();
            node.addStat("depth", iterator.getDepth(node));
            node.addStat("nodeType", node.getClass().getSimpleName());
            node.addStat("closenessCentrality", closenessCentrality.getVertexScore(node));
            node.addStat("totalAncestors", graph.getAncNodes(node).length);
            node.addStat("depth", iterator.getDepth(node));

            // could not get it working, disabling for now (kw)
            //            node.addStat("height", getNodeHeight(node));


            if (node.getClass().equals(ValueNode.class)) {

                //TODO good stats? depends
                //TODO hook in NER here
            } else if (node instanceof StructureNode) {
//                setFileGraphStats(node, );
                StructureNode strNode = (StructureNode) node;
                if (node.getClass().equals(ListNode.class)) {
                    //TODO Analyse: only values included? if so, values of same type? if so, min, max, avg (Number, StringLength), most common n gram

                } else if (node.getClass().equals(MapNode.class)) {
                    // Schema Discovery: are all subtrees of same structure, if so, are there non-contain labels that build a structure?
                }

                // if node only contains valuenodes as children, we can run some statistics

                // todo: the more likely group in real life is a list of objects with the same attributes
                //       and then make a group of all these attributes. how to detect? what if an attribute is only
                //       present in 90% of objects?

                SarNode[] children = graph.getAncNodes(node);
                if (children.length > 0 && Arrays.stream(children).allMatch(n -> n instanceof ValueNode)) {
                    node.addStat("groupParent", true);

                    List<ValueNode> vns = Arrays.stream(children)
                            .map(n -> (ValueNode) n)
//                            .map(n -> n.getClass().getSimpleName())
//                            .peek(c -> System.out.println(c))
                            .collect(Collectors.toList());
                    NodeGroupAnalyzer nga = new NodeGroupAnalyzer(node, vns);
                    nga.analyze();
                }
            } else if (node instanceof FileNode) {
                //graphstats
//                setFileGraphStats(node, closenessCentrality.getVertexScore(node));
//                file.addStat("depth", )
                // height: max number of levele
            } else if (node instanceof FolderNode) {
                //TODO good stats? graph? but value?
            }


        }
    }

    public void printAllStats() {
        System.out.println("STATS: ");
        graph.vertexSet().forEach(node -> {
            if (!node.getStats().isEmpty()) {
                System.out.println(node.getStats());
            }
        });
    }


}
