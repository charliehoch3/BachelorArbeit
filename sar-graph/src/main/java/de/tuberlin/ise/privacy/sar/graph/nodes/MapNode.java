package de.tuberlin.ise.privacy.sar.graph.nodes;

/**
 * Structure Node representing an object, may only contain named edges
 */
public class MapNode extends StructureNode {


    /**
     * @param name         Name (label) of the node, usually File/Folder name
     * @param id           Unique id for the noder 
     */
    public MapNode(String name, String id) {
        super(name, id);
    }

    @Override
    public String toString() {
        return "{Map #"+ getId() + " " + getName()+ "}";
    }
}
