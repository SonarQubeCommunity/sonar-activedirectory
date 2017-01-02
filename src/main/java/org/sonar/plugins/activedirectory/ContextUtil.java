package org.sonar.plugins.activedirectory;

import static org.apache.commons.lang.StringUtils.defaultIfBlank;

import org.sonar.api.config.Settings;

public class ContextUtil {
  
  public static final String PROPERTY_CONTEXT = "sonar.web.context";
  private final Settings settings;
  private static String contextPath = "";

  public ContextUtil(Settings settings) {
    this.settings = settings;
    contextPath = defaultIfBlank(settings.getString(PROPERTY_CONTEXT), "").replaceFirst("(\\/+)$", "");
  }
  
  public static String getContextPath() {
    return contextPath;
  }
  
}
