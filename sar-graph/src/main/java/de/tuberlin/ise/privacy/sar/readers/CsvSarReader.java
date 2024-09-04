package de.tuberlin.ise.privacy.sar.readers;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import de.tuberlin.ise.privacy.sar.graph.SarGraph;
import de.tuberlin.ise.privacy.sar.graph.edges.EdgeType;
import de.tuberlin.ise.privacy.sar.graph.edges.SarEdge;
import de.tuberlin.ise.privacy.sar.graph.nodes.ContentNode;
import de.tuberlin.ise.privacy.sar.graph.nodes.MapNode;
import de.tuberlin.ise.privacy.sar.graph.nodes.ValueNode;
import de.tuberlin.ise.privacy.sar.utils.SarFileUtils;

import org.jgrapht.graph.DefaultDirectedGraph;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;

public class CsvSarReader {
    /**
     * @param file
     * @return SAR (sub-graph) with content of csv
     */

    private static Logger logger = Logger.getLogger(SarFileUtils.class.getName());


    public SarGraph readCsv(File file) throws FileNotFoundException {
        SarGraph graph = new SarGraph();
        System.out.println("Reading csv");

        List<String[]> rows;

        CSVReader reader = new CSVReaderBuilder(new FileReader(file))
                .withCSVParser(
                        new CSVParserBuilder()
                                .withSeparator(';')
                                .build()
                ).build();

        try {
            rows = reader.readAll();
        } catch (IOException | CsvException e) {
            throw new RuntimeException(e);
        }

        boolean headers = true;

        // algorithm of the year: header row if
        // a) all headers are string (if your header name is a number you deserve it) AND
        // b) the second row is not all string

        headers = rows.size() >= 2 &&
                Arrays.stream(rows.get(0)).noneMatch(CsvSarReader::startsWithDigit) &&
                Arrays.stream(rows.get(1)).anyMatch(CsvSarReader::startsWithDigit);

        logger.info("Headers for " + file + ": " + headers);

        String colPrefix = "col";
        String rowPrefix = "row";

        String rootID = SarFileUtils.createID(file.getName());
        ContentNode root = new MapNode(rootID, rootID);
        String rowsID = rootID + "_rows";
        String colsID = rootID + "_cols";
        MapNode rowsNode = new MapNode("rows", rowsID);
        MapNode colsNode = new MapNode("cols", colsID);

        graph.addVertex(root);
        graph.setRootNode(root);
        graph.addVertex(rowsNode);
        graph.addVertex(colsNode);
        graph.addEdge(root, rowsNode, new SarEdge(EdgeType.MAPS_TO));
        graph.addEdge(root, colsNode, new SarEdge(EdgeType.MAPS_TO));

        Map<String, MapNode> colNodes = new LinkedHashMap<>();
        String[] headerCols = new String[0];

        int ri = 0;
        for (String[] row : rows) {

            // read headers
            if (headers && headerCols.length == 0) {
                headerCols = row;
                continue;
            }

            ri++;
            String rowName = rowsID + "_" + ri;

            MapNode rowNode = new MapNode(rowName, rowName);
            graph.addVertex(rowNode);
            graph.addEdge(rowsNode, rowNode, new SarEdge(EdgeType.CONTAINS));

            int ci = 0;
            for (String c : row) {
                ci++;
                String colName;
                if (headers) {
                    colName =  headerCols[ci - 1];
                } else {
                    colName = "col" + ci;
                }

                MapNode colNode;
                if (colNodes.containsKey(colName)) {
                    colNode = colNodes.get(colName);
                } else {
                    colNode = new MapNode(colName, colsID + "_" + colName);
                    graph.addVertex(colNode);
                    graph.addEdge(colsNode, colNode, new SarEdge(EdgeType.CONTAINS));
                    colNodes.put(colName, colNode);
                }

                String cellName = rowName + "_" + colName;
//                logger.info(c);
                ValueNode cellNode = new ValueNode(cellName, cellName, c);
                graph.addVertex(cellNode);
                graph.addEdge(rowNode, cellNode, new SarEdge(EdgeType.VALUE));
                graph.addEdge(colNode, cellNode, new SarEdge(EdgeType.VALUE));

            }
        }


        return graph;
    }


    static boolean startsWithDigit(String str) {
        return Character.isDigit(str.charAt(0));
    }

}