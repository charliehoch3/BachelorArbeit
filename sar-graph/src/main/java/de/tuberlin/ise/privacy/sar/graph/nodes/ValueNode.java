package de.tuberlin.ise.privacy.sar.graph.nodes;

import de.tuberlin.ise.privacy.sar.utils.DataType;

public class ValueNode extends ContentNode {

    Object content;
    DataType type;


    /**
     * @param name    Name (label) of the node, usually File/Folder name
     * @param id      Unique id for the node
     * @param content Content (value) that should be stored
     */
    public ValueNode(String name, String id, Object content) {
        super(name, id);
        this.content = content;
        inferBasicType();
    }


    public String getContentAString() {
        if(content == null) return "null"; else return content.toString();
    }

    public String toString() {
//        return "(Value_Node_" + getName() + "_of_type_" + type + "_and_value_" + content + ")";
        return "[VN: #" + getId() + " " + getName() + "=" + getContentAString()+ "]";
    }

    private void inferBasicType() {
        if (content instanceof Number) {
            type = DataType.NUMBER;
        } else if (content instanceof Boolean) {
            type = DataType.BOOLEAN;
        } else if (content instanceof String) {

            // todo: less naive URI detection
            if (((String) content).startsWith("http")) {
                type = DataType.URI;
            } else {
                type = DataType.TEXT;
            }

        } else {
            type = DataType.UNKNOWN;
        }
    }

    public DataType getType() {
        return type;
    }

    public Object getContent() {
        return content;
    }
}
