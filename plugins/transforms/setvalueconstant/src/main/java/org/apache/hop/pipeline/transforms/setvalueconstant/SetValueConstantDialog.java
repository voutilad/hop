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

package org.apache.hop.pipeline.transforms.setvalueconstant;

import org.apache.hop.core.Const;
import org.apache.hop.core.annotations.PluginDialog;
import org.apache.hop.core.exception.HopException;
import org.apache.hop.core.row.IRowMeta;
import org.apache.hop.core.row.IValueMeta;
import org.apache.hop.core.util.Utils;
import org.apache.hop.i18n.BaseMessages;
import org.apache.hop.pipeline.PipelineMeta;
import org.apache.hop.pipeline.transform.BaseTransformMeta;
import org.apache.hop.pipeline.transform.ITransformDialog;
import org.apache.hop.pipeline.transform.TransformMeta;
import org.apache.hop.pipeline.transforms.setvalueconstant.SetValueConstantMeta;
import org.apache.hop.ui.core.dialog.ErrorDialog;
import org.apache.hop.ui.core.widget.ColumnInfo;
import org.apache.hop.ui.core.widget.TableView;
import org.apache.hop.ui.pipeline.transform.BaseTransformDialog;
import org.apache.hop.ui.pipeline.transform.ITableItemInsertListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@PluginDialog(
        id = "SetValueConstant",
        image = "setvalueconstant.svg",
        pluginType = PluginDialog.PluginType.TRANSFORM,
        documentationUrl = "http://www.project-hop.org/manual/latest/plugins/transforms/selectvalues.html"
)
public class SetValueConstantDialog extends BaseTransformDialog implements ITransformDialog {
  private static Class<?> PKG = SetValueConstantMeta.class; // for i18n purposes, needed by Translator!!

  private SetValueConstantMeta input;

  private ModifyListener lsMod;
  private ModifyListener oldlsMod;
  private int middle;
  private int margin;

  /**
   * all fields from the previous transforms
   */
  private Map<String, Integer> inputFields;

  private Label wlFields;
  private TableView wFields;
  private FormData fdlFields, fdFields;

  private ColumnInfo[] colinf;

  private Label wluseVars;
  private Button wuseVars;

  public SetValueConstantDialog( Shell parent, Object in, PipelineMeta tr, String sname ) {
    super( parent, (BaseTransformMeta) in, tr, sname );
    input = (SetValueConstantMeta) in;
    inputFields = new HashMap<String, Integer>();
  }

  public String open() {
    Shell parent = getParent();
    Display display = parent.getDisplay();

    shell = new Shell( parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MIN | SWT.MAX );
    props.setLook( shell );
    setShellImage( shell, input );

    lsMod = new ModifyListener() {
      public void modifyText( ModifyEvent e ) {
        input.setChanged();
      }
    };
    changed = input.hasChanged();
    oldlsMod = lsMod;
    FormLayout formLayout = new FormLayout();
    formLayout.marginWidth = Const.FORM_MARGIN;
    formLayout.marginHeight = Const.FORM_MARGIN;

    middle = props.getMiddlePct();
    margin = props.getMargin();

    shell.setLayout( formLayout );
    shell.setText( BaseMessages.getString( PKG, "SetValueConstantDialog.Shell.Title" ) );

    // TransformName line
    wlTransformName = new Label( shell, SWT.RIGHT );
    wlTransformName.setText( BaseMessages.getString( PKG, "SetValueConstantDialog.TransformName.Label" ) );
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

    // Use variable?
    wluseVars = new Label( shell, SWT.RIGHT );
    wluseVars.setText( BaseMessages.getString( PKG, "SetValueConstantDialog.useVars.Label" ) );
    props.setLook( wluseVars );
    FormData fdlUpdate = new FormData();
    fdlUpdate.left = new FormAttachment( 0, 0 );
    fdlUpdate.right = new FormAttachment( middle, -margin );
    fdlUpdate.top = new FormAttachment( wTransformName, 2 * margin );
    wluseVars.setLayoutData( fdlUpdate );
    wuseVars = new Button( shell, SWT.CHECK );
    wuseVars.setToolTipText( BaseMessages.getString( PKG, "SetValueConstantDialog.useVars.Tooltip" ) );
    props.setLook( wuseVars );
    FormData fdUpdate = new FormData();
    fdUpdate.left = new FormAttachment( middle, 0 );
    fdUpdate.top = new FormAttachment( wTransformName, 2 * margin );
    fdUpdate.right = new FormAttachment( 100, 0 );
    wuseVars.setLayoutData( fdUpdate );

    // Table with fields
    wlFields = new Label( shell, SWT.NONE );
    wlFields.setText( BaseMessages.getString( PKG, "SetValueConstantDialog.Fields.Label" ) );
    props.setLook( wlFields );
    fdlFields = new FormData();
    fdlFields.left = new FormAttachment( 0, 0 );
    fdlFields.top = new FormAttachment( wuseVars, margin );
    wlFields.setLayoutData( fdlFields );

    int FieldsCols = 4;
    final int FieldsRows = input.getFields().size();
    colinf = new ColumnInfo[ FieldsCols ];
    colinf[ 0 ] =
      new ColumnInfo(
        BaseMessages.getString( PKG, "SetValueConstantDialog.Fieldname.Column" ),
        ColumnInfo.COLUMN_TYPE_CCOMBO, new String[] {}, false );
    colinf[ 1 ] =
      new ColumnInfo(
        BaseMessages.getString( PKG, "SetValueConstantDialog.Value.Column" ), ColumnInfo.COLUMN_TYPE_TEXT,
        false );
    colinf[ 2 ] =
      new ColumnInfo(
        BaseMessages.getString( PKG, "SetValueConstantDialog.Value.ConversionMask" ),
        ColumnInfo.COLUMN_TYPE_CCOMBO, Const.getDateFormats() );
    colinf[ 3 ] =
      new ColumnInfo(
        BaseMessages.getString( PKG, "SetValueConstantDialog.Value.SetEmptyString" ),
        ColumnInfo.COLUMN_TYPE_CCOMBO,
        new String[] {
          BaseMessages.getString( PKG, "System.Combo.Yes" ), BaseMessages.getString( PKG, "System.Combo.No" ) } );

    wFields =
      new TableView(
        pipelineMeta, shell, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI, colinf, FieldsRows, oldlsMod, props );

    fdFields = new FormData();
    fdFields.left = new FormAttachment( 0, 0 );
    fdFields.top = new FormAttachment( wlFields, margin );
    fdFields.right = new FormAttachment( 100, 0 );
    fdFields.bottom = new FormAttachment( 100, -50 );

    wFields.setLayoutData( fdFields );

    //
    // Search the fields in the background
    //

    final Runnable runnable = new Runnable() {
      public void run() {
        TransformMeta transformMeta = pipelineMeta.findTransform( transformName );
        if ( transformMeta != null ) {
          try {
            IRowMeta row = pipelineMeta.getPrevTransformFields( transformMeta );

            // Remember these fields...
            for ( int i = 0; i < row.size(); i++ ) {
              inputFields.put( row.getValueMeta( i ).getName(), Integer.valueOf( i ) );
            }
            setComboBoxes();
          } catch ( HopException e ) {
            logError( BaseMessages.getString( PKG, "System.Dialog.GetFieldsFailed.Message" ) );
          }
        }
      }
    };
    new Thread( runnable ).start();

    wOk = new Button( shell, SWT.PUSH );
    wOk.setText( BaseMessages.getString( PKG, "System.Button.OK" ) );
    wGet = new Button( shell, SWT.PUSH );
    wGet.setText( BaseMessages.getString( PKG, "System.Button.GetFields" ) );
    wCancel = new Button( shell, SWT.PUSH );
    wCancel.setText( BaseMessages.getString( PKG, "System.Button.Cancel" ) );

    setButtonPositions( new Button[] { wOk, wGet, wCancel }, margin, wFields );

    // Add listeners
    lsCancel = new Listener() {
      public void handleEvent( Event e ) {
        cancel();
      }
    };
    lsGet = new Listener() {
      public void handleEvent( Event e ) {
        get();
      }
    };
    lsOk = new Listener() {
      public void handleEvent( Event e ) {
        ok();
      }
    };

    wCancel.addListener( SWT.Selection, lsCancel );
    wOk.addListener( SWT.Selection, lsOk );
    wGet.addListener( SWT.Selection, lsGet );

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

  protected void setComboBoxes() {
    // Something was changed in the row.
    //
    final Map<String, Integer> fields = new HashMap<String, Integer>();

    // Add the currentMeta fields...
    fields.putAll( inputFields );

    Set<String> keySet = fields.keySet();
    List<String> entries = new ArrayList<>( keySet );

    String[] fieldNames = entries.toArray( new String[ entries.size() ] );

    Const.sortStrings( fieldNames );
    colinf[ 0 ].setComboValues( fieldNames );
  }

  private void get() {
    try {
      IRowMeta r = pipelineMeta.getPrevTransformFields( transformMeta );
      if ( r != null ) {
        ITableItemInsertListener insertListener = new ITableItemInsertListener() {
          public boolean tableItemInserted( TableItem tableItem, IValueMeta v ) {
            return true;
          }
        };

        BaseTransformDialog
          .getFieldsFromPrevious( r, wFields, 1, new int[] { 1 }, new int[] {}, -1, -1, insertListener );
      }
    } catch ( HopException ke ) {
      new ErrorDialog( shell, BaseMessages.getString( PKG, "System.Dialog.GetFieldsFailed.Title" ), BaseMessages
        .getString( PKG, "System.Dialog.GetFieldsFailed.Message" ), ke );
    }
  }

  /**
   * Copy information from the meta-data input to the dialog fields.
   */
  public void getData() {
    wuseVars.setSelection( input.isUseVars() );
    Table table = wFields.table;
    if ( input.getFields().size() > 0 ) {
      table.removeAll();
    }
    for ( int i = 0; i < input.getFields().size(); i++ ) {
      SetValueConstantMeta.Field field = input.getField( i );
      TableItem ti = new TableItem( table, SWT.NONE );
      ti.setText( 0, "" + ( i + 1 ) );
      if ( field.getFieldName() != null ) {
        ti.setText( 1, field.getFieldName() );
      }
      if ( field.getReplaceValue() != null ) {
        ti.setText( 2, field.getReplaceValue() );
      }
      if ( field.getReplaceMask() != null ) {
        ti.setText( 3, field.getReplaceMask() );
      }
      ti.setText( 4, field.isEmptyString() ? BaseMessages.getString( PKG, "System.Combo.Yes" ) : BaseMessages
        .getString( PKG, "System.Combo.No" ) );

    }

    wFields.setRowNums();
    wFields.removeEmptyRows();
    wFields.optWidth( true );

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
    transformName = wTransformName.getText(); // return value

    input.setUseVars( wuseVars.getSelection() );
    int count = wFields.nrNonEmpty();
    List<SetValueConstantMeta.Field> fields = new ArrayList<>();

    //CHECKSTYLE:Indentation:OFF
    for ( int i = 0; i < count; i++ ) {
      TableItem ti = wFields.getNonEmpty( i );
      SetValueConstantMeta.Field field = new SetValueConstantMeta.Field();
      field.setFieldName( ti.getText( 1 ) );
      field.setEmptyString( BaseMessages.getString( PKG, "System.Combo.Yes" ).equalsIgnoreCase( ti.getText( 4 ) ) );
      field.setReplaceValue( field.isEmptyString() ? "" : ti.getText( 2 ) );
      field.setReplaceMask( field.isEmptyString() ? "" : ti.getText( 3 ) );
      fields.add( field );
    }
    input.setFields( fields );

    dispose();
  }
}
