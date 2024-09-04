package de.tuberlin.ise.privacy.sar.graph.edges;

import java.util.ArrayList;

import org.jgrapht.graph.DefaultEdge;

import de.tuberlin.ise.privacy.sar.graph.nodes.SarNode;

public class SarEdge extends DefaultEdge{
    EdgeType type;

    ArrayList<String> attributes; //optional, for Iteration 3: Knowledge Discovery

    public SarEdge(EdgeType type){
        super();
        this.type = type;
        attributes = new ArrayList<>();
    }

    public void setAttribute(String attribute) {
        attributes.add(attribute);
    }

    public EdgeType getType(){
        return type;
    }

    public SarNode getSourceNode(){
        return (SarNode) this.getSource();
    }

    public SarNode getTargetNode(){
        return (SarNode) this.getTarget();
    }

    public String toString(){
        return "Edge: " + this.getSource() + " " + type + " " + this.getTarget() + " ";
    }
}
