package de.tuberlin.ise.privacy.sar.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public enum FileType {
    EMPTY(null),
    FOLDER(null),
    FILE(null),
        STRUCTURED(FILE),
            CSV(STRUCTURED),
        SEMI_STRUCTURED(FILE),
            JSON(SEMI_STRUCTURED),
            XML(SEMI_STRUCTURED),
            HTML(SEMI_STRUCTURED),
        UNSTRUCTURED(FILE),
            TEXT(UNSTRUCTURED),
            IMAGE(UNSTRUCTURED),
            MEDIA(UNSTRUCTURED),
        UNKNOWN(FILE),
    ;
    private FileType parent = null;
    private List<FileType> children = new ArrayList<FileType>();

    private FileType(FileType parent){
        this.parent = parent;
        if(this.parent !=null){
            this.parent.children.add(this);
        }
    }


    public boolean is(FileType type){
        if (type != null) {
            for(FileType t=this; t!=null; t=t.parent){
                if (type == t){
                    return true;
                }
            }
        }
        // else or if not found something to return true
        return false;
    }

    public FileType[] getChildren(){
        return children.toArray(new FileType[children.size()]);
    }


        public static FileType getType(File file) {
        if (file.isDirectory() && file.listFiles().length == 0) {
            //todo: test, maybe we want to keep is directory, but add an empty child node if file.listFiles==0
            return FileType.EMPTY;
        } else if (file.isDirectory()) {
            return FileType.FOLDER;

        }
        //else: file is "file" and not folder!
        String fileName = file.getName();
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
        if (fileName.contains("no") && fileName.contains("data")) {
            //specific catch phrase for e.g. facebook data with no_data.txt files
            return FileType.EMPTY;
        } else if (extension.equals("json")) {
            return FileType.JSON;
        } else if (extension.equals("xml")) {
            return FileType.XML;
        } else if (extension.equals("html")) {
            return FileType.HTML;
        } else if (extension.equals("csv")) {
            return FileType.CSV;
        } else if (extension.equals("txt") || extension.equals("docx") || extension.equals("doc") || extension.equals("odt") || extension.equals("pdf") || extension.equals("md")) { //  other text documents (not known)
            return FileType.TEXT;
        } else if (extension.equals("png") || extension.equals("jpg")) {
            return FileType.IMAGE;
        } else if (extension.equals("mp3") || extension.equals("mp4") || extension.equals("wmv")) {
            return FileType.MEDIA;
        } else if (extension.equals("db") || extension.equals("sqlitedb") || extension.equals("accdb") || extension.equals("nfs") || extension.equals("fp7")) {
            //extensions drawn from most common files: https://fileinfo.com/filetypes/database
            return FileType.STRUCTURED;
        } else {
            return FileType.UNKNOWN;
        }
    }

}
