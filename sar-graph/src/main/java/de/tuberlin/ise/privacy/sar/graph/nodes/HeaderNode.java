package de.tuberlin.ise.privacy.sar.graph.nodes;

public class HeaderNode extends StructureNode implements Comparable<Object> {

    private String content;
    private String htmlTagName;

    public HeaderNode(String name, String id, String content, String htmlTagName) {
        super(name, id);
        this.content = content;
        this.htmlTagName = htmlTagName;
    }

    @Override
    public String toString() {
        return "[HN #" + getId() + " " + htmlTagName + "=" + content + "]";
    }

    public String getContent() {
        return content;
    }

    public String getHtmlTagName() {
        return htmlTagName;
    }

    @Override
    public int compareTo(Object o) {
        if(o instanceof HeaderNode other) {
            return this.htmlTagName.compareTo(other.htmlTagName);
        }
        return 0;
    }
}
