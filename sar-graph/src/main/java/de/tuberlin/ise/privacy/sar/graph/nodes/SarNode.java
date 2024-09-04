package de.tuberlin.ise.privacy.sar.graph.nodes;

import java.util.HashMap;

public abstract class SarNode {
    private String name = null;
    private String id; // id = absoulte path
    private HashMap<String, Object> stats;

    /**
     * @param name         Name (label) of the node, usually File/Folder name
     * @param absolutePath Unique id for the node, usually absulute path of file/folder
     */
    SarNode(String name, String absolutePath) {
        this.name = name;
        this.id = absolutePath;
        stats = new HashMap<>();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return "(Sar_Node_" + name + ")";
    }

    public void addStat(String name, Object value) {
        stats.put(name, value);
    }

    public HashMap<String, Object> getStats() {
        return stats;
    }
}
