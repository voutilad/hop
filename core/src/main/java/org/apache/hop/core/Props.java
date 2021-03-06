/*! ******************************************************************************
 *
 * Hop : The Hop Orchestration Platform
 *
 * http://www.project-hop.org
 *
 *******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************/

package org.apache.hop.core;

import org.apache.hop.core.config.HopConfig;
import org.apache.hop.core.logging.ILogChannel;
import org.apache.hop.core.logging.LogChannel;

/**
 * We use Props to store all kinds of user interactive information such as the selected colors, fonts, positions of
 * windows, etc.
 *
 * @author Matt
 * @since 15-12-2003
 */
public class Props implements Cloneable {
  private static Class<?> PKG = Const.class; // for i18n purposes, needed by Translator!!

  private static final String STRING_USER_PREFERENCES = "User preferences";

  protected static Props props;

  public static final String STRING_FONT_FIXED_NAME = "FontFixedName";
  public static final String STRING_FONT_FIXED_SIZE = "FontFixedSize";
  public static final String STRING_FONT_FIXED_STYLE = "FontFixedStyle";

  public static final String STRING_FONT_DEFAULT_NAME = "FontDefaultName";
  public static final String STRING_FONT_DEFAULT_SIZE = "FontDefaultSize";
  public static final String STRING_FONT_DEFAULT_STYLE = "FontDefaultStyle";

  public static final String STRING_FONT_GRAPH_NAME = "FontGraphName";
  public static final String STRING_FONT_GRAPH_SIZE = "FontGraphSize";
  public static final String STRING_FONT_GRAPH_STYLE = "FontGraphStyle";

  public static final String STRING_FONT_GRID_NAME = "FontGridName";
  public static final String STRING_FONT_GRID_SIZE = "FontGridSize";
  public static final String STRING_FONT_GRID_STYLE = "FontGridStyle";

  public static final String STRING_FONT_NOTE_NAME = "FontNoteName";
  public static final String STRING_FONT_NOTE_SIZE = "FontNoteSize";
  public static final String STRING_FONT_NOTE_STYLE = "FontNoteStyle";

  public static final String STRING_BACKGROUND_COLOR_R = "BackgroundColorR";
  public static final String STRING_BACKGROUND_COLOR_G = "BackgroundColorG";
  public static final String STRING_BACKGROUND_COLOR_B = "BackgroundColorB";

  public static final String STRING_GRAPH_COLOR_R = "GraphColorR";
  public static final String STRING_GRAPH_COLOR_G = "GraphColorG";
  public static final String STRING_GRAPH_COLOR_B = "GraphColorB";

  public static final String STRING_TAB_COLOR_R = "TabColorR";
  public static final String STRING_TAB_COLOR_G = "TabColorG";
  public static final String STRING_TAB_COLOR_B = "TabColorB";

  public static final String STRING_ZOOM_FACTOR = "ZoomFactor";
  public static final String STRING_ICON_SIZE = "IconSize";
  public static final String STRING_LINE_WIDTH = "LineWidth";
  public static final String STRING_MIDDLE_PCT = "MiddlePct";
  public static final String STRING_INDICATE_SLOW_PIPELINE_TRANSFORMS = "IndicateSlowPipelineTransforms";

  public static final String STRING_LAST_PREVIEW_PIPELINE = "LastPreviewPipeline";
  public static final String STRING_LAST_PREVIEW_TRANSFORM = "LastPreviewTransform";
  public static final String STRING_LAST_PREVIEW_SIZE = "LastPreviewSize";

  public static final String STRING_MAX_UNDO = "MaxUndo";

  public static final String STRING_AUTO_SAVE = "AutoSave";
  public static final String STRING_SAVE_CONF = "SaveConfirmation";
  public static final String STRING_AUTO_SPLIT = "AutoSplit";
  public static final String STRING_AUTO_COLLAPSE_CORE_TREE = "AutoCollapseCoreObjectsTree";

  public static final String STRING_USE_DB_CACHE = "UseDBCache";
  public static final String STRING_OPEN_LAST_FILE = "OpenLastFile";

  public static final String STRING_SHOW_CANVAS_GRID = "ShowCanvasGrid";
  public static final String STRING_SHOW_EXIT_WARNING = "ShowExitWarning";
  public static final String STRING_SHOW_OS_LOOK = "ShowOSLook";

  public static final String STRING_CUSTOM_PARAMETER = "CustomParameter";

  public static final String STRING_DEFAULT_PREVIEW_SIZE = "DefaultPreviewSize";

  private static final String STRING_MAX_NR_LINES_IN_LOG = "MaxNrOfLinesInLog";
  private static final String STRING_MAX_LOG_LINE_TIMEOUT_MINUTES = "MaxLogLineTimeOutMinutes";

  protected ILogChannel log;

  public static final int WIDGET_STYLE_DEFAULT = 0;
  public static final int WIDGET_STYLE_FIXED = 1;
  public static final int WIDGET_STYLE_TABLE = 2;
  public static final int WIDGET_STYLE_NOTEPAD = 3;
  public static final int WIDGET_STYLE_GRAPH = 4;
  public static final int WIDGET_STYLE_TAB = 5;
  public static final int WIDGET_STYLE_TOOLBAR = 6;

  /**
   * Initialize the properties: load from disk.
   */
  public static final void init() {
    if ( props == null ) {
      props = new Props();

    } else {
      throw new RuntimeException( "The Properties systems settings are already initialised!" );
    }
  }

  /**
   * Check to see whether the Hop properties where loaded.
   *
   * @return true if the Hop properties where loaded.
   */
  public static boolean isInitialized() {
    return props != null;
  }

  public static Props getInstance() {
    if ( props != null ) {
      return props;
    }

    throw new RuntimeException( "Properties, Hop systems settings, not initialised!" );
  }

  protected Props() {
    log = new LogChannel( STRING_USER_PREFERENCES );
  }

  @Override
  public String toString() {
    return STRING_USER_PREFERENCES;
  }


  protected void setDefault() {
    if ( !containsKey( "WorkflowDialogStyle" ) ) {
      setProperty( "WorkflowDialogStyle", "RESIZE,MAX,MIN" );
    }
  }

  protected void setProperty(String key, String value) {
    try {
      HopConfig.setGuiProperty( key, value );
    } catch(Exception e) {
      throw new RuntimeException("Error saving hop config option key '"+key+"', value '"+value+"'", e);
    }
  }

  public String getProperty( String propertyName ) {
    return getProperty( propertyName, null );
  }

  public String getProperty( String propertyName, String defaultValue ) {
    return HopConfig.readOptionString( propertyName, defaultValue );
  }

  public boolean containsKey(String key) {
    return HopConfig.getConfigMap().containsKey( key );
  }

  public void setUseDBCache( boolean use ) {
    setProperty( STRING_USE_DB_CACHE, use ? "Y" : "N" );
  }

  public boolean useDBCache() {
    String use = getProperty( STRING_USE_DB_CACHE );
    return !"N".equalsIgnoreCase( use );
  }



  /**
   * @param parameterName The parameter name
   * @param defaultValue  The default value in case the parameter doesn't exist yet.
   * @return The custom parameter
   */
  public String getCustomParameter( String parameterName, String defaultValue ) {
    return getProperty( STRING_CUSTOM_PARAMETER + parameterName, defaultValue );
  }

  /**
   * Set the custom parameter
   *
   * @param parameterName The name of the parameter
   * @param value         The value to be stored in the properties file.
   */
  public void setCustomParameter( String parameterName, String value ) {
    setProperty( STRING_CUSTOM_PARAMETER + parameterName, value );
  }

  public void clearCustomParameters() {

    for (String key : HopConfig.getConfigMap().keySet() ) {
      if ( key.startsWith( STRING_CUSTOM_PARAMETER ) ) {
        // Clear this one
        HopConfig.getConfigMap().remove( key );
      }
    }
  }

  public int getMaxNrLinesInLog() {
    String lines = getProperty( STRING_MAX_NR_LINES_IN_LOG );
    return Const.toInt( lines, Const.MAX_NR_LOG_LINES );
  }

  public void setMaxNrLinesInLog( int maxNrLinesInLog ) {
    setProperty( STRING_MAX_NR_LINES_IN_LOG, Integer.toString( maxNrLinesInLog ) );
  }

  public int getMaxLogLineTimeoutMinutes() {
    String minutes = getProperty( STRING_MAX_LOG_LINE_TIMEOUT_MINUTES );
    return Const.toInt( minutes, Const.MAX_LOG_LINE_TIMEOUT_MINUTES );
  }

  public void setMaxLogLineTimeoutMinutes( int maxLogLineTimeoutMinutes ) {
    setProperty( STRING_MAX_LOG_LINE_TIMEOUT_MINUTES, Integer.toString( maxLogLineTimeoutMinutes ) );
  }

  public void reset() {
    props = null;
    clear();
  }

  private void clear() {
    HopConfig.getConfigMap().clear();
  }


}
