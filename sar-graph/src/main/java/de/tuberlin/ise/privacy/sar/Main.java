package de.tuberlin.ise.privacy.sar;

import de.tuberlin.ise.privacy.sar.analyzers.SarGraphAnalyzer;
import de.tuberlin.ise.privacy.sar.graph.SarGraph;
import de.tuberlin.ise.privacy.sar.graph.nodes.SarNode;
import de.tuberlin.ise.privacy.sar.readers.ContentReader;
import de.tuberlin.ise.privacy.sar.readers.DirectoryReader;
import de.tuberlin.ise.privacy.sar.utils.SarExporter;

import java.io.File;
import java.util.Scanner;


public class Main {
    public static void main(String[] args) {
        /**
         * 1. read folder structure
         * 2. convert to SAR graph
         * 3. write to JSON
         * 4. (enhance grapph with statistics)
         * 5. (eval)
         */


        // Phase 0. Determine Directory to Read
        String path = determineRootPath();
        File rootDir = new File(path);
        System.out.println("Converting SAR data from: " + rootDir.getAbsolutePath());

        // Phase 1: tree-like graph from directory
        SarGraph graph = new SarGraph();
        DirectoryReader rootReader = new DirectoryReader(graph);
        rootReader.addRootDir(rootDir);

        // Phase 2: enrich with content (now it is a directed graph)
        ContentReader contentReader = new ContentReader(graph);
        contentReader.readAllFileContents();

//        for (SarNode node : graph.iterables().vertices()) {
//            System.out.println(node);
//        }

        // Phase 3: Knowledge Discovery
        SarGraphAnalyzer analyzer = new SarGraphAnalyzer(graph);
        analyzer.analyze();
        analyzer.printAllStats();

        // Phase 4: Visualization & Export
        SarExporter graphExporter = new SarExporter(graph);
        graphExporter.dotExportToPath("demo/demo.dot");

        System.out.println("Finished. Bye!");
    }

    private static String determineRootPath() {
        String path;

        // todo: read from CLI param or use demo
        if (new File("demo").exists()) {
            // path = "demo/karl3000"; //debug
            // path = "demo/rewe";
            // path = "demo/pinterest";
            // path = "demo/Etsy";
            // path = "demo/PayPal";
             path = "demo/WhatsApp_html";
             // path = demo/WhatsApp_json";
        } else {
            Scanner userInput = new Scanner(System.in);
//            System.out.println("Enter provider name");
//            dataProvider = userInput.nextLine();
            System.out.println("Enter path to unzipped access data");
            path = userInput.nextLine();
            userInput.close();
        }
        return path;
    }
}
