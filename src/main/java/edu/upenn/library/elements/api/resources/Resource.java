package edu.upenn.library.elements.api.resources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An API resource is a specific URL endpoint, on which HTTP requests
 * can be made. Resource objects know something about the data returned.
 */
public abstract class Resource {

  private Map<String, List<String>> params = new HashMap<String, List<String>>();

  /**
   * The path portion of the API URL
    * @return
   */
  public abstract String getPath();

  /**
   * Returns the name of an element, nested inside the ATOM 'entry' element,
   * which contains data specific to the kind of request we're making.
   * This is described in the table in the API Technical Guide v4.6 p.69.
   * @return
   */
  public abstract String getAtomEntryElement();

  public void setParam(CommonParams param, String value) {
    setParam(param.getParamForUrl(), value);
  }

  /**
   * Set a URL parameter for this resource request.
   * @param param
   * @param value
   */
  public void setParam(String param, String value) {
    List<String> values = new ArrayList<String>();
    values.add(value);
    params.put(param, values);
  }

  public Map<String, List<String>> getParams() {
    return params;
  }

}
