/*! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2017 by Pentaho : http://www.pentaho.com
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

package org.apache.hop.testing.xp;

import org.apache.hop.core.exception.HopException;
import org.apache.hop.core.extension.ExtensionPoint;
import org.apache.hop.core.extension.IExtensionPoint;
import org.apache.hop.core.gui.AreaOwner;
import org.apache.hop.core.gui.Point;
import org.apache.hop.core.logging.ILogChannel;
import org.apache.hop.core.row.IRowMeta;
import org.apache.hop.metastore.api.IMetaStore;
import org.apache.hop.metastore.persist.MetaStoreFactory;
import org.apache.hop.pipeline.PipelineMeta;
import org.apache.hop.pipeline.transform.TransformMeta;
import org.apache.hop.testing.DataSet;
import org.apache.hop.testing.PipelineUnitTest;
import org.apache.hop.testing.PipelineUnitTestSetLocation;
import org.apache.hop.testing.gui.TestingGuiPlugin;
import org.apache.hop.testing.util.DataSetConst;
import org.apache.hop.ui.core.dialog.ErrorDialog;
import org.apache.hop.ui.hopgui.HopGui;
import org.apache.hop.ui.hopgui.file.pipeline.HopGuiPipelineGraph;
import org.apache.hop.ui.hopgui.file.pipeline.extension.HopGuiPipelineGraphExtension;
import org.apache.hop.ui.testing.DataSetDialog;
import org.apache.hop.ui.testing.PipelineUnitTestSetLocationDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.MessageBox;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ExtensionPoint(
  extensionPointId = "PipelineGraphMouseDown",
  id = "LocationMouseDoubleClickExtensionPoint",
  description = "Open a data set when double clicked on it"
)
public class LocationMouseDoubleClickExtensionPoint implements IExtensionPoint<HopGuiPipelineGraphExtension> {

  @Override
  public void callExtensionPoint( ILogChannel log, HopGuiPipelineGraphExtension pipelineGraphExtension ) throws HopException {
    HopGuiPipelineGraph pipelineGraph = pipelineGraphExtension.getPipelineGraph();
    PipelineMeta pipelineMeta = pipelineGraph.getPipelineMeta();

    PipelineUnitTest unitTest = TestingGuiPlugin.getCurrentUnitTest( pipelineMeta );
    if ( unitTest == null ) {
      return;
    }

    HopGui hopGui = HopGui.getInstance();
    try {
      List<DataSet> dataSets = DataSet.createFactory( hopGui.getMetaStore() ).getElements();
      for (DataSet dataSet : dataSets) {
        dataSet.initializeVariablesFrom( pipelineMeta );
      }

      Map<String, IRowMeta> transformFieldsMap = new HashMap<>();
      for ( TransformMeta transformMeta : pipelineMeta.getTransforms() ) {
        try {
          IRowMeta transformFields = pipelineMeta.getTransformFields( transformMeta );
          transformFieldsMap.put( transformMeta.getName(), transformFields );
        } catch ( Exception e ) {
          // Ignore GUI errors...
        }
      }

      // Find the location that was double clicked on...
      //
      MouseEvent e = pipelineGraphExtension.getEvent();
      Point point = pipelineGraphExtension.getPoint();

      if ( e.button == 1 || e.button == 2 ) {
        AreaOwner areaOwner = pipelineGraph.getVisibleAreaOwner( point.x, point.y );
        if ( areaOwner != null && areaOwner.getAreaType() != null ) {
          // Check if this is the flask...
          //
          if ( DataSetConst.AREA_DRAWN_INPUT_DATA_SET.equals( areaOwner.getParent() ) ) {

            // Open the dataset double clicked on...
            //
            String transformName = (String) areaOwner.getOwner();

            PipelineUnitTestSetLocation inputLocation = unitTest.findInputLocation( transformName );
            if ( inputLocation != null ) {
              PipelineUnitTestSetLocationDialog dialog = new PipelineUnitTestSetLocationDialog( hopGui.getShell(), inputLocation, dataSets, transformFieldsMap );
              if ( dialog.open() ) {
                pipelineGraph.updateGui();
              }
            }
          } else if ( DataSetConst.AREA_DRAWN_GOLDEN_DATA_SET.equals( areaOwner.getParent() ) ) {

            // Open the dataset double clicked on...
            //
            String transformName = (String) areaOwner.getOwner();

            PipelineUnitTestSetLocation goldenLocation = unitTest.findGoldenLocation( transformName );
            if ( goldenLocation != null ) {
              PipelineUnitTestSetLocationDialog dialog = new PipelineUnitTestSetLocationDialog( hopGui.getShell(), goldenLocation, dataSets, transformFieldsMap );
              if ( dialog.open() ) {
                pipelineGraph.updateGui();
              }
            }
          }
        }
      }
    } catch ( Exception e ) {
      new ErrorDialog( hopGui.getShell(), "Error", "Error editing location", e );
    }
  }

  private void openDataSet( String dataSetName ) {

    HopGui hopGui = HopGui.getInstance();

    IMetaStore metaStore = hopGui.getMetaStore();
    try {
      MetaStoreFactory<DataSet> setFactory = DataSet.createFactory( metaStore );
      DataSet dataSet = setFactory.loadElement( dataSetName );
      dataSet.initializeVariablesFrom( hopGui.getVariables() );

      DataSetDialog dataSetDialog = new DataSetDialog( hopGui.getShell(), metaStore, dataSet );
      while ( dataSetDialog.open() != null ) {
        String message = TestingGuiPlugin.validateDataSet( dataSet, dataSetName, setFactory.getElementNames() );

        // Save the data set...
        //
        if ( message == null ) {
          setFactory.saveElement( dataSet );
          break;
        } else {
          MessageBox box = new MessageBox( hopGui.getShell(), SWT.OK );
          box.setText( "Error" );
          box.setMessage( message );
          box.open();
        }

      }
    } catch ( Exception e ) {
      new ErrorDialog( hopGui.getShell(), "Error", "Error editing data set '" + dataSetName + "'", e );
    }
  }
}
