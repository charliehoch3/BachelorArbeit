package de.tuberlin.ise.privacy.sar.utils;

import java.util.ArrayList;
import java.util.List;

public enum DataType {
    BASIC(null),
        NUMBER(BASIC),
        BOOLEAN(BASIC),
        TEXT(BASIC),
            CHARACTER(TEXT),
    ADVANCED(null),
        URI(ADVANCED),
        DATE(ADVANCED),
    DISCOVERED(null),
        ENTITY(DISCOVERED),
            NAME(ENTITY),
            COMPANY(ENTITY),
            ADDRESS(ENTITY),
    UNKNOWN(null), //should never happen 
    ;
    private DataType parent = null;
    private List<DataType> children = new ArrayList<DataType>();

    private DataType(DataType parent){
        this.parent = parent;
        if(this.parent !=null){
            this.parent.children.add(this);
        }
    }


    public boolean is(DataType type){
        if (type != null) {
            for(DataType t=this; t!=null; t=t.parent){
                if (type == t){
                    return true;
                }
            }
        }
        // else or if not found something to return true
        return false;
    }

    public DataType[] getHierarchy(){
        return children.toArray(new DataType[children.size()]);
    }
}
