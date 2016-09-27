package edu.upenn.library.elements.api.resources;

/**
 * Parameters common to many resource operations. But NOT all.
 */
public enum CommonParams {
  IDS("ids"),
  PAGE("page"),
  PER_PAGE("per-page"),
  DETAIL("detail");

  private final String paramForUrl;

  CommonParams(String paramForUrl) {
    this.paramForUrl = paramForUrl;
  }

  public String getParamForUrl() {
    return paramForUrl;
  }

}
