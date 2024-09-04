package de.tuberlin.ise.privacy.sar.readers;

import java.io.File;
import java.io.IOException;
import java.util.*;

import de.tuberlin.ise.privacy.sar.CSVConverter;
import de.tuberlin.ise.privacy.sar.graph.SarGraph;
import de.tuberlin.ise.privacy.sar.graph.nodes.*;
import de.tuberlin.ise.privacy.sar.utils.SarFileUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;

import de.tuberlin.ise.privacy.sar.graph.edges.EdgeType;
import de.tuberlin.ise.privacy.sar.graph.edges.SarEdge;

public class HtmlSarReader {

    SarGraph graph = new SarGraph();

    /**
     * read single html file,
     * that contains data in arbitrary tags (special treatment for tables)
     *
     * @param file in html format
     * @return SAR sub-graph with content of file
     */
    public SarGraph readHtml(File file) {
        try {
            //base URI for relative URIs, which we assume not to have
            Document doc = Jsoup.parse(file, "UTF-8", "");
            MapNode head = new MapNode(doc.title(), SarFileUtils.createID());
            graph.addVertex(head);
            graph.setRootNode(head);
            Element body = doc.body();
                /*if(body.childrenSize()==1){ // child node could have children itself, how to handle this?
                    ContentNode child = convertToValueNode(body.child(0));
                    if(child != null){
                        //graph.addVertex(child);
                        graph.addEdge(head, child, new SarEdge(EdgeType.CONTAINS));
                    }

                }else if(body.childNodeSize()>1){

                }*/
            body.children().forEach(e -> handleHtmlNode(e, head));
        } catch (IOException e) {
            System.err.println("Could not parse HTML document: " + file.getName() + " maybe it is a mal-defined HTML?");
        }

        handleHeaderEdges(graph, file);
        return graph;
    }

    private void handleHeaderEdges(SarGraph graph, File file) {
        SarEdge[] edges = graph.edgeSet().toArray(new SarEdge[0]);
        List<HeaderNode> parentlist = new ArrayList<>();
        parentlist.add((HeaderNode) edges[0].getTargetNode());
        Set<Pair<String, String>> htmlListe = new HashSet<>();
        Pair<String, String> aktuellesPair;

        for (int i = 0; i < edges.length-1; i++) {
            if(edges[i].getTargetNode() instanceof HeaderNode aktuellerKnoten) {
                if(edges[i+1].getTargetNode() instanceof HeaderNode nextKnoten) {
                    if(aktuellerKnoten.compareTo(nextKnoten) <= -1){
                        parentlist.add(nextKnoten);
                    } else {
                        String stringList = parentlist.get(0).getContent();
                        for(int j = 1; j < parentlist.size()-1; j++){
                              stringList = stringList + "_" + parentlist.get(j).getContent();
                        }
                        aktuellesPair = Pair.of(stringList, aktuellerKnoten.getContent());
                        htmlListe.add(aktuellesPair);
                        List<HeaderNode> tempparentlist = new ArrayList<>();
                        for(int k = 0; k < parentlist.size(); k++){
                            tempparentlist.add(parentlist.get(k));
                            if(nextKnoten.getHtmlTagName().equals(parentlist.get(k).getHtmlTagName())){
                                tempparentlist.remove(tempparentlist.size()-1);
                                parentlist = tempparentlist;
                                parentlist.add(nextKnoten);
                                break;
                            }
                        }
                    }
                }
            }

        }
        CSVConverter.csvBuilder(htmlListe, file.getName());
        //System.out.println(htmlListe.stream().map(Pair::toString).collect(Collectors.joining("\n")));
    }

    private ContentNode convertToValueNode(Element e) {
        ContentNode node = null;
        String ownText = e.wholeOwnText();
        System.out.println("Converting " + e.tagName() + " with content " + ownText + " to value node");
        if (ownText.isBlank()) {
            System.out.println("  skipped due to no content");
        } else {
            if (e.childrenSize() < 1) {
                node = new ValueNode("HTML node", SarFileUtils.createID(), ownText); //TODO change name
            } else if (e.wholeText().length() > ownText.length()) {
                node = new ListNode("HTML node: liste", ownText);
                graph.addVertex(node); //TODO: attention, I add the vertex here but not the edge (unknown), also it is not unified
                System.out.println("   adding child nodes");
                for (Node childNode : e.children()) {
                    handleHtmlNode(childNode, node);
                    /*Element child = (Element) childNode;
                    if(child!=null && child.hasText()){
                        ContentNode childGraphNode = convertToValueNode(child); //TODO: handle HTML??l
                        graph.addVertex(childGraphNode);
                        graph.addEdge(node, childGraphNode, new SarEdge(EdgeType.CONTAINS));
                    }*/
                }
            } else {
                //System.out.println("Could not handle value node " + e);
            }
        }
        //System.out.println("   returning " + node);
        return node;
    }

    private void handleHtmlNode(Node node, ContentNode reference) {
        if (node instanceof DataNode) {
            // skip, DataNode is for style and script, which is not useful for this program
        } else if (node instanceof TextNode text && !text.getWholeText().isBlank()) {
            System.out.println("   adding textnode " + text + " to parent node " + reference);
            ValueNode content = new ValueNode("HTML text", SarFileUtils.createID(), text.getWholeText());
            graph.addVertex(content);
//            graph.addEdge(reference, content, new SarEdge(EdgeType.CONTAINS));
            // does not work? text.childNodes().forEach(child -> handleHtmlNode(child, reference));
            if (text.childNodeSize() > 1) {
                for (Node child : text.childNodes()) {
                    handleHtmlNode(child, content);
                }
            }
        } else {
            Element e = (Element) node;
            //is empty?
            final ContentNode parent;
            if (e.tagName().equals("table")) {
                //System.out.println("Detected Table " + e);
                parent = new MapNode("HTML table", SarFileUtils.createID()); //TODO change name
                graph.addVertex(parent);
//                graph.addEdge(reference, parent, new SarEdge(EdgeType.CONTAINS));
                handleTable(e, (MapNode) parent);
                return;
            } else if (e.tagName().equals("ul") || e.tagName().equals("ol")) {
                //System.out.println("Detected List " + e);
                parent = new ListNode("HTML list", SarFileUtils.createID()); //TODO change name
                graph.addVertex(parent);
//                graph.addEdge(reference, parent, new SarEdge(EdgeType.CONTAINS));
                e.children().forEach(child -> handleListElement(child, parent));
                return;
            } else if(e.tagName().startsWith("h")) {
                //System.out.println("Detected Header " + e);
                HeaderNode content = new HeaderNode("HTML header", SarFileUtils.createID(), e.text(), e.tagName());
                graph.addVertex(content);
                graph.addEdge(reference, content, new SarEdge(EdgeType.RELATES));
                return;
            }else if (e.hasText()) {
                // Skip value Nodes, we don't care
                //else: convert to valueNode
                /*ContentNode content = convertToValueNode(e);
                if (content != null) {
                    System.out.println("  adding contentnode " + content + " to parent node " + reference);
                    graph.addVertex(content);
                    graph.addEdge(reference, content, new SarEdge(EdgeType.CONTAINS));
                    parent = content;
                } else {
                    //TODO: check
                    parent = reference;
                }*/
                parent = reference;
            } else {
                parent = reference;
            }

            e.children().forEach(child -> handleHtmlNode(child, parent));
        }
    }

    private void handleTable(Element e, MapNode parent) {
        List<Element> rows = e.getElementsByTag("tr");
        List<MapNode> cols = new ArrayList<>();
        //List<Element> heads = e.getElementsByTag("th");

        //parse column names
        rows.get(0).children().forEach(colNode -> {
            Element node = (Element) colNode;
            String colName = "col_" + colNode.siblingIndex();
            if (node.tagName().equals("th")) {
                colName += "_" + node.ownText();

            }
            MapNode col = new MapNode(colName, SarFileUtils.createID());
            graph.addVertex(col);
//            graph.addEdge(parent, col, new SarEdge(EdgeType.CONTAINS));
            cols.add(col);
        });

        int i = 0;
        if (!e.getElementsByTag("th").isEmpty()) {
            i = 1; // in case of headers, skip first row
        }
        for (; i < rows.size(); i++) {
            Element row = rows.get(i);
            String rowName = "row_" + i;
            if (row.firstChild().normalName().equals("th")) {
                rowName += "_" + ((Element) row.firstChild()).ownText();
                row.replaceWith(row.firstChild());
            }
            MapNode rowNode = new MapNode(rowName, SarFileUtils.createID());
            graph.addVertex(rowNode);
//            graph.addEdge(parent, rowNode, new SarEdge(EdgeType.CONTAINS));
            readTableRow(row, rowNode, cols);
        }
    }

    private void readTableRow(Element row, MapNode rowNode, List<MapNode> cols) {
        List<Element> elements = row.getElementsByTag("td");
        elements.forEach(element -> {
            ValueNode cellNode = new ValueNode(element.ownText(), SarFileUtils.createID(), element.text());
            MapNode colNode = cols.get(element.siblingIndex());
            graph.addVertex(cellNode);
//            graph.addEdge(rowNode, cellNode, new SarEdge(EdgeType.VALUE));
//            graph.addEdge(colNode, cellNode, new SarEdge(EdgeType.VALUE));
        });
    }

    private void handleListElement(Node child, ContentNode reference) {
        Element e = (Element) child;
        ContentNode node = new ValueNode(e.ownText(), SarFileUtils.createID(), e.text());
        graph.addVertex(node);
//        graph.addEdge(reference, node, new SarEdge(EdgeType.CONTAINS));
    }


}
