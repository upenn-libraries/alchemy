package edu.upenn.library.elements.api;

/*
 * Represents a URL parameter passed to an API resource.
 */
public class Param {

  private String paramForUrl;

  public Param(String paramForUrl) {
    this.paramForUrl = paramForUrl;
  }

  public String getParamForUrl() {
    return paramForUrl;
  }
}
