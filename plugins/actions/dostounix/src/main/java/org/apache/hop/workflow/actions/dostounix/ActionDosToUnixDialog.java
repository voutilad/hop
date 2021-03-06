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

package org.apache.hop.workflow.actions.dostounix;

import org.apache.hop.core.Const;
import org.apache.hop.core.annotations.PluginDialog;
import org.apache.hop.core.extension.ExtensionPointHandler;
import org.apache.hop.core.logging.LogChannel;
import org.apache.hop.core.util.Utils;
import org.apache.hop.i18n.BaseMessages;
import org.apache.hop.ui.core.PropsUi;
import org.apache.hop.ui.core.dialog.BaseDialog;
import org.apache.hop.ui.hopgui.HopGuiExtensionPoint;
import org.apache.hop.ui.hopgui.delegates.HopGuiDirectoryDialogExtension;
import org.apache.hop.ui.workflow.dialog.WorkflowDialog;
import org.apache.hop.ui.workflow.action.ActionDialog;
import org.apache.hop.workflow.WorkflowMeta;
import org.apache.hop.workflow.action.IAction;
import org.apache.hop.workflow.action.IActionDialog;
import org.apache.hop.ui.core.gui.WindowProperty;
import org.apache.hop.ui.core.widget.ColumnInfo;
import org.apache.hop.ui.core.widget.TableView;
import org.apache.hop.ui.core.widget.TextVar;
import org.apache.hop.ui.pipeline.transform.BaseTransformDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This dialog allows you to edit the XML valid action settings.
 *
 * @author Samatar Hassan
 * @since 26-03-2008
 */
@PluginDialog( 
		  id = "DOS_UNIX_CONVERTER", 
		  image = "DosToUnix.svg", 
		  pluginType = PluginDialog.PluginType.ACTION,
		  documentationUrl = "https://www.project-hop.org/manual/latest/plugins/actions/"
)
public class ActionDosToUnixDialog extends ActionDialog implements IActionDialog {
  private static Class<?> PKG = ActionDosToUnix.class; // for i18n purposes, needed by Translator!!

  private static final String[] FILETYPES = new String[] {
    BaseMessages.getString( PKG, "JobDosToUnix.Filetype.Xml" ),
    BaseMessages.getString( PKG, "JobDosToUnix.Filetype.All" ) };

  private Label wlName;
  private Text wName;
  private FormData fdlName, fdName;

  private Label wlSourceFileFolder;
  private Button wbSourceFileFolder, wbSourceDirectory;

  private TextVar wSourceFileFolder;
  private FormData fdlSourceFileFolder, fdbSourceFileFolder, fdSourceFileFolder, fdbSourceDirectory;

  private Label wlIncludeSubfolders;
  private Button wIncludeSubfolders;
  private FormData fdlIncludeSubfolders, fdIncludeSubfolders;

  private Button wOk, wCancel;
  private Listener lsOk, lsCancel;

  private ActionDosToUnix action;
  private Shell shell;

  private SelectionAdapter lsDef;

  private boolean changed;

  private Label wlPrevious;

  private Button wPrevious;

  private FormData fdlPrevious, fdPrevious;

  private Label wlFields;

  private TableView wFields;

  private FormData fdlFields, fdFields;

  private Group wSettings;
  private FormData fdSettings;

  private Label wlWildcard;
  private TextVar wWildcard;
  private FormData fdlWildcard, fdWildcard;

  private Button wbdSourceFileFolder; // Delete
  private Button wbeSourceFileFolder; // Edit
  private Button wbaSourceFileFolder; // Add or change

  private CTabFolder wTabFolder;
  private Composite wGeneralComp, wAdvancedComp;
  private CTabItem wGeneralTab, wAdvancedTab;
  private FormData fdGeneralComp, fdAdvancedComp;
  private FormData fdTabFolder;

  // Add File to result

  private Group wFileResult;
  private FormData fdFileResult;

  private Group wSuccessOn;
  private FormData fdSuccessOn;

  private FormData fdbeSourceFileFolder, fdbaSourceFileFolder, fdbdSourceFileFolder;

  private Label wlSuccessCondition, wlAddFilenameToResult;
  private CCombo wSuccessCondition, wAddFilenameToResult;
  private FormData fdlSuccessCondition, fdSuccessCondition, fdlAddFilenameToResult, fdAddFilenameToResult;

  private Label wlNrErrorsLessThan;
  private TextVar wNrErrorsLessThan;
  private FormData fdlNrErrorsLessThan, fdNrErrorsLessThan;

  public ActionDosToUnixDialog( Shell parent, IAction action, WorkflowMeta workflowMeta ) {
    super( parent, action, workflowMeta );
    this.action = (ActionDosToUnix) action;

    if ( this.action.getName() == null ) {
      this.action.setName( BaseMessages.getString( PKG, "JobDosToUnix.Name.Default" ) );
    }
  }

  @Override
  public IAction open() {
    Shell parent = getParent();
    Display display = parent.getDisplay();

    shell = new Shell( parent, props.getWorkflowsDialogStyle() );
    props.setLook( shell );
    WorkflowDialog.setShellImage( shell, action );

    ModifyListener lsMod = new ModifyListener() {
      public void modifyText( ModifyEvent e ) {
        action.setChanged();
      }
    };
    changed = action.hasChanged();

    FormLayout formLayout = new FormLayout();
    formLayout.marginWidth = Const.FORM_MARGIN;
    formLayout.marginHeight = Const.FORM_MARGIN;

    shell.setLayout( formLayout );
    shell.setText( BaseMessages.getString( PKG, "JobDosToUnix.Title" ) );

    int middle = props.getMiddlePct();
    int margin = Const.MARGIN;

    // Filename line
    wlName = new Label( shell, SWT.RIGHT );
    wlName.setText( BaseMessages.getString( PKG, "JobDosToUnix.Name.Label" ) );
    props.setLook( wlName );
    fdlName = new FormData();
    fdlName.left = new FormAttachment( 0, 0 );
    fdlName.right = new FormAttachment( middle, -margin );
    fdlName.top = new FormAttachment( 0, margin );
    wlName.setLayoutData( fdlName );
    wName = new Text( shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
    props.setLook( wName );
    wName.addModifyListener( lsMod );
    fdName = new FormData();
    fdName.left = new FormAttachment( middle, 0 );
    fdName.top = new FormAttachment( 0, margin );
    fdName.right = new FormAttachment( 100, 0 );
    wName.setLayoutData( fdName );

    wTabFolder = new CTabFolder( shell, SWT.BORDER );
    props.setLook( wTabFolder, PropsUi.WIDGET_STYLE_TAB );

    // ////////////////////////
    // START OF GENERAL TAB ///
    // ////////////////////////

    wGeneralTab = new CTabItem( wTabFolder, SWT.NONE );
    wGeneralTab.setText( BaseMessages.getString( PKG, "JobDosToUnix.Tab.General.Label" ) );

    wGeneralComp = new Composite( wTabFolder, SWT.NONE );
    props.setLook( wGeneralComp );

    FormLayout generalLayout = new FormLayout();
    generalLayout.marginWidth = 3;
    generalLayout.marginHeight = 3;
    wGeneralComp.setLayout( generalLayout );

    // SETTINGS grouping?
    // ////////////////////////
    // START OF SETTINGS GROUP
    //

    wSettings = new Group( wGeneralComp, SWT.SHADOW_NONE );
    props.setLook( wSettings );
    wSettings.setText( BaseMessages.getString( PKG, "JobDosToUnix.Settings.Label" ) );

    FormLayout groupLayout = new FormLayout();
    groupLayout.marginWidth = 10;
    groupLayout.marginHeight = 10;
    wSettings.setLayout( groupLayout );

    wlIncludeSubfolders = new Label( wSettings, SWT.RIGHT );
    wlIncludeSubfolders.setText( BaseMessages.getString( PKG, "JobDosToUnix.IncludeSubfolders.Label" ) );
    props.setLook( wlIncludeSubfolders );
    fdlIncludeSubfolders = new FormData();
    fdlIncludeSubfolders.left = new FormAttachment( 0, 0 );
    fdlIncludeSubfolders.top = new FormAttachment( wName, margin );
    fdlIncludeSubfolders.right = new FormAttachment( middle, -margin );
    wlIncludeSubfolders.setLayoutData( fdlIncludeSubfolders );
    wIncludeSubfolders = new Button( wSettings, SWT.CHECK );
    props.setLook( wIncludeSubfolders );
    wIncludeSubfolders.setToolTipText( BaseMessages.getString( PKG, "JobDosToUnix.IncludeSubfolders.Tooltip" ) );
    fdIncludeSubfolders = new FormData();
    fdIncludeSubfolders.left = new FormAttachment( middle, 0 );
    fdIncludeSubfolders.top = new FormAttachment( wName, margin );
    fdIncludeSubfolders.right = new FormAttachment( 100, 0 );
    wIncludeSubfolders.setLayoutData( fdIncludeSubfolders );
    wIncludeSubfolders.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {
        action.setChanged();
      }
    } );

    // previous
    wlPrevious = new Label( wSettings, SWT.RIGHT );
    wlPrevious.setText( BaseMessages.getString( PKG, "JobDosToUnix.Previous.Label" ) );
    props.setLook( wlPrevious );
    fdlPrevious = new FormData();
    fdlPrevious.left = new FormAttachment( 0, 0 );
    fdlPrevious.top = new FormAttachment( wIncludeSubfolders, margin );
    fdlPrevious.right = new FormAttachment( middle, -margin );
    wlPrevious.setLayoutData( fdlPrevious );
    wPrevious = new Button( wSettings, SWT.CHECK );
    props.setLook( wPrevious );
    wPrevious.setSelection( action.arg_from_previous );
    wPrevious.setToolTipText( BaseMessages.getString( PKG, "JobDosToUnix.Previous.Tooltip" ) );
    fdPrevious = new FormData();
    fdPrevious.left = new FormAttachment( middle, 0 );
    fdPrevious.top = new FormAttachment( wIncludeSubfolders, margin );
    fdPrevious.right = new FormAttachment( 100, 0 );
    wPrevious.setLayoutData( fdPrevious );
    wPrevious.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {

        refreshArgFromPrevious();

      }
    } );
    fdSettings = new FormData();
    fdSettings.left = new FormAttachment( 0, margin );
    fdSettings.top = new FormAttachment( wName, margin );
    fdSettings.right = new FormAttachment( 100, -margin );
    wSettings.setLayoutData( fdSettings );

    // ///////////////////////////////////////////////////////////
    // / END OF SETTINGS GROUP
    // ///////////////////////////////////////////////////////////

    // SourceFileFolder line
    wlSourceFileFolder = new Label( wGeneralComp, SWT.RIGHT );
    wlSourceFileFolder.setText( BaseMessages.getString( PKG, "JobDosToUnix.SourceFileFolder.Label" ) );
    props.setLook( wlSourceFileFolder );
    fdlSourceFileFolder = new FormData();
    fdlSourceFileFolder.left = new FormAttachment( 0, 0 );
    fdlSourceFileFolder.top = new FormAttachment( wSettings, 2 * margin );
    fdlSourceFileFolder.right = new FormAttachment( middle, -margin );
    wlSourceFileFolder.setLayoutData( fdlSourceFileFolder );

    // Browse Source folders button ...
    wbSourceDirectory = new Button( wGeneralComp, SWT.PUSH | SWT.CENTER );
    props.setLook( wbSourceDirectory );
    wbSourceDirectory.setText( BaseMessages.getString( PKG, "JobDosToUnix.BrowseFolders.Label" ) );
    fdbSourceDirectory = new FormData();
    fdbSourceDirectory.right = new FormAttachment( 100, 0 );
    fdbSourceDirectory.top = new FormAttachment( wSettings, margin );
    wbSourceDirectory.setLayoutData( fdbSourceDirectory );

    wbSourceDirectory.addListener( SWT.Selection, e->BaseDialog.presentDirectoryDialog( shell, wSourceFileFolder, workflowMeta ));

    // Browse Source files button ...
    wbSourceFileFolder = new Button( wGeneralComp, SWT.PUSH | SWT.CENTER );
    props.setLook( wbSourceFileFolder );
    wbSourceFileFolder.setText( BaseMessages.getString( PKG, "JobDosToUnix.BrowseFiles.Label" ) );
    fdbSourceFileFolder = new FormData();
    fdbSourceFileFolder.right = new FormAttachment( wbSourceDirectory, -margin );
    fdbSourceFileFolder.top = new FormAttachment( wSettings, margin );
    wbSourceFileFolder.setLayoutData( fdbSourceFileFolder );

    // Browse Destination file add button ...
    wbaSourceFileFolder = new Button( wGeneralComp, SWT.PUSH | SWT.CENTER );
    props.setLook( wbaSourceFileFolder );
    wbaSourceFileFolder.setText( BaseMessages.getString( PKG, "JobDosToUnix.FilenameAdd.Button" ) );
    fdbaSourceFileFolder = new FormData();
    fdbaSourceFileFolder.right = new FormAttachment( wbSourceFileFolder, -margin );
    fdbaSourceFileFolder.top = new FormAttachment( wSettings, margin );
    wbaSourceFileFolder.setLayoutData( fdbaSourceFileFolder );

    wSourceFileFolder = new TextVar( workflowMeta, wGeneralComp, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
    wSourceFileFolder.setToolTipText( BaseMessages.getString( PKG, "JobDosToUnix.SourceFileFolder.Tooltip" ) );

    props.setLook( wSourceFileFolder );
    wSourceFileFolder.addModifyListener( lsMod );
    fdSourceFileFolder = new FormData();
    fdSourceFileFolder.left = new FormAttachment( middle, 0 );
    fdSourceFileFolder.top = new FormAttachment( wSettings, 2 * margin );
    fdSourceFileFolder.right = new FormAttachment( wbSourceFileFolder, -55 );
    wSourceFileFolder.setLayoutData( fdSourceFileFolder );

    // Whenever something changes, set the tooltip to the expanded version:
    wSourceFileFolder.addModifyListener( new ModifyListener() {
      public void modifyText( ModifyEvent e ) {
        wSourceFileFolder.setToolTipText( workflowMeta.environmentSubstitute( wSourceFileFolder.getText() ) );
      }
    } );

    wbSourceFileFolder.addListener( SWT.Selection, e-> BaseDialog.presentFileDialog( shell, wSourceFileFolder, workflowMeta,
      new String[] { "*.xml;*.XML", "*" }, FILETYPES, true )
    );

    // Buttons to the right of the screen...
    wbdSourceFileFolder = new Button( wGeneralComp, SWT.PUSH | SWT.CENTER );
    props.setLook( wbdSourceFileFolder );
    wbdSourceFileFolder.setText( BaseMessages.getString( PKG, "JobDosToUnix.FilenameDelete.Button" ) );
    wbdSourceFileFolder.setToolTipText( BaseMessages.getString( PKG, "JobDosToUnix.FilenameDelete.Tooltip" ) );
    fdbdSourceFileFolder = new FormData();
    fdbdSourceFileFolder.right = new FormAttachment( 100, 0 );
    fdbdSourceFileFolder.top = new FormAttachment( wSourceFileFolder, 40 );
    wbdSourceFileFolder.setLayoutData( fdbdSourceFileFolder );

    wbeSourceFileFolder = new Button( wGeneralComp, SWT.PUSH | SWT.CENTER );
    props.setLook( wbeSourceFileFolder );
    wbeSourceFileFolder.setText( BaseMessages.getString( PKG, "JobDosToUnix.FilenameEdit.Button" ) );
    wbeSourceFileFolder.setToolTipText( BaseMessages.getString( PKG, "JobDosToUnix.FilenameEdit.Tooltip" ) );
    fdbeSourceFileFolder = new FormData();
    fdbeSourceFileFolder.right = new FormAttachment( 100, 0 );
    fdbeSourceFileFolder.left = new FormAttachment( wbdSourceFileFolder, 0, SWT.LEFT );
    fdbeSourceFileFolder.top = new FormAttachment( wbdSourceFileFolder, margin );
    wbeSourceFileFolder.setLayoutData( fdbeSourceFileFolder );

    // Wildcard
    wlWildcard = new Label( wGeneralComp, SWT.RIGHT );
    wlWildcard.setText( BaseMessages.getString( PKG, "JobDosToUnix.Wildcard.Label" ) );
    props.setLook( wlWildcard );
    fdlWildcard = new FormData();
    fdlWildcard.left = new FormAttachment( 0, 0 );
    fdlWildcard.top = new FormAttachment( wSourceFileFolder, margin );
    fdlWildcard.right = new FormAttachment( middle, -margin );
    wlWildcard.setLayoutData( fdlWildcard );

    wWildcard = new TextVar( workflowMeta, wGeneralComp, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
    wWildcard.setToolTipText( BaseMessages.getString( PKG, "JobDosToUnix.Wildcard.Tooltip" ) );
    props.setLook( wWildcard );
    wWildcard.addModifyListener( lsMod );
    fdWildcard = new FormData();
    fdWildcard.left = new FormAttachment( middle, 0 );
    fdWildcard.top = new FormAttachment( wSourceFileFolder, margin );
    fdWildcard.right = new FormAttachment( wbSourceFileFolder, -55 );
    wWildcard.setLayoutData( fdWildcard );

    wlFields = new Label( wGeneralComp, SWT.NONE );
    wlFields.setText( BaseMessages.getString( PKG, "JobDosToUnix.Fields.Label" ) );
    props.setLook( wlFields );
    fdlFields = new FormData();
    fdlFields.left = new FormAttachment( 0, 0 );
    fdlFields.right = new FormAttachment( middle, -margin );
    fdlFields.top = new FormAttachment( wWildcard, margin );
    wlFields.setLayoutData( fdlFields );

    int rows =
      action.source_filefolder == null ? 1 : ( action.source_filefolder.length == 0
        ? 0 : action.source_filefolder.length );
    final int FieldsRows = rows;

    ColumnInfo[] colinf =
      new ColumnInfo[] {
        new ColumnInfo(
          BaseMessages.getString( PKG, "JobDosToUnix.Fields.SourceFileFolder.Label" ),
          ColumnInfo.COLUMN_TYPE_TEXT, false ),
        new ColumnInfo(
          BaseMessages.getString( PKG, "JobDosToUnix.Fields.Wildcard.Label" ), ColumnInfo.COLUMN_TYPE_TEXT,
          false ),
        new ColumnInfo(
          BaseMessages.getString( PKG, "JobDosToUnix.Fields.ConversionType.Label" ),
          ColumnInfo.COLUMN_TYPE_CCOMBO, ActionDosToUnix.ConversionTypeDesc, false ), };

    colinf[ 0 ].setUsingVariables( true );
    colinf[ 0 ].setToolTip( BaseMessages.getString( PKG, "JobDosToUnix.Fields.SourceFileFolder.Tooltip" ) );
    colinf[ 1 ].setUsingVariables( true );
    colinf[ 1 ].setToolTip( BaseMessages.getString( PKG, "JobDosToUnix.Fields.Wildcard.Tooltip" ) );

    wFields =
      new TableView(
        workflowMeta, wGeneralComp, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI, colinf, FieldsRows, lsMod, props );

    fdFields = new FormData();
    fdFields.left = new FormAttachment( 0, 0 );
    fdFields.top = new FormAttachment( wlFields, margin );
    fdFields.right = new FormAttachment( wbeSourceFileFolder, -margin );
    fdFields.bottom = new FormAttachment( 100, -margin );
    wFields.setLayoutData( fdFields );

    refreshArgFromPrevious();

    // Add the file to the list of files...
    SelectionAdapter selA = new SelectionAdapter() {
      public void widgetSelected( SelectionEvent arg0 ) {
        wFields.add( new String[] { wSourceFileFolder.getText(), wWildcard.getText() } );
        wSourceFileFolder.setText( "" );

        wWildcard.setText( "" );
        wFields.removeEmptyRows();
        wFields.setRowNums();
        wFields.optWidth( true );
      }
    };
    wbaSourceFileFolder.addSelectionListener( selA );
    wSourceFileFolder.addSelectionListener( selA );

    // Delete files from the list of files...
    wbdSourceFileFolder.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent arg0 ) {
        int[] idx = wFields.getSelectionIndices();
        wFields.remove( idx );
        wFields.removeEmptyRows();
        wFields.setRowNums();
      }
    } );

    // Edit the selected file & remove from the list...
    wbeSourceFileFolder.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent arg0 ) {
        int idx = wFields.getSelectionIndex();
        if ( idx >= 0 ) {
          String[] string = wFields.getItem( idx );
          wSourceFileFolder.setText( string[ 0 ] );
          wWildcard.setText( string[ 1 ] );
          wFields.remove( idx );
        }
        wFields.removeEmptyRows();
        wFields.setRowNums();
      }
    } );

    fdGeneralComp = new FormData();
    fdGeneralComp.left = new FormAttachment( 0, 0 );
    fdGeneralComp.top = new FormAttachment( 0, 0 );
    fdGeneralComp.right = new FormAttachment( 100, 0 );
    fdGeneralComp.bottom = new FormAttachment( 100, 0 );
    wGeneralComp.setLayoutData( fdGeneralComp );

    wGeneralComp.layout();
    wGeneralTab.setControl( wGeneralComp );
    props.setLook( wGeneralComp );

    // ///////////////////////////////////////////////////////////
    // / END OF GENERAL TAB
    // ///////////////////////////////////////////////////////////

    // ////////////////////////////////////
    // START OF ADVANCED TAB ///
    // ///////////////////////////////////

    wAdvancedTab = new CTabItem( wTabFolder, SWT.NONE );
    wAdvancedTab.setText( BaseMessages.getString( PKG, "JobDosToUnix.Tab.Advanced.Label" ) );

    FormLayout contentLayout = new FormLayout();
    contentLayout.marginWidth = 3;
    contentLayout.marginHeight = 3;

    wAdvancedComp = new Composite( wTabFolder, SWT.NONE );
    props.setLook( wAdvancedComp );
    wAdvancedComp.setLayout( contentLayout );

    // SuccessOngrouping?
    // ////////////////////////
    // START OF SUCCESS ON GROUP///
    // /
    wSuccessOn = new Group( wAdvancedComp, SWT.SHADOW_NONE );
    props.setLook( wSuccessOn );
    wSuccessOn.setText( BaseMessages.getString( PKG, "JobDosToUnix.SuccessOn.Group.Label" ) );

    FormLayout successongroupLayout = new FormLayout();
    successongroupLayout.marginWidth = 10;
    successongroupLayout.marginHeight = 10;

    wSuccessOn.setLayout( successongroupLayout );

    // Success Condition
    wlSuccessCondition = new Label( wSuccessOn, SWT.RIGHT );
    wlSuccessCondition.setText( BaseMessages.getString( PKG, "JobDosToUnix.SuccessCondition.Label" ) );
    props.setLook( wlSuccessCondition );
    fdlSuccessCondition = new FormData();
    fdlSuccessCondition.left = new FormAttachment( 0, 0 );
    fdlSuccessCondition.right = new FormAttachment( middle, 0 );
    fdlSuccessCondition.top = new FormAttachment( 0, margin );
    wlSuccessCondition.setLayoutData( fdlSuccessCondition );
    wSuccessCondition = new CCombo( wSuccessOn, SWT.SINGLE | SWT.READ_ONLY | SWT.BORDER );
    wSuccessCondition.add( BaseMessages.getString( PKG, "JobDosToUnix.SuccessWhenAllWorksFine.Label" ) );
    wSuccessCondition.add( BaseMessages.getString( PKG, "JobDosToUnix.SuccessWhenAtLeat.Label" ) );
    wSuccessCondition.add( BaseMessages.getString( PKG, "JobDosToUnix.SuccessWhenBadFormedLessThan.Label" ) );
    wSuccessCondition.select( 0 ); // +1: starts at -1

    props.setLook( wSuccessCondition );
    fdSuccessCondition = new FormData();
    fdSuccessCondition.left = new FormAttachment( middle, 0 );
    fdSuccessCondition.top = new FormAttachment( 0, margin );
    fdSuccessCondition.right = new FormAttachment( 100, 0 );
    wSuccessCondition.setLayoutData( fdSuccessCondition );
    wSuccessCondition.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {
        activeSuccessCondition();

      }
    } );

    // Success when number of errors less than
    wlNrErrorsLessThan = new Label( wSuccessOn, SWT.RIGHT );
    wlNrErrorsLessThan.setText( BaseMessages.getString( PKG, "JobDosToUnix.NrErrorFilesCountLessThan.Label" ) );
    props.setLook( wlNrErrorsLessThan );
    fdlNrErrorsLessThan = new FormData();
    fdlNrErrorsLessThan.left = new FormAttachment( 0, 0 );
    fdlNrErrorsLessThan.top = new FormAttachment( wSuccessCondition, margin );
    fdlNrErrorsLessThan.right = new FormAttachment( middle, -margin );
    wlNrErrorsLessThan.setLayoutData( fdlNrErrorsLessThan );

    wNrErrorsLessThan =
      new TextVar( workflowMeta, wSuccessOn, SWT.SINGLE | SWT.LEFT | SWT.BORDER, BaseMessages.getString(
        PKG, "JobDosToUnix.NrErrorFilesCountLessThan.Tooltip" ) );
    props.setLook( wNrErrorsLessThan );
    wNrErrorsLessThan.addModifyListener( lsMod );
    fdNrErrorsLessThan = new FormData();
    fdNrErrorsLessThan.left = new FormAttachment( middle, 0 );
    fdNrErrorsLessThan.top = new FormAttachment( wSuccessCondition, margin );
    fdNrErrorsLessThan.right = new FormAttachment( 100, -margin );
    wNrErrorsLessThan.setLayoutData( fdNrErrorsLessThan );

    fdSuccessOn = new FormData();
    fdSuccessOn.left = new FormAttachment( 0, margin );
    fdSuccessOn.top = new FormAttachment( 0, margin );
    fdSuccessOn.right = new FormAttachment( 100, -margin );
    wSuccessOn.setLayoutData( fdSuccessOn );
    // ///////////////////////////////////////////////////////////
    // / END OF Success ON GROUP
    // ///////////////////////////////////////////////////////////

    // fileresult grouping?
    // ////////////////////////
    // START OF LOGGING GROUP///
    // /
    wFileResult = new Group( wAdvancedComp, SWT.SHADOW_NONE );
    props.setLook( wFileResult );
    wFileResult.setText( BaseMessages.getString( PKG, "JobDosToUnix.FileResult.Group.Label" ) );

    FormLayout fileresultgroupLayout = new FormLayout();
    fileresultgroupLayout.marginWidth = 10;
    fileresultgroupLayout.marginHeight = 10;

    wFileResult.setLayout( fileresultgroupLayout );

    // Add Filenames to result filenames?
    wlAddFilenameToResult = new Label( wFileResult, SWT.RIGHT );
    wlAddFilenameToResult.setText( BaseMessages.getString( PKG, "JobDosToUnix.AddFilenameToResult.Label" ) );
    props.setLook( wlAddFilenameToResult );
    fdlAddFilenameToResult = new FormData();
    fdlAddFilenameToResult.left = new FormAttachment( 0, 0 );
    fdlAddFilenameToResult.right = new FormAttachment( middle, 0 );
    fdlAddFilenameToResult.top = new FormAttachment( 0, margin );
    wlAddFilenameToResult.setLayoutData( fdlAddFilenameToResult );
    wAddFilenameToResult = new CCombo( wFileResult, SWT.SINGLE | SWT.READ_ONLY | SWT.BORDER );
    wAddFilenameToResult.add( BaseMessages.getString( PKG, "JobDosToUnix.AddNoFilesToResult.Label" ) );
    wAddFilenameToResult.add( BaseMessages.getString( PKG, "JobDosToUnix.AddAllFilenamesToResult.Label" ) );
    wAddFilenameToResult.add( BaseMessages.getString( PKG, "JobDosToUnix.AddOnlyProcessedFilenames.Label" ) );
    wAddFilenameToResult.add( BaseMessages.getString( PKG, "JobDosToUnix.AddOnlyErrorFilenames.Label" ) );
    wAddFilenameToResult.select( 0 ); // +1: starts at -1

    props.setLook( wAddFilenameToResult );
    fdAddFilenameToResult = new FormData();
    fdAddFilenameToResult.left = new FormAttachment( middle, 0 );
    fdAddFilenameToResult.top = new FormAttachment( 0, margin );
    fdAddFilenameToResult.right = new FormAttachment( 100, 0 );
    wAddFilenameToResult.setLayoutData( fdAddFilenameToResult );
    wAddFilenameToResult.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {

      }
    } );

    fdFileResult = new FormData();
    fdFileResult.left = new FormAttachment( 0, margin );
    fdFileResult.top = new FormAttachment( wSuccessOn, margin );
    fdFileResult.right = new FormAttachment( 100, -margin );
    wFileResult.setLayoutData( fdFileResult );
    // ///////////////////////////////////////////////////////////
    // / END OF FilesResult GROUP
    // ///////////////////////////////////////////////////////////

    fdAdvancedComp = new FormData();
    fdAdvancedComp.left = new FormAttachment( 0, 0 );
    fdAdvancedComp.top = new FormAttachment( 0, 0 );
    fdAdvancedComp.right = new FormAttachment( 100, 0 );
    fdAdvancedComp.bottom = new FormAttachment( 100, 0 );
    wAdvancedComp.setLayoutData( wAdvancedComp );

    wAdvancedComp.layout();
    wAdvancedTab.setControl( wAdvancedComp );

    // ///////////////////////////////////////////////////////////
    // / END OF ADVANCED TAB
    // ///////////////////////////////////////////////////////////

    fdTabFolder = new FormData();
    fdTabFolder.left = new FormAttachment( 0, 0 );
    fdTabFolder.top = new FormAttachment( wName, margin );
    fdTabFolder.right = new FormAttachment( 100, 0 );
    fdTabFolder.bottom = new FormAttachment( 100, -50 );
    wTabFolder.setLayoutData( fdTabFolder );

    wOk = new Button( shell, SWT.PUSH );
    wOk.setText( BaseMessages.getString( PKG, "System.Button.OK" ) );
    wCancel = new Button( shell, SWT.PUSH );
    wCancel.setText( BaseMessages.getString( PKG, "System.Button.Cancel" ) );

    BaseTransformDialog.positionBottomButtons( shell, new Button[] { wOk, wCancel }, margin, wTabFolder );

    // Add listeners
    lsCancel = new Listener() {
      public void handleEvent( Event e ) {
        cancel();
      }
    };
    lsOk = new Listener() {
      public void handleEvent( Event e ) {
        ok();
      }
    };

    wCancel.addListener( SWT.Selection, lsCancel );
    wOk.addListener( SWT.Selection, lsOk );

    lsDef = new SelectionAdapter() {
      public void widgetDefaultSelected( SelectionEvent e ) {
        ok();
      }
    };

    wName.addSelectionListener( lsDef );
    wSourceFileFolder.addSelectionListener( lsDef );

    // Detect X or ALT-F4 or something that kills this window...
    shell.addShellListener( new ShellAdapter() {
      public void shellClosed( ShellEvent e ) {
        cancel();
      }
    } );

    getData();
    activeSuccessCondition();

    wTabFolder.setSelection( 0 );
    BaseTransformDialog.setSize( shell );

    shell.open();
    while ( !shell.isDisposed() ) {
      if ( !display.readAndDispatch() ) {
        display.sleep();
      }
    }
    return action;
  }

  private void activeSuccessCondition() {
    wlNrErrorsLessThan.setEnabled( wSuccessCondition.getSelectionIndex() != 0 );
    wNrErrorsLessThan.setEnabled( wSuccessCondition.getSelectionIndex() != 0 );
  }

  private void refreshArgFromPrevious() {

    wlFields.setEnabled( !wPrevious.getSelection() );
    wFields.setEnabled( !wPrevious.getSelection() );
    wbdSourceFileFolder.setEnabled( !wPrevious.getSelection() );
    wbeSourceFileFolder.setEnabled( !wPrevious.getSelection() );
    wbSourceFileFolder.setEnabled( !wPrevious.getSelection() );
    wbaSourceFileFolder.setEnabled( !wPrevious.getSelection() );
    wlSourceFileFolder.setEnabled( !wPrevious.getSelection() );
    wSourceFileFolder.setEnabled( !wPrevious.getSelection() );

    wlWildcard.setEnabled( !wPrevious.getSelection() );
    wWildcard.setEnabled( !wPrevious.getSelection() );
    wbSourceDirectory.setEnabled( !wPrevious.getSelection() );
  }

  public void dispose() {
    WindowProperty winprop = new WindowProperty( shell );
    props.setScreen( winprop );
    shell.dispose();
  }

  /**
   * Copy information from the meta-data input to the dialog fields.
   */
  public void getData() {
    if ( action.getName() != null ) {
      wName.setText( action.getName() );
    }

    if ( action.source_filefolder != null ) {
      for ( int i = 0; i < action.source_filefolder.length; i++ ) {
        TableItem ti = wFields.table.getItem( i );
        if ( action.source_filefolder[ i ] != null ) {
          ti.setText( 1, action.source_filefolder[ i ] );
        }

        if ( action.wildcard[ i ] != null ) {
          ti.setText( 2, action.wildcard[ i ] );
        }
        ti.setText( 3, ActionDosToUnix.getConversionTypeDesc( action.conversionTypes[ i ] ) );
      }
      wFields.setRowNums();
      wFields.optWidth( true );
    }
    wPrevious.setSelection( action.arg_from_previous );
    wIncludeSubfolders.setSelection( action.include_subfolders );

    if ( action.getNrErrorsLessThan() != null ) {
      wNrErrorsLessThan.setText( action.getNrErrorsLessThan() );
    } else {
      wNrErrorsLessThan.setText( "10" );
    }

    if ( action.getSuccessCondition() != null ) {
      if ( action.getSuccessCondition().equals( ActionDosToUnix.SUCCESS_IF_AT_LEAST_X_FILES_PROCESSED ) ) {
        wSuccessCondition.select( 1 );
      } else if ( action.getSuccessCondition().equals( ActionDosToUnix.SUCCESS_IF_ERROR_FILES_LESS ) ) {
        wSuccessCondition.select( 2 );
      } else {
        wSuccessCondition.select( 0 );
      }
    } else {
      wSuccessCondition.select( 0 );
    }

    if ( action.getResultFilenames() != null ) {
      if ( action.getResultFilenames().equals( ActionDosToUnix.ADD_PROCESSED_FILES_ONLY ) ) {
        wAddFilenameToResult.select( 1 );
      } else if ( action.getResultFilenames().equals( ActionDosToUnix.ADD_ERROR_FILES_ONLY ) ) {
        wAddFilenameToResult.select( 2 );
      } else {
        wAddFilenameToResult.select( 0 );
      }
    } else {
      wAddFilenameToResult.select( 0 );
    }

    wName.selectAll();
    wName.setFocus();
  }

  private void cancel() {
    action.setChanged( changed );
    action = null;
    dispose();
  }

  private void ok() {

    if ( Utils.isEmpty( wName.getText() ) ) {
      MessageBox mb = new MessageBox( shell, SWT.OK | SWT.ICON_ERROR );
      mb.setMessage( "Please give this action a name!" );
      mb.setText( "No name" );
      mb.open();
      return;
    }
    action.setName( wName.getText() );

    action.setIncludeSubfolders( wIncludeSubfolders.getSelection() );
    action.setArgFromPrevious( wPrevious.getSelection() );

    action.setNrErrorsLessThan( wNrErrorsLessThan.getText() );

    if ( wSuccessCondition.getSelectionIndex() == 1 ) {
      action.setSuccessCondition( ActionDosToUnix.SUCCESS_IF_AT_LEAST_X_FILES_PROCESSED );
    } else if ( wSuccessCondition.getSelectionIndex() == 2 ) {
      action.setSuccessCondition( ActionDosToUnix.SUCCESS_IF_ERROR_FILES_LESS );
    } else {
      action.setSuccessCondition( ActionDosToUnix.SUCCESS_IF_NO_ERRORS );
    }

    if ( wAddFilenameToResult.getSelectionIndex() == 1 ) {
      action.setResultFilenames( ActionDosToUnix.ADD_PROCESSED_FILES_ONLY );
    } else if ( wAddFilenameToResult.getSelectionIndex() == 2 ) {
      action.setResultFilenames( ActionDosToUnix.ADD_ERROR_FILES_ONLY );
    } else if ( wAddFilenameToResult.getSelectionIndex() == 3 ) {
      action.setResultFilenames( ActionDosToUnix.ADD_ALL_FILENAMES );
    } else {
      action.setResultFilenames( ActionDosToUnix.ADD_NOTHING );
    }

    int nritems = wFields.nrNonEmpty();
    int nr = 0;
    for ( int i = 0; i < nritems; i++ ) {
      String arg = wFields.getNonEmpty( i ).getText( 1 );
      if ( arg != null && arg.length() != 0 ) {
        nr++;
      }
    }
    action.source_filefolder = new String[ nr ];
    action.wildcard = new String[ nr ];
    action.conversionTypes = new int[ nr ];
    nr = 0;
    for ( int i = 0; i < nritems; i++ ) {
      String source = wFields.getNonEmpty( i ).getText( 1 );
      String wild = wFields.getNonEmpty( i ).getText( 2 );
      if ( source != null && source.length() != 0 ) {
        action.source_filefolder[ nr ] = source;
        action.wildcard[ nr ] = wild;
        action.conversionTypes[ nr ] =
          ActionDosToUnix.getConversionTypeByDesc( wFields.getNonEmpty( i ).getText( 3 ) );
        nr++;
      }
    }
    dispose();
  }

  public String toString() {
    return this.getClass().getName();
  }

  public boolean evaluates() {
    return true;
  }

  public boolean isUnconditional() {
    return false;
  }
}
