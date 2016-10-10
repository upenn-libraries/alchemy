package edu.upenn.library.elements.api.xml.v49;

import edu.upenn.library.elements.api.XML;
import edu.upenn.library.elements.api.xml.XMLDocument;
import org.jdom2.Element;
import org.jdom2.Text;

/**
 * For use with the /{cat}/records/{data-source}/{proprietary-id} API resource.
 */
public class ImportRecord extends XMLDocument {

  private Element nativeE;
  private Element root;

  public ImportRecord() {
    root = new Element("import-record", XML.apiNs);

    nativeE = new Element("native", XML.apiNs);
    root.addContent(nativeE);

    getDocument().addContent(root);
  }

  public ImportRecord(String typeName) {
    this();
    root.setAttribute("type-name", typeName);
  }

  public ImportRecord(int typeId) {
    this();
    root.setAttribute("type-id", String.valueOf(typeId));
  }

  public void addBooleanField(boolean value) {

  }

  public void addDateField(boolean value) {

  }

  public void addDecimalField(String fieldName) {

  }

  public void addText(String fieldName, String value) {
    Element text = new Element("text", XML.apiNs);
    text.setContent(new Text(value));

    Element field = new Element("field", XML.apiNs);
    field.setAttribute("name", fieldName);
    field.addContent(text);

    nativeE.addContent(field);
  }

}
