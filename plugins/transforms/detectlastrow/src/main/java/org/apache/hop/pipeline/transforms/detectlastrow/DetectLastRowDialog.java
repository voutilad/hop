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

package org.apache.hop.pipeline.transforms.detectlastrow;

import org.apache.hop.core.Const;
import org.apache.hop.core.annotations.PluginDialog;
import org.apache.hop.core.util.Utils;
import org.apache.hop.i18n.BaseMessages;
import org.apache.hop.pipeline.PipelineMeta;
import org.apache.hop.pipeline.transform.BaseTransformMeta;
import org.apache.hop.pipeline.transform.ITransformDialog;
import org.apache.hop.ui.core.widget.TextVar;
import org.apache.hop.ui.pipeline.transform.BaseTransformDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.*;

@PluginDialog(
        id = "DetectLastRow",
        image = "detectlastrow.svg",
        pluginType = PluginDialog.PluginType.TRANSFORM,
        documentationUrl = ""
)
public class DetectLastRowDialog extends BaseTransformDialog implements ITransformDialog {
  private static Class<?> PKG = DetectLastRowMeta.class; // for i18n purposes, needed by Translator!!

  private Label wlResult;
  private TextVar wResult;
  private FormData fdlResult, fdResult;

  private DetectLastRowMeta input;

  public DetectLastRowDialog( Shell parent, Object in, PipelineMeta pipelineMeta, String sname ) {
    super( parent, (BaseTransformMeta) in, pipelineMeta, sname );
    input = (DetectLastRowMeta) in;
  }

  public String open() {
    Shell parent = getParent();
    Display display = parent.getDisplay();

    shell = new Shell( parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MAX | SWT.MIN );
    props.setLook( shell );
    setShellImage( shell, input );

    ModifyListener lsMod = new ModifyListener() {
      public void modifyText( ModifyEvent e ) {
        input.setChanged();
      }
    };

    changed = input.hasChanged();

    FormLayout formLayout = new FormLayout();
    formLayout.marginWidth = Const.FORM_MARGIN;
    formLayout.marginHeight = Const.FORM_MARGIN;

    shell.setLayout( formLayout );
    shell.setText( BaseMessages.getString( PKG, "DetectLastRowDialog.Shell.Title" ) );

    int middle = props.getMiddlePct();
    int margin = props.getMargin();

    // TransformName line
    wlTransformName = new Label( shell, SWT.RIGHT );
    wlTransformName.setText( BaseMessages.getString( PKG, "DetectLastRowDialog.TransformName.Label" ) );
    props.setLook( wlTransformName );
    fdlTransformName = new FormData();
    fdlTransformName.left = new FormAttachment( 0, 0 );
    fdlTransformName.right = new FormAttachment( middle, -margin );
    fdlTransformName.top = new FormAttachment( 0, margin );
    wlTransformName.setLayoutData( fdlTransformName );
    wTransformName = new Text( shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
    wTransformName.setText( transformName );
    props.setLook( wTransformName );
    wTransformName.addModifyListener( lsMod );
    fdTransformName = new FormData();
    fdTransformName.left = new FormAttachment( middle, 0 );
    fdTransformName.top = new FormAttachment( 0, margin );
    fdTransformName.right = new FormAttachment( 100, 0 );
    wTransformName.setLayoutData( fdTransformName );

    // Result fieldname ...
    wlResult = new Label( shell, SWT.RIGHT );
    wlResult.setText( BaseMessages.getString( PKG, "DetectLastRowDialog.ResultField.Label" ) );
    props.setLook( wlResult );
    fdlResult = new FormData();
    fdlResult.left = new FormAttachment( 0, 0 );
    fdlResult.right = new FormAttachment( middle, -margin );
    fdlResult.top = new FormAttachment( wTransformName, margin * 2 );
    wlResult.setLayoutData( fdlResult );

    wResult = new TextVar( pipelineMeta, shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
    wResult.setToolTipText( BaseMessages.getString( PKG, "DetectLastRowDialog.ResultField.Tooltip" ) );
    props.setLook( wResult );
    wResult.addModifyListener( lsMod );
    fdResult = new FormData();
    fdResult.left = new FormAttachment( middle, 0 );
    fdResult.top = new FormAttachment( wTransformName, margin * 2 );
    fdResult.right = new FormAttachment( 100, 0 );
    wResult.setLayoutData( fdResult );

    // THE BUTTONS
    wOk = new Button( shell, SWT.PUSH );
    wOk.setText( BaseMessages.getString( PKG, "System.Button.OK" ) );
    wCancel = new Button( shell, SWT.PUSH );
    wCancel.setText( BaseMessages.getString( PKG, "System.Button.Cancel" ) );

    setButtonPositions( new Button[] { wOk, wCancel }, margin, wResult );

    // Add listeners
    lsOk = new Listener() {
      public void handleEvent( Event e ) {
        ok();
      }
    };

    lsCancel = new Listener() {
      public void handleEvent( Event e ) {
        cancel();
      }
    };

    wOk.addListener( SWT.Selection, lsOk );
    wCancel.addListener( SWT.Selection, lsCancel );

    lsDef = new SelectionAdapter() {
      public void widgetDefaultSelected( SelectionEvent e ) {
        ok();
      }
    };

    wTransformName.addSelectionListener( lsDef );

    // Detect X or ALT-F4 or something that kills this window...
    shell.addShellListener( new ShellAdapter() {
      public void shellClosed( ShellEvent e ) {
        cancel();
      }
    } );

    // Set the shell size, based upon previous time...
    setSize();

    getData();
    input.setChanged( changed );

    shell.open();
    while ( !shell.isDisposed() ) {
      if ( !display.readAndDispatch() ) {
        display.sleep();
      }
    }
    return transformName;
  }

  /**
   * Copy information from the meta-data input to the dialog fields.
   */
  public void getData() {
    if ( log.isDebug() ) {
      logDebug( BaseMessages.getString( PKG, "DetectLastRowDialog.Log.GettingKeyInfo" ) );
    }

    if ( input.getResultFieldName() != null ) {
      wResult.setText( input.getResultFieldName() );
    }

    wTransformName.selectAll();
    wTransformName.setFocus();
  }

  private void cancel() {
    transformName = null;
    input.setChanged( changed );
    dispose();
  }

  private void ok() {
    if ( Utils.isEmpty( wTransformName.getText() ) ) {
      return;
    }
    input.setResultFieldName( wResult.getText() );
    transformName = wTransformName.getText(); // return value

    dispose();
  }
}
