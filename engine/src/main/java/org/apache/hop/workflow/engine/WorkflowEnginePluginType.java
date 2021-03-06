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

package org.apache.hop.workflow.engine;

import org.apache.hop.core.Const;
import org.apache.hop.core.exception.HopPluginException;
import org.apache.hop.core.plugins.BasePluginType;
import org.apache.hop.core.plugins.IPluginType;
import org.apache.hop.core.plugins.PluginAnnotationType;
import org.apache.hop.core.plugins.PluginFolder;
import org.apache.hop.core.plugins.PluginMainClassType;
import org.apache.hop.pipeline.engine.IPipelineEngine;

import java.lang.annotation.Annotation;
import java.util.Map;

@PluginMainClassType( IWorkflowEngine.class )
@PluginAnnotationType( WorkflowEnginePlugin.class )
public class WorkflowEnginePluginType extends BasePluginType implements IPluginType {

  private WorkflowEnginePluginType() {
    super( WorkflowEnginePlugin.class, "HOP_WORKFLOW_ENGINES", "Hop Workflow Engines" );

    pluginFolders.add( new PluginFolder( "plugins", false, true ) );
  }

  private static WorkflowEnginePluginType pluginType;

  public static WorkflowEnginePluginType getInstance() {
    if ( pluginType == null ) {
      pluginType = new WorkflowEnginePluginType();
    }
    return pluginType;
  }

  @Override
  protected void registerNatives() throws HopPluginException {
    super.registerNatives();
  }

  @Override
  protected String getXmlPluginFile() {
    return Const.XML_FILE_HOP_WORKFLOW_ENGINES;
  }

  @Override
  protected String getMainTag() {
    return "hop-workflow-engines";
  }

  @Override
  protected String getSubTag() {
    return "hop-workflow-engine";
  }

  @Override
  protected String getPath() {
    return "./";
  }

  @Override
  protected String extractCategory( Annotation annotation ) {
    return null;
  }

  @Override
  protected String extractDesc( Annotation annotation ) {
    return ( (WorkflowEnginePlugin) annotation ).description();
  }

  @Override
  protected String extractID( Annotation annotation ) {
    return ( (WorkflowEnginePlugin) annotation ).id();
  }

  @Override
  protected String extractName( Annotation annotation ) {
    return ( (WorkflowEnginePlugin) annotation ).name();
  }

  @Override
  protected String extractImageFile( Annotation annotation ) {
    return null;
  }

  @Override
  protected boolean extractSeparateClassLoader( Annotation annotation ) {
    return false;
  }

  @Override
  protected String extractI18nPackageName( Annotation annotation ) {
    return null;
  }

  @Override
  protected void addExtraClasses( Map<Class<?>, String> classMap, Class<?> clazz, Annotation annotation ) {
  }

  @Override
  protected String extractDocumentationUrl( Annotation annotation ) {
    return null;
  }

  @Override
  protected String extractCasesUrl( Annotation annotation ) {
    return null;
  }

  @Override
  protected String extractForumUrl( Annotation annotation ) {
    return null;
  }

  @Override
  protected String extractSuggestion( Annotation annotation ) {
    return null;
  }

  @Override
  protected String extractClassLoaderGroup( Annotation annotation ) {
    return null;
  }
}
