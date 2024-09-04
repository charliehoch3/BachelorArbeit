package de.tuberlin.ise.privacy.sar.utils;

import java.io.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

import de.tuberlin.ise.privacy.sar.errors.UnknownFileExtensionException;
import de.tuberlin.ise.privacy.sar.graph.SarGraph;
import de.tuberlin.ise.privacy.sar.graph.edges.SarEdge;
import de.tuberlin.ise.privacy.sar.graph.nodes.ContentNode;
import de.tuberlin.ise.privacy.sar.graph.nodes.SarNode;

import de.tuberlin.ise.privacy.sar.graph.nodes.ValueNode;
import de.tuberlin.ise.privacy.sar.readers.CsvSarReader;
import de.tuberlin.ise.privacy.sar.readers.JsonSarReader;

import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.nio.json.JSONExporter;



public class SarFileUtils {
    private static AtomicLong baseId = new AtomicLong();

    public static String createID() {
        return String.valueOf(baseId.getAndIncrement());
    }

    public static String createID(String name) {
        return createID() + "_" + name;
    }

}
