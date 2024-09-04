package de.tuberlin.ise.privacy.sar.graph.edges;

public class LabeledEdge extends SarEdge{
    String label;
    String keywords; //TODO might add keywords to labels during knowledge discovery


    public LabeledEdge(String label){
        super(EdgeType.VALUE);
        this.label = label;
    }

    public String getLabel(){
        return label;
    }
    
    public String toString(){
        return "Edge " + label+": " + this.getSource() + " " + type + " " + this.getTarget() + " ";
    }
}
