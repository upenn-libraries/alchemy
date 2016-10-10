package edu.upenn.library.elements.tasks;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import edu.upenn.library.elements.Task;
import edu.upenn.library.elements.api.Feed;
import edu.upenn.library.elements.api.FeedEntry;
import edu.upenn.library.elements.api.XML;
import edu.upenn.library.elements.api.resources.Groups;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.jdom2.Element;

public class GroupsReport extends Task {

  class Node {
    String name;
    Element element;
    List<Node> children = new ArrayList<Node>();
  }

  @Override
  public String getDescription() {
    return "Generate .CSV report of groups.";
  }

  @Override
  public String getHelp() {
    return "Usage: GroupsReport [output file]\n" +
      "\n" +
      "Generate .CSV report of groups.\n";
  }

  private Node buildTree(Map<String, Element> idsToElements, Element element) {

    Node node = new Node();

    node.name = element.getChild("name", XML.apiNs).getValue();
    node.element = element;

    Element childrenElement = element.getChild("children", XML.apiNs);
    if(childrenElement != null) {
      childrenElement.getChildren().forEach(child -> {
        Element childNode = idsToElements.get(child.getAttribute("id").getValue());
        node.children.add(buildTree(idsToElements, childNode));
      });
    }

    node.children.sort((a, b) -> a.name.compareTo(b.name));

    return node;
  }

  private void writeTree(List<List<String>> rows, int indent, Node node) {
    List<String> row = new ArrayList<>();

    for(int i = 0; i < indent; i++) {
      row.add(null);
    }
    row.add(node.name);

    rows.add(row);

    for(Node child : node.children) {
      writeTree(rows, indent + 1, child);
    }
  }

  @Override
  public void execute() throws Exception {
    if(getArgs().size() < 1) {
      getLogger().error("You must specify a filename argument.");
      return;
    }
    String filename = getArgs().get(0);

    FileWriter writer = new FileWriter(filename);
    CSVPrinter printer = CSVFormat.DEFAULT.print(writer);

    Feed feed = getApi().getFeed(new Groups());

    Map<String, Element> idsToElements = new HashMap<>();
    Element root = null;
    for(FeedEntry entry : feed.getEntries()) {
      Element group = entry.getElementsContent();
      idsToElements.put(group.getAttribute("id").getValue(), group);
      if(group.getChild("parents", XML.apiNs) == null) {
        root = group;
      }
    }

    if(root != null) {
      Node rootNode = buildTree(idsToElements, root);
      List<List<String>> rows = new ArrayList<>();
      writeTree(rows, 0, rootNode);
      printer.printRecords(rows);
    } else {
      getLogger().error("Couldn't find root node");
    }

    printer.flush();
    printer.close();
  }

}
