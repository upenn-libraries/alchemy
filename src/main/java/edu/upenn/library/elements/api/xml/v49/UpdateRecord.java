package edu.upenn.library.elements.api.xml.v49;

import edu.upenn.library.elements.api.XML;
import edu.upenn.library.elements.api.xml.XMLDocument;
import org.jdom2.Element;
import org.jdom2.Text;

/**
 * Using this document for PATCH requests isn't covered in the API Technical Guide.
 * See this page on the Symplectic support site:
 * https://support.symplectic.co.uk/support/solutions/articles/6000050094-how-to-use-the-patch-record-api-operation
 */
public class UpdateRecord extends XMLDocument {

  public enum Operation {
    Set, Clear, Add, Remove;
  }

  private Element fields;
  private Element root;

  public UpdateRecord() {
    root = new Element("update-record", XML.apiNs);

    fields = new Element("fields", XML.apiNs);
    root.addContent(fields);

    getDocument().addContent(root);
  }

  /**
   * Add a field element, and an arbitrary tree of elements under it
   */
  public void addField(Operation op, String fieldName, Element element) {
    Element field = new Element("field", XML.apiNs);
    field.setAttribute("name", fieldName);
    field.setAttribute("operation", op.name().toLowerCase());
    if(element != null) {
      field.addContent(element);
    }

    fields.addContent(field);
  }

  /**
   * Add a field element, with no elements under it.
   * This is how the "clear" operation works.
   */
  public void addField(Operation op, String fieldName) {
    addField(op, fieldName, null);
  }

  /**
   * Add a field element whose value is a string
   */
  public void addTextField(Operation op, String fieldName, String value) {
    Element text = new Element("text", XML.apiNs);
    text.setContent(new Text(value));
    addField(op, fieldName, text);
  }

}
