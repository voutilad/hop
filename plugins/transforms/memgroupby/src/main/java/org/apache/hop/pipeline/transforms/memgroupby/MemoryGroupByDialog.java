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

package org.apache.hop.pipeline.transforms.memgroupby;

import org.apache.hop.core.Const;
import org.apache.hop.core.annotations.PluginDialog;
import org.apache.hop.core.exception.HopException;
import org.apache.hop.core.row.IRowMeta;
import org.apache.hop.core.util.Utils;
import org.apache.hop.i18n.BaseMessages;
import org.apache.hop.pipeline.PipelineMeta;
import org.apache.hop.pipeline.transform.BaseTransformMeta;
import org.apache.hop.pipeline.transform.ITransformDialog;
import org.apache.hop.pipeline.transform.TransformMeta;
import org.apache.hop.ui.core.dialog.ErrorDialog;
import org.apache.hop.ui.core.widget.ColumnInfo;
import org.apache.hop.ui.core.widget.TableView;
import org.apache.hop.ui.pipeline.transform.BaseTransformDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
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
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@PluginDialog(
        id = "MemoryGroupBy",
        image = "memorygroupby.svg",
        pluginType = PluginDialog.PluginType.TRANSFORM,
        documentationUrl = ""
)
public class MemoryGroupByDialog extends BaseTransformDialog implements ITransformDialog {
  private static Class<?> PKG = MemoryGroupByMeta.class; // for i18n purposes, needed by Translator!!

  private Label wlGroup;
  private TableView wGroup;
  private FormData fdlGroup, fdGroup;

  private Label wlAgg;
  private TableView wAgg;
  private FormData fdlAgg, fdAgg;

  private Label wlAlwaysAddResult;
  private Button wAlwaysAddResult;
  private FormData fdlAlwaysAddResult, fdAlwaysAddResult;

  private Button wGet, wGetAgg;
  private FormData fdGet, fdGetAgg;
  private Listener lsGet, lsGetAgg;

  private MemoryGroupByMeta input;

  private ColumnInfo[] ciKey;
  private ColumnInfo[] ciReturn;

  private Map<String, Integer> inputFields;

  public MemoryGroupByDialog( Shell parent, Object in, PipelineMeta pipelineMeta, String sname ) {
    super( parent, (BaseTransformMeta) in, pipelineMeta, sname );
    input = (MemoryGroupByMeta) in;
    inputFields = new HashMap<String, Integer>();
  }

  @Override
  public String open() {
    Shell parent = getParent();
    Display display = parent.getDisplay();

    shell = new Shell( parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MAX | SWT.MIN );
    props.setLook( shell );
    setShellImage( shell, input );

    ModifyListener lsMod = new ModifyListener() {
      @Override
      public void modifyText( ModifyEvent e ) {
        input.setChanged();
      }
    };
    SelectionListener lsSel = new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent arg0 ) {
        input.setChanged();
      }
    };
    backupChanged = input.hasChanged();

    FormLayout formLayout = new FormLayout();
    formLayout.marginWidth = Const.FORM_MARGIN;
    formLayout.marginHeight = Const.FORM_MARGIN;

    shell.setLayout( formLayout );
    shell.setText( BaseMessages.getString( PKG, "MemoryGroupByDialog.Shell.Title" ) );

    int middle = props.getMiddlePct();
    int margin = props.getMargin();

    // TransformName line
    wlTransformName = new Label( shell, SWT.RIGHT );
    wlTransformName.setText( BaseMessages.getString( PKG, "MemoryGroupByDialog.TransformName.Label" ) );
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

    // Always pass a result rows as output
    //
    wlAlwaysAddResult = new Label( shell, SWT.RIGHT );
    wlAlwaysAddResult.setText( BaseMessages.getString( PKG, "MemoryGroupByDialog.AlwaysAddResult.Label" ) );
    wlAlwaysAddResult
      .setToolTipText( BaseMessages.getString( PKG, "MemoryGroupByDialog.AlwaysAddResult.ToolTip" ) );
    props.setLook( wlAlwaysAddResult );
    fdlAlwaysAddResult = new FormData();
    fdlAlwaysAddResult.left = new FormAttachment( 0, 0 );
    fdlAlwaysAddResult.top = new FormAttachment( wTransformName, margin );
    fdlAlwaysAddResult.right = new FormAttachment( middle, -margin );
    wlAlwaysAddResult.setLayoutData( fdlAlwaysAddResult );
    wAlwaysAddResult = new Button( shell, SWT.CHECK );
    wAlwaysAddResult.setToolTipText( BaseMessages.getString( PKG, "MemoryGroupByDialog.AlwaysAddResult.ToolTip" ) );
    props.setLook( wAlwaysAddResult );
    fdAlwaysAddResult = new FormData();
    fdAlwaysAddResult.left = new FormAttachment( middle, 0 );
    fdAlwaysAddResult.top = new FormAttachment( wTransformName, margin );
    fdAlwaysAddResult.right = new FormAttachment( 100, 0 );
    wAlwaysAddResult.setLayoutData( fdAlwaysAddResult );
    wAlwaysAddResult.addSelectionListener( lsSel );

    wlGroup = new Label( shell, SWT.NONE );
    wlGroup.setText( BaseMessages.getString( PKG, "MemoryGroupByDialog.Group.Label" ) );
    props.setLook( wlGroup );
    fdlGroup = new FormData();
    fdlGroup.left = new FormAttachment( 0, 0 );
    fdlGroup.top = new FormAttachment( wAlwaysAddResult, margin );
    wlGroup.setLayoutData( fdlGroup );

    int nrKeyCols = 1;
    int nrKeyRows = ( input.getGroupField() != null ? input.getGroupField().length : 1 );

    ciKey = new ColumnInfo[ nrKeyCols ];
    ciKey[ 0 ] =
      new ColumnInfo(
        BaseMessages.getString( PKG, "MemoryGroupByDialog.ColumnInfo.GroupField" ),
        ColumnInfo.COLUMN_TYPE_CCOMBO, new String[] { "" }, false );

    wGroup =
      new TableView(
        pipelineMeta, shell, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL, ciKey,
        nrKeyRows, lsMod, props );

    wGet = new Button( shell, SWT.PUSH );
    wGet.setText( BaseMessages.getString( PKG, "MemoryGroupByDialog.GetFields.Button" ) );
    fdGet = new FormData();
    fdGet.top = new FormAttachment( wlGroup, margin );
    fdGet.right = new FormAttachment( 100, 0 );
    wGet.setLayoutData( fdGet );

    fdGroup = new FormData();
    fdGroup.left = new FormAttachment( 0, 0 );
    fdGroup.top = new FormAttachment( wlGroup, margin );
    fdGroup.right = new FormAttachment( wGet, -margin );
    fdGroup.bottom = new FormAttachment( 45, 0 );
    wGroup.setLayoutData( fdGroup );

    // THE Aggregate fields
    wlAgg = new Label( shell, SWT.NONE );
    wlAgg.setText( BaseMessages.getString( PKG, "MemoryGroupByDialog.Aggregates.Label" ) );
    props.setLook( wlAgg );
    fdlAgg = new FormData();
    fdlAgg.left = new FormAttachment( 0, 0 );
    fdlAgg.top = new FormAttachment( wGroup, margin );
    wlAgg.setLayoutData( fdlAgg );

    int UpInsCols = 4;
    int UpInsRows = ( input.getAggregateField() != null ? input.getAggregateField().length : 1 );

    ciReturn = new ColumnInfo[ UpInsCols ];
    ciReturn[ 0 ] =
      new ColumnInfo(
        BaseMessages.getString( PKG, "MemoryGroupByDialog.ColumnInfo.Name" ), ColumnInfo.COLUMN_TYPE_TEXT,
        false );
    ciReturn[ 1 ] =
      new ColumnInfo(
        BaseMessages.getString( PKG, "MemoryGroupByDialog.ColumnInfo.Subject" ),
        ColumnInfo.COLUMN_TYPE_CCOMBO, new String[] { "" }, false );
    ciReturn[ 2 ] =
      new ColumnInfo(
        BaseMessages.getString( PKG, "MemoryGroupByDialog.ColumnInfo.Type" ), ColumnInfo.COLUMN_TYPE_CCOMBO,
        MemoryGroupByMeta.typeGroupLongDesc );
    ciReturn[ 3 ] =
      new ColumnInfo(
        BaseMessages.getString( PKG, "MemoryGroupByDialog.ColumnInfo.Value" ), ColumnInfo.COLUMN_TYPE_TEXT,
        false );
    ciReturn[ 3 ].setToolTip( BaseMessages.getString( PKG, "MemoryGroupByDialog.ColumnInfo.Value.Tooltip" ) );
    ciReturn[ 3 ].setUsingVariables( true );

    wAgg =
      new TableView(
        pipelineMeta, shell, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL, ciReturn,
        UpInsRows, lsMod, props );

    wGetAgg = new Button( shell, SWT.PUSH );
    wGetAgg.setText( BaseMessages.getString( PKG, "MemoryGroupByDialog.GetLookupFields.Button" ) );
    fdGetAgg = new FormData();
    fdGetAgg.top = new FormAttachment( wlAgg, margin );
    fdGetAgg.right = new FormAttachment( 100, 0 );
    wGetAgg.setLayoutData( fdGetAgg );

    //
    // Search the fields in the background

    final Runnable runnable = new Runnable() {
      @Override
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

    // THE BUTTONS
    wOk = new Button( shell, SWT.PUSH );
    wOk.setText( BaseMessages.getString( PKG, "System.Button.OK" ) );
    wCancel = new Button( shell, SWT.PUSH );
    wCancel.setText( BaseMessages.getString( PKG, "System.Button.Cancel" ) );

    setButtonPositions( new Button[] { wOk, wCancel }, margin, null );

    fdAgg = new FormData();
    fdAgg.left = new FormAttachment( 0, 0 );
    fdAgg.top = new FormAttachment( wlAgg, margin );
    fdAgg.right = new FormAttachment( wGetAgg, -margin );
    fdAgg.bottom = new FormAttachment( wOk, -margin );
    wAgg.setLayoutData( fdAgg );

    // Add listeners
    lsOk = new Listener() {
      @Override
      public void handleEvent( Event e ) {
        ok();
      }
    };
    lsGet = new Listener() {
      @Override
      public void handleEvent( Event e ) {
        get();
      }
    };
    lsGetAgg = new Listener() {
      @Override
      public void handleEvent( Event e ) {
        getAgg();
      }
    };
    lsCancel = new Listener() {
      @Override
      public void handleEvent( Event e ) {
        cancel();
      }
    };

    wOk.addListener( SWT.Selection, lsOk );
    wGet.addListener( SWT.Selection, lsGet );
    wGetAgg.addListener( SWT.Selection, lsGetAgg );
    wCancel.addListener( SWT.Selection, lsCancel );

    lsDef = new SelectionAdapter() {
      @Override
      public void widgetDefaultSelected( SelectionEvent e ) {
        ok();
      }
    };

    wTransformName.addSelectionListener( lsDef );

    // Detect X or ALT-F4 or something that kills this window...
    shell.addShellListener( new ShellAdapter() {
      @Override
      public void shellClosed( ShellEvent e ) {
        cancel();
      }
    } );

    // Set the shell size, based upon previous time...
    setSize();

    getData();
    input.setChanged( backupChanged );

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
    ciKey[ 0 ].setComboValues( fieldNames );
    ciReturn[ 1 ].setComboValues( fieldNames );
  }

  /**
   * Copy information from the meta-data input to the dialog fields.
   */
  public void getData() {
    logDebug( BaseMessages.getString( PKG, "MemoryGroupByDialog.Log.GettingKeyInfo" ) );

    wAlwaysAddResult.setSelection( input.isAlwaysGivingBackOneRow() );

    if ( input.getGroupField() != null ) {
      for ( int i = 0; i < input.getGroupField().length; i++ ) {
        TableItem item = wGroup.table.getItem( i );
        if ( input.getGroupField()[ i ] != null ) {
          item.setText( 1, input.getGroupField()[ i ] );
        }
      }
    }

    if ( input.getAggregateField() != null ) {
      for ( int i = 0; i < input.getAggregateField().length; i++ ) {
        TableItem item = wAgg.table.getItem( i );
        if ( input.getAggregateField()[ i ] != null ) {
          item.setText( 1, input.getAggregateField()[ i ] );
        }
        if ( input.getSubjectField()[ i ] != null ) {
          item.setText( 2, input.getSubjectField()[ i ] );
        }
        item.setText( 3, Const.NVL( MemoryGroupByMeta.getTypeDescLong( input.getAggregateType()[ i ] ), "" ) );
        if ( input.getValueField()[ i ] != null ) {
          item.setText( 4, input.getValueField()[ i ] );
        }
      }
    }

    wGroup.setRowNums();
    wGroup.optWidth( true );
    wAgg.setRowNums();
    wAgg.optWidth( true );

    wTransformName.selectAll();
    wTransformName.setFocus();
  }

  private void cancel() {
    transformName = null;
    input.setChanged( backupChanged );
    dispose();
  }

  private void ok() {
    if ( Utils.isEmpty( wTransformName.getText() ) ) {
      return;
    }

    int sizegroup = wGroup.nrNonEmpty();
    int nrFields = wAgg.nrNonEmpty();

    input.setAlwaysGivingBackOneRow( wAlwaysAddResult.getSelection() );

    input.allocate( sizegroup, nrFields );

    //CHECKSTYLE:Indentation:OFF
    for ( int i = 0; i < sizegroup; i++ ) {
      TableItem item = wGroup.getNonEmpty( i );
      input.getGroupField()[ i ] = item.getText( 1 );
    }

    //CHECKSTYLE:Indentation:OFF
    for ( int i = 0; i < nrFields; i++ ) {
      TableItem item = wAgg.getNonEmpty( i );
      input.getAggregateField()[ i ] = item.getText( 1 );
      input.getSubjectField()[ i ] = item.getText( 2 );
      input.getAggregateType()[ i ] = MemoryGroupByMeta.getType( item.getText( 3 ) );
      input.getValueField()[ i ] = item.getText( 4 );
    }

    transformName = wTransformName.getText();

    dispose();
  }

  private void get() {
    try {
      IRowMeta r = pipelineMeta.getPrevTransformFields( transformName );
      if ( r != null && !r.isEmpty() ) {
        BaseTransformDialog.getFieldsFromPrevious( r, wGroup, 1, new int[] { 1 }, new int[] {}, -1, -1, null );
      }
    } catch ( HopException ke ) {
      new ErrorDialog(
        shell, BaseMessages.getString( PKG, "MemoryGroupByDialog.FailedToGetFields.DialogTitle" ), BaseMessages
        .getString( PKG, "MemoryGroupByDialog.FailedToGetFields.DialogMessage" ), ke );
    }
  }

  private void getAgg() {
    try {
      IRowMeta r = pipelineMeta.getPrevTransformFields( transformName );
      if ( r != null && !r.isEmpty() ) {
        BaseTransformDialog.getFieldsFromPrevious( r, wAgg, 1, new int[] { 1, 2 }, new int[] {}, -1, -1, null );
      }
    } catch ( HopException ke ) {
      new ErrorDialog(
        shell, BaseMessages.getString( PKG, "MemoryGroupByDialog.FailedToGetFields.DialogTitle" ), BaseMessages
        .getString( PKG, "MemoryGroupByDialog.FailedToGetFields.DialogMessage" ), ke );
    }
  }
}
