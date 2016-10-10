package edu.upenn.library.elements.api.xml.v49;

import edu.upenn.library.elements.api.XML;
import edu.upenn.library.elements.api.xml.XMLDocument;
import org.jdom2.Element;
import org.jdom2.Text;

/**
 * For use with the /relationships API resource.
 */
public class ImportRelationship extends XMLDocument {

  private Element root;
  private Element fromObject;
  private Element toObject;

  public ImportRelationship() {
    root = new Element("import-relationship", XML.apiNs);
    getDocument().addContent(root);

    fromObject = new Element("from-object", XML.apiNs);
    root.addContent(fromObject);

    toObject = new Element("to-object", XML.apiNs);
    root.addContent(toObject);
  }

  public ImportRelationship(String typeName) {
    this();
    Element typeNameE = new Element("type-name", XML.apiNs);
    typeNameE.setContent(new Text(typeName));
    root.addContent(typeNameE);
  }

  public ImportRelationship(int typeId) {
    this();
    Element typeIdE = new Element("type-id", XML.apiNs);
    typeIdE.setContent(new Text(String.valueOf(typeId)));
    root.addContent(typeIdE);
  }

  public void setFromObject(String value) {
    fromObject.setContent(new Text(value));
  }

  public void setToObject(String value) {
    toObject.setContent(new Text(value));
  }

}
