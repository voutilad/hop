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

package org.apache.hop.www;

import org.apache.hop.base.LoadSaveBase;
import org.apache.hop.core.exception.HopException;
import org.apache.hop.pipeline.transforms.loadsave.validator.IFieldLoadSaveValidator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SlaveServerPipelineStatusLoadSaveTester extends LoadSaveBase<SlaveServerPipelineStatus> {

  public SlaveServerPipelineStatusLoadSaveTester( Class<SlaveServerPipelineStatus> clazz, List<String> commonAttributes ) {
    super( clazz, commonAttributes );
  }

  public SlaveServerPipelineStatusLoadSaveTester( Class<SlaveServerPipelineStatus> clazz, List<String> commonAttributes,
                                                  Map<String, IFieldLoadSaveValidator<?>> fieldLoadSaveValidatorAttributeMap ) {
    super( clazz, commonAttributes, new ArrayList<>(), new HashMap<>(),
      new HashMap<>(), fieldLoadSaveValidatorAttributeMap,
      new HashMap<String, IFieldLoadSaveValidator<?>>() );
  }

  public void testSerialization() throws HopException {
    testXmlRoundTrip();
  }

  protected void testXmlRoundTrip() throws HopException {
    SlaveServerPipelineStatus metaToSave = createMeta();
    Map<String, IFieldLoadSaveValidator<?>> validatorMap =
      createValidatorMapAndInvokeSetters( xmlAttributes, metaToSave );

    String xml = metaToSave.getXml();
    SlaveServerPipelineStatus metaLoaded = SlaveServerPipelineStatus.fromXml( xml );
    validateLoadedMeta( xmlAttributes, validatorMap, metaToSave, metaLoaded );
  }
}
