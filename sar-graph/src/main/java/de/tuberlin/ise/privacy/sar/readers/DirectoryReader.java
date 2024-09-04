package de.tuberlin.ise.privacy.sar.readers;

import de.tuberlin.ise.privacy.sar.graph.SarGraph;
import de.tuberlin.ise.privacy.sar.graph.nodes.FileNode;
import de.tuberlin.ise.privacy.sar.graph.nodes.FolderNode;
import de.tuberlin.ise.privacy.sar.graph.nodes.SarNode;
import de.tuberlin.ise.privacy.sar.utils.FileType;
import de.tuberlin.ise.privacy.sar.utils.SarFileUtils;

import java.io.File;

public class DirectoryReader {

    private SarGraph graph;

    public DirectoryReader(SarGraph graph) {
        this.graph = graph;
    }


    public void addRootDir(File rootDir) {

        if (rootDir.isDirectory()) {
            FolderNode rootNode = new FolderNode(rootDir.getName(), rootDir.getAbsolutePath(), rootDir);
            graph.addNode(rootNode);
            addChilds(rootDir);
        } else {
            System.err.println("Argument " + rootDir.getAbsolutePath() + " can not be converted into a graph. Maybe there is no root directory?");
        }
    }



    public void addFile(File file) {
        //System.out.println("Adding file " + file.getAbsolutePath() + " to Sar Graph " + service);
        SarNode node = null;
        String fileName = file.getName();
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
        String rawFileName = fileName.replace("." + extension, "");
        String path = file.getAbsolutePath();
        FileType fileType = FileType.getType(file);
        if (fileType == FileType.FOLDER) {
            node = new FolderNode(rawFileName, path, file);
            graph.addNode(node);
            graph.addContainsEdge(file);
            addChilds(file);
        } else if (fileType.is(FileType.FILE)) {
            node = new FileNode(rawFileName, path, fileType);
            graph.addNode(node);
            graph.addContainsEdge(file);
        } else if (fileType == FileType.EMPTY) {
            return;
        } else {
            System.err.println("Could not add file " + path + " because it is neither a folder nor a file");
            return;
        }
    }

    private void addChilds(File folder) {
        File[] children = folder.listFiles();
        for (File childFile : children) {
            addFile(childFile);
        }
    }

}
