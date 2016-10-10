package edu.upenn.library.elements.api.xml;

import org.jdom2.Document;
import org.jdom2.output.XMLOutputter;

public class XMLDocument {

  private Document document = new Document();

  public Document getDocument() {
    return document;
  }

  @Override
  public String toString() {
    XMLOutputter xout = new XMLOutputter();
    return xout.outputString(document);
  }

}
