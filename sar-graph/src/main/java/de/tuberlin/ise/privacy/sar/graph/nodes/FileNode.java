package de.tuberlin.ise.privacy.sar.graph.nodes;


import de.tuberlin.ise.privacy.sar.utils.FileType;

import java.io.File;
import java.util.HashMap;

public class FileNode extends SarNode{
    protected String path;
    protected FileType type;

    // only available for (semi-)structured content
    protected File content;

    // only available after SarGraph.analyze()
    protected HashMap<String, Object> stats;


    /**
     * Constructor for Semistructured Files
     * @param name
     * @param path
     * @param type
     * @param file
     */
    public FileNode(String name, String path, FileType type, File file) {
        super(name, path);
        this.type = type;
        this.path = path;
        this.content = file;
    }

    /**
     * Constructor for unstructured Files
     * @param name
     * @param path
     * @param type
     */
    public FileNode(String name, String path, FileType type) {
        super(name, path);
        this.type = type;
        this.path = path;
    }

    public boolean isText(){
        return (type.equals(FileType.TEXT));
    }

    public boolean isStructured(){
        return (type.is(FileType.STRUCTURED));
    } 

    public boolean isSemiStructured(){
        return (type.is(FileType.SEMI_STRUCTURED));
    }

    public boolean isUnstructured(){
        return (type!=FileType.EMPTY || type!=FileType.STRUCTURED || type!=FileType.SEMI_STRUCTURED);
    }
}
