package de.tuberlin.ise.privacy.sar.graph.nodes;


/**
 * StructureNode representing a list, may only containt UNNAMED edges
 */
public class ListNode extends StructureNode {

    /**
     * @param name         Name (label) of the node, usually File/Folder name
     * @param id           Unique id for the node
     */
    public ListNode(String name, String id) {
        super(name, id);
    }
}
