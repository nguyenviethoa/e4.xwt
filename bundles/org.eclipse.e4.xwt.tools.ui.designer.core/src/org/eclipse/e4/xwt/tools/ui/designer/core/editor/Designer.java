/*******************************************************************************
 * Copyright (c) 2006, 2009 Soyatec (http://www.soyatec.com) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Soyatec - initial API and implementation
 *******************************************************************************/
package org.eclipse.e4.xwt.tools.ui.designer.core.editor;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.e4.xwt.tools.ui.designer.core.component.CustomSashForm;
import org.eclipse.e4.xwt.tools.ui.designer.core.editor.IVisualRenderer.Result;
import org.eclipse.e4.xwt.tools.ui.designer.core.editor.builder.DesignerModelBuilder;
import org.eclipse.e4.xwt.tools.ui.designer.core.editor.builder.IModelBuilder;
import org.eclipse.e4.xwt.tools.ui.designer.core.editor.builder.ModelBuildListener;
import org.eclipse.e4.xwt.tools.ui.designer.core.editor.commandstack.CombinedCommandStack;
import org.eclipse.e4.xwt.tools.ui.designer.core.editor.dnd.DropContext;
import org.eclipse.e4.xwt.tools.ui.designer.core.editor.dnd.DropTargetAdapter;
import org.eclipse.e4.xwt.tools.ui.designer.core.editor.dnd.palette.PaletteDropAdapter;
import org.eclipse.e4.xwt.tools.ui.designer.core.editor.outline.ContentOutlinePage;
import org.eclipse.e4.xwt.tools.ui.designer.core.editor.outline.OutlineContentProvider;
import org.eclipse.e4.xwt.tools.ui.designer.core.editor.outline.OutlineLableProvider;
import org.eclipse.e4.xwt.tools.ui.designer.core.editor.text.StructuredTextHelper;
import org.eclipse.e4.xwt.tools.ui.designer.core.parts.root.DesignerRootEditPart;
import org.eclipse.e4.xwt.tools.ui.designer.core.problems.ConfigurableProblemHandler;
import org.eclipse.e4.xwt.tools.ui.designer.core.problems.ProblemHandler;
import org.eclipse.e4.xwt.tools.ui.designer.core.utils.DisplayUtil;
import org.eclipse.e4.xwt.tools.ui.palette.page.CustomPalettePage;
import org.eclipse.e4.xwt.tools.ui.xaml.XamlDocument;
import org.eclipse.e4.xwt.tools.ui.xaml.XamlNode;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.KeyHandler;
import org.eclipse.gef.KeyStroke;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.commands.CommandStackListener;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.gef.ui.parts.GraphicalViewerKeyHandler;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.gef.ui.parts.SelectionSynchronizer;
import org.eclipse.gef.ui.views.palette.PalettePage;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRewriteTarget;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.editors.text.TextFileDocumentProvider;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.part.MultiPageEditorSite;
import org.eclipse.ui.progress.UIJob;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.PropertySheetPage;
import org.eclipse.wst.sse.core.internal.text.BasicStructuredDocument;
import org.eclipse.wst.sse.core.internal.text.JobSafeStructuredDocument;
import org.eclipse.wst.sse.core.internal.undo.StructuredTextUndoManager;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.eclipse.wst.sse.ui.internal.StructuredTextViewer;
import org.eclipse.wst.xml.core.internal.provisional.contenttype.ContentTypeIdForXML;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;

/**
 * @author jliu (jin.liu@soyatec.com)
 */
@SuppressWarnings("restriction")
public abstract class Designer extends MultiPageEditorPart implements ISelectionChangedListener, CommandStackListener {

	public static final String DESIGNER_INPUT = "DESIGNER INPUT";
	public static final String DESIGNER_TEXT_EDITOR = "DESIGNER TEXT EDITOR";

	// UI editor.
	private CustomSashForm pageContainer;
	private CustomPalettePage palettePage;
	private IPropertySheetPage propertyPage;
	private ContentOutlinePage outlinePage;
	private boolean isDispatching = false;
	private ContextMenuProvider menuProvider;
	private ProblemHandler problemHandler;

	// GEF editor.
	private GraphicalViewer graphicalViewer;
	private EditDomain editDomain;
	private SelectionSynchronizer selectionSynchronizer;
	private ActionRegistry actionRegistry;
	private ActionGroup actionGroup;

	// Source editor.
	private StructuredTextEditor fTextEditor;
	private IPropertyListener fPropertyListener;
	private DropTargetAdapter dropListener;
	private boolean isProcessHighlighting = false;

	private XamlDocument xamlDocuemnt;
	private IVisualRenderer visualsRender;

	private CombinedCommandStack commandStack = new CombinedCommandStack();

	private IModelBuilder modelBuilder;
	private ModelBuildListener modelBuilderListener = new ModelBuildListener() {
		public void notifyChanged(Notification event) {
			performModelChanged(event);
		}
	};

	protected Display display;
	private LoadingFigureController loadingFigureController;

	private EditPartFactory editPartFactory;

	private KeyHandler fSharedKeyHandler;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.MultiPageEditorPart#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
	 */
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		super.init(site, input);
		site.setSelectionProvider(null);// clear multi-page selection provider.
		display = site.getShell().getDisplay();
		loadingFigureController = new LoadingFigureController();
		editDomain = new EditDomain(this);
		editDomain.setCommandStack(commandStack.getCommandStack4GEF());
		setPartName(input.getName());
		getEditDomain().setData(DESIGNER_INPUT, input);
		getCommandStack().getCommandStack4GEF().addCommandStackListener(this);
		createActions();
		configureActions();
	}

	protected void configureActions() {
		actionGroup.updateActions(ActionGroup.PROPERTY_GRP);
		actionGroup.updateActions(ActionGroup.STACK_GRP);

		if (graphicalViewer != null) {
			Iterator<?> actions = getActionRegistry().getActions();
			while (actions.hasNext()) {
				Object object = (Object) actions.next();
				if (object instanceof SelectionAction) {
					((SelectionAction) object).setSelectionProvider(graphicalViewer);
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.MultiPageEditorPart#setFocus()
	 */
	public void setFocus() {
		if (graphicalViewer != null && graphicalViewer.getControl() != null) {
			graphicalViewer.getControl().setFocus();
		}
		super.setFocus();
	}

	/**
	 * Perform model changed event.
	 * 
	 * @param event
	 */
	protected void performModelChanged(Notification event) {
		if (visualsRender != null) {
			if (event.isTouch()) {
				return;
			}
			Result result = getVisualsRender().refreshVisuals(event);
			if (result == null || !result.refreshed) {
				return;
			}
			Object notifier = result.visuals;
			// When the eventType is ADD, we need to refresh all children.
			if (notifier == null) {
				refreshUI(getGraphicalViewer().getRootEditPart());
			} else {
				EditPart editPart = getEditPart(notifier);
				if (editPart != null) {
					refreshUI(editPart.getParent());
				} else {
					while (notifier != null && notifier instanceof XamlNode) {
						Object parentNode = ((XamlNode) notifier).eContainer();
						while (getEditPart(parentNode) != null) {
							refreshUI(getEditPart(parentNode).getParent());
							break;
						}
						notifier = parentNode;
					}
				}
				// highlight changed one.
				if (editPart != null) {
					isProcessHighlighting = true;
					setViewerSelection(graphicalViewer, new StructuredSelection(editPart));
					isProcessHighlighting = false;
				}
			}
		}
	}

	public void refresh(EditPart editPart) {
		if (editPart == null) {
			return;
		}
		editPart.refresh();
		getOutlinePage().refresh(editPart);
		List children = editPart.getChildren();
		for (Object object : children) {
			refresh((EditPart) object);
		}
	}

	public void refreshUI(final EditPart editPart) {
		if (editPart != null) {
			DisplayUtil.asyncExec(new Runnable() {
				public void run() {
					refresh(editPart);
				}
			});
		}
	}

	public EditPart getEditPart(Object model) {
		return (EditPart) graphicalViewer.getEditPartRegistry().get(model);
	}

	private void setXamlDocument(XamlDocument document) {
		this.xamlDocuemnt = document;
	}

	public XamlDocument getXamlDocument() {
		return xamlDocuemnt;
	}

	/**
	 * Start loading models.
	 */
	private void tryToLoadModels() {
		loadingFigureController.showLoadingFigure(true);
		loadingFigureController.startListener(getGraphicalViewer());
		UIJob setupJob = new UIJob("Setup") {
			public IStatus runInUIThread(IProgressMonitor monitor) {
				if (modelBuilder.doLoad(Designer.this, monitor)) {
					setXamlDocument(modelBuilder.getXamlDocument());
				}
				if (!isDisposed()) {
					try {
						setupGraphicalViewer();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				return Status.OK_STATUS;
			}
		};
		setupJob.setDisplay(display);
		setupJob.setPriority(Job.SHORT);
		final IModelBuilder modelBuilder = getModelBuilder();
		if (getDocument() != null && modelBuilder != null) {
			setupJob.schedule();
		}
	}

	protected void runWithDialog(IRunnableWithProgress runnable) {
		try {
			ProgressMonitorDialog d = new ProgressMonitorDialog(getSite().getShell());
			d.run(true, false, runnable);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void setupGraphicalViewer() {
		IVisualRenderer vr = getVisualsRender();
		if (vr != null) {
			vr.createVisuals();
			getEditDomain().setViewerData(getGraphicalViewer(), IVisualRenderer.KEY, vr);
		}
		getGraphicalViewer().setContents(getDiagramEditPart());
		loadingFigureController.showLoadingFigure(false);
	}

	public IVisualRenderer getVisualsRender() {
		if (visualsRender == null) {
			visualsRender = createVisualsRender();
		}
		return visualsRender;
	}

	/**
	 * @return
	 */
	private EditPart getDiagramEditPart() {
		return getEditPartFactory().createEditPart(getGraphicalViewer().getRootEditPart(), getXamlDocument());
	}

	public GraphicalViewer getGraphicalViewer() {
		return graphicalViewer;
	}

	protected synchronized boolean isDisposed() {
		return editDomain == null;
	}

	public IFile getInputFile() {
		IEditorInput editorInput = getEditorInput();
		return (IFile) editorInput.getAdapter(IFile.class);
	}

	/**
	 * @return the modelBuilder
	 */
	public IModelBuilder getModelBuilder() {
		if (modelBuilder == null) {
			modelBuilder = createModelBuilder();
		}
		if (!modelBuilder.hasListener(modelBuilderListener)) {
			modelBuilder.addModelBuildListener(modelBuilderListener);
		}
		return modelBuilder;
	}

	protected IModelBuilder createModelBuilder() {
		return new DesignerModelBuilder();
	}

	/**
	 * Initialize and create actions.
	 */
	protected void createActions() {
		if (actionGroup == null) {
			actionGroup = new ActionGroup(this);
		}
		actionGroup.createActions();
	}

	/**
	 * Lazily creates and returns the action registry.
	 * 
	 * @return the action registry
	 */
	public ActionRegistry getActionRegistry() {
		if (actionRegistry == null)
			actionRegistry = new ActionRegistry();
		return actionRegistry;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.MultiPageEditorPart#createPageContainer(org.eclipse.swt.widgets.Composite)
	 */
	protected Composite createPageContainer(Composite parent) {
		ViewForm diagramPart = new ViewForm(parent, SWT.FLAT);

		ToolBar toolBar = createToolBar(diagramPart);
		if (toolBar != null && !toolBar.isDisposed()) {
			diagramPart.setTopLeft(toolBar);
		}

		pageContainer = new CustomSashForm(diagramPart, SWT.VERTICAL);
		pageContainer.setBackgroundMode(SWT.INHERIT_DEFAULT);
		createGraphicalViewer(pageContainer);
		diagramPart.setContent(pageContainer);
		return pageContainer;
	}

	/**
	 * Create Graphical Viewer for GEF Editor.
	 * 
	 * @param parent
	 */
	private void createGraphicalViewer(Composite parent) {
		graphicalViewer = new ScrollingGraphicalViewer();
		graphicalViewer.createControl(parent);
		graphicalViewer.getControl().setBackground(ColorConstants.listBackground);
		configureGraphicalViewer();
	}

	/**
	 * Configure GraphicalViewer
	 */
	protected void configureGraphicalViewer() {
		graphicalViewer.addSelectionChangedListener(this);
		editDomain.addViewer(graphicalViewer);

		getSite().setSelectionProvider(graphicalViewer);
		getSelectionSynchronizer().addViewer(graphicalViewer);

		graphicalViewer.setEditPartFactory(getEditPartFactory());
		ContextMenuProvider menuProvider = getContextMenuProvider();
		if (menuProvider != null) {
			graphicalViewer.setContextMenu(menuProvider);
		}

		DesignerRootEditPart rootEditPart = new DesignerRootEditPart();
		graphicalViewer.setRootEditPart(rootEditPart);

		graphicalViewer.setKeyHandler(new GraphicalViewerKeyHandler(graphicalViewer).setParent(getCommonKeyHandler()));

		Iterator<?> actions = getActionRegistry().getActions();
		while (actions.hasNext()) {
			Object object = (Object) actions.next();
			if (object instanceof SelectionAction) {
				((SelectionAction) object).setSelectionProvider(graphicalViewer);
			}
		}
	}
	
	/**
	 * Returns the KeyHandler with common bindings for both the Outline and
	 * Graphical Views. For example, delete is a common action.
	 */
	protected KeyHandler getCommonKeyHandler()
	{
		if (fSharedKeyHandler == null)
		{
			fSharedKeyHandler = new KeyHandler();
			fSharedKeyHandler.put(KeyStroke.getPressed(SWT.DEL, SWT.DEL, 0), getActionRegistry().getAction(ActionFactory.DELETE.getId()));
			fSharedKeyHandler.put(KeyStroke.getPressed(SWT.F2, 0), getActionRegistry().getAction(GEFActionConstants.DIRECT_EDIT));
		}
		return fSharedKeyHandler;
	}


	/**
	 * MenuProvider of the editor
	 * 
	 * @return
	 */
	public ContextMenuProvider getContextMenuProvider() {
		if (menuProvider == null) {
			menuProvider = createMenuProvider();
		}
		return menuProvider;
	}

	protected ContextMenuProvider createMenuProvider() {
		return new DesignerMenuProvider(this);
	}

	/**
	 * Returns the selection syncronizer object. The synchronizer can be used to sync the selection of 2 or more EditPartViewers.
	 * 
	 * @return
	 */
	public SelectionSynchronizer getSelectionSynchronizer() {
		if (selectionSynchronizer == null) {
			selectionSynchronizer = new SelectionSynchronizer();
		}
		return selectionSynchronizer;
	}

	/**
	 * Create a ToolBar for editor with global actions.
	 * 
	 * @param parent
	 * @return
	 */
	protected ToolBar createToolBar(Composite parent) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.MultiPageEditorPart#createPages()
	 */
	protected void createPages() {
		createSourcePage();
		createExternalPages();

		updateContainer();
	}

	/**
	 * Update TabFolder.
	 */
	private void updateContainer() {
		Composite container = getContainer();
		if (container == null || !(container instanceof CTabFolder)) {
			return;
		}
		CTabFolder tabFolder = (CTabFolder) container;
		tabFolder.setTabPosition(SWT.TOP);
		tabFolder.setSimple(false);
		ToolBar toolBar = new ToolBar(tabFolder, SWT.FLAT | SWT.WRAP | SWT.RIGHT);
		configureContainerToolBar(toolBar);
		tabFolder.setTopRight(toolBar);
		tabFolder.setTabHeight(Math.max(toolBar.computeSize(SWT.DEFAULT, SWT.DEFAULT).y, tabFolder.getTabHeight()));
	}

	/**
	 * ToolBar of container, it is really at the middle of the editor.
	 */
	protected void configureContainerToolBar(ToolBar toolBar) {

	}

	protected void createExternalPages() {

	}

	/**
	 * Create and add a StructuredTextEditor as a Source Page.
	 */
	private void createSourcePage() {
		// Subclass of StructuredTextEditor is not allowed.
		fTextEditor = new StructuredTextEditor();
		final StructuredTextUndoManager undoManager = new StructuredTextUndoManager(commandStack);
		TextFileDocumentProvider provider = new TextFileDocumentProvider() {
			public IDocument getDocument(Object element) {
				JobSafeStructuredDocument document = (JobSafeStructuredDocument) super.getDocument(element);
				if (document != null) {
					try {
						Field fUndoManager = BasicStructuredDocument.class.getDeclaredField("fUndoManager");
						fUndoManager.setAccessible(true);
						Object object = fUndoManager.get(document);
						if (object != null && object != undoManager) {
							fUndoManager.set(document, null);
							document.setUndoManager(undoManager);
						} else if (object == null) {
							document.setUndoManager(undoManager);
						}
					} catch (Exception e) {
					}
				}
				return document;
			}
		};
		fTextEditor.initializeDocumentProvider(provider);
		fTextEditor.setEditorPart(this);
		if (fPropertyListener == null) {
			fPropertyListener = new PropertyListener();
		}
		fTextEditor.addPropertyListener(fPropertyListener);
		try {
			int fSourcePageIndex = addPage(fTextEditor, getEditorInput());
			setPageText(fSourcePageIndex, "Source");
			firePropertyChange(PROP_TITLE);
			tryToLoadModels();
		} catch (PartInitException e) {
			e.printStackTrace();
		}
		fTextEditor.setAction(ITextEditorActionConstants.DELETE, null);
		if (pageContainer != null) {
			pageContainer.setWeights(new int[] { 1, 1 });
		}
		configureTextEditor();
	}

	protected IEditorSite createSite(IEditorPart editor) {
		IEditorSite site = null;
		if (editor == fTextEditor) {
			site = new MultiPageEditorSite(this, editor) {

				public String getId() {
					// sets this id so nested editor is considered xml source
					// page
					return ContentTypeIdForXML.ContentTypeID_XML + ".source"; //$NON-NLS-1$;
				}
			};
		} else {
			site = super.createSite(editor);
		}
		return site;
	}

	/**
	 * Configure Text Editor,
	 */
	protected void configureTextEditor() {
		if (getPalettePage() == null) {
			return;
		}
		if (dropListener == null) {
			dropListener = new DropTargetAdapter();
			DropContext dropContext = getDropContext();
			if (dropContext != null) {
				dropListener.addDropAdapter(new PaletteDropAdapter(this, dropContext));
			}
		}
		StyledText styledText = getTextWidget();
		if (styledText == null || dropListener == null) {
			return;
		}
		DropTarget dropTarget = (DropTarget) styledText.getData(DND.DROP_TARGET_KEY);
		if (dropTarget != null) {
			dropTarget.removeDropListener(dropListener);
		}

		styledText.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				StyledText styledText = (StyledText) e.widget;
				DropTarget dropTarget = (DropTarget) styledText.getData(DND.DROP_TARGET_KEY);
				if (dropTarget != null) {
					dropTarget.removeDropListener(dropListener);
				}
			}
		});
		dropTarget.dispose();

		dropTarget = new DropTarget(styledText, DND.DROP_MOVE | DND.DROP_COPY);
		dropTarget.setTransfer(new Transfer[] { getPalettePage().getPaletteTransfer() });
		dropTarget.addDropListener(dropListener);

		getProblemHandler().handle();
	}

	public void format() {
		StructuredTextViewer textViewer = getTextViewer();
		if (textViewer != null && textViewer.canDoOperation(StructuredTextViewer.FORMAT_DOCUMENT)) {
			textViewer.doOperation(StructuredTextViewer.FORMAT_DOCUMENT);
		} else if (getDocument() != null) {
			StructuredTextHelper.format(getDocument());
		}
	}

	public void formatWithCompound(Runnable runnable) {
		if (runnable == null) {
			format();
			return;
		}
		StructuredTextViewer textViewer = getTextViewer();
		if (textViewer == null) {
			runnable.run();
			return;
		}
		IRewriteTarget rewriteTarget = textViewer.getRewriteTarget();
		if (rewriteTarget != null) {
			rewriteTarget.beginCompoundChange();
		}
		runnable.run();
		format();
		if (rewriteTarget != null) {
			rewriteTarget.endCompoundChange();
		}
	}

	public StyledText getTextWidget() {
		if (getTextViewer() != null) {
			return getTextViewer().getTextWidget();
		}
		return null;
	}

	public StructuredTextViewer getTextViewer() {
		if (fTextEditor != null) {
			return fTextEditor.getTextViewer();
		}
		return null;
	}

	public IDocument getDocument() {
		IDocument document = null;
		if (fTextEditor != null) {
			IDocumentProvider documentProvider = fTextEditor.getDocumentProvider();
			IEditorInput editorInput = fTextEditor.getEditorInput();
			if (documentProvider == null || editorInput == null) {
				return null;
			}
			document = documentProvider.getDocument(editorInput);
		}
		return document;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void doSave(IProgressMonitor monitor) {
		fTextEditor.doSave(monitor);
		if (modelBuilder != null) {
			modelBuilder.doSave(monitor);
		}
		// getCommandStack().flush();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorPart#doSaveAs()
	 */
	public void doSaveAs() {
		fTextEditor.doSaveAs();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.MultiPageEditorPart#dispose()
	 */
	public void dispose() {
		getModelBuilder().removeModelBuildListener(modelBuilderListener);
		getCommandStack().getCommandStack4GEF().removeCommandStackListener(this);
		getCommandStack().flush();
		getGraphicalViewer().removeSelectionChangedListener(this);
		if (modelBuilder != null) {
			modelBuilder.dispose();
		}
		if (visualsRender != null) {
			visualsRender.dispose();
		}
		fSharedKeyHandler = null;
		getActionRegistry().dispose();
		getProblemHandler().clear();
		super.dispose();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.MultiPageEditorPart#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class adapter) {
		if (ActionRegistry.class == adapter) {
			return getActionRegistry();
		} else if (org.eclipse.gef.EditDomain.class.isAssignableFrom(adapter)) {
			return getEditDomain();
		} else if (PalettePage.class.isAssignableFrom(adapter)) {
			return getPalettePage();
		} else if (CommandStack.class == adapter) {
			return getCommandStack().getCommandStack4GEF();
		} else if (GraphicalViewer.class == adapter) {
			return getGraphicalViewer();
		} else if (adapter == IPropertySheetPage.class) {
			return getPropertySheetPage();
		} else if (adapter == IContentOutlinePage.class) {
			return getOutlinePage();
		} else if (adapter == ProblemHandler.class) {
			return getProblemHandler();
		} else if (adapter == EditPart.class && getGraphicalViewer() != null) {
			return getGraphicalViewer().getRootEditPart();
		} else if (adapter == IFigure.class && getGraphicalViewer() != null) {
			return ((GraphicalEditPart) getGraphicalViewer().getRootEditPart()).getFigure();
		} else if (adapter == IProject.class) {
			return getProject();
		} else if (adapter == IFile.class) {
			return getInputFile();
		}
		return super.getAdapter(adapter);
	}

	public ProblemHandler getProblemHandler() {
		if (problemHandler == null) {
			problemHandler = new ConfigurableProblemHandler(this);
		}
		return problemHandler;
	}

	/**
	 * @return
	 */
	private ContentOutlinePage getOutlinePage() {
		if (outlinePage == null) {
			outlinePage = createOutlinePage();
		}
		if (graphicalViewer != null) {
			outlinePage.setSelection(graphicalViewer.getSelection());
		}
		return outlinePage;
	}

	protected ContentOutlinePage createOutlinePage() {
		OutlineContentProvider contentProvider = new OutlineContentProvider();
		OutlineLableProvider lableProvider = new OutlineLableProvider();
		return new ContentOutlinePage(this, contentProvider, lableProvider);
	}

	public IPropertySheetPage getPropertySheetPage() {
		if (propertyPage == null || propertyPage.getControl() == null || propertyPage.getControl().isDisposed()) {
			propertyPage = createPropertyPage();
		}
		// if (graphicalViewer != null) {
		// propertyPage.selectionChanged(this, graphicalViewer.getSelection());
		// }
		return propertyPage;
	}

	protected IPropertySheetPage createPropertyPage() {
		return new PropertySheetPage();
	}

	/**
	 * @return
	 */
	private CustomPalettePage getPalettePage() {
		if (palettePage == null) {
			palettePage = createPalettePage();
		}
		return palettePage;
	}

	public CombinedCommandStack getCommandStack() {
		return commandStack;
	}

	/**
	 * @return the editDomain
	 */
	public EditDomain getEditDomain() {
		return editDomain;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
	 */
	public boolean isSaveAsAllowed() {
		return fTextEditor.isSaveAsAllowed();
	}

	protected EditPart convert(EditPartViewer viewer, EditPart part) {
		Object temp = viewer.getEditPartRegistry().get(part.getModel());
		EditPart newPart = null;
		if (temp != null) {
			newPart = (EditPart) temp;
		}
		return newPart;
	}

	private void setViewerSelection(GraphicalViewer viewer, ISelection selection) {
		ArrayList<EditPart> result = new ArrayList<EditPart>();
		Iterator<EditPart> iter = ((IStructuredSelection) selection).iterator();
		while (iter.hasNext()) {
			EditPart part = convert(viewer, iter.next());
			if (part != null) {
				result.add(part);
			}
		}
		if (!result.isEmpty()) {
			viewer.setSelection(new StructuredSelection(result));
			if (result.size() > 0) {
				viewer.reveal((EditPart) result.get(result.size() - 1));
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
	 */
	public final void selectionChanged(SelectionChangedEvent event) {
		Object source = event.getSource();
		if (outlinePage != null && source == outlinePage.getTreeViewer()) {
			if (isDispatching) {
				return;
			}
			isDispatching = true;
			setViewerSelection(graphicalViewer, event.getSelection());
			isDispatching = false;
		} else {
			// If not the active editor, ignore selection changed.
			IEditorPart activeEditor = getSite().getPage().getActiveEditor();
			if (Designer.this.equals(activeEditor)) {
				performSelectionChanged(event);
			}
		}
	}

	/**
	 * Perform Selection event.
	 * 
	 * @param event
	 */
	protected void performSelectionChanged(SelectionChangedEvent event) {
		IStructuredSelection selection = (IStructuredSelection) event.getSelection();
		if (selection.isEmpty()) {
			return;
		}
		
		if (!isProcessHighlighting) {
			selectEditPartsInCodeEditor(selection);
		}
		
		// if (propertyPage != null) {
		// propertyPage.selectionChanged(this, selection);
		// }
		if (actionGroup != null) {
			actionGroup.updateActions(ActionGroup.SELECTION_GRP);
		}
		if (outlinePage != null && !isDispatching) {
			outlinePage.setSelection(selection);
		}
		ActionRegistry actionRegistry = getActionRegistry();
		Iterator<?> actions = actionRegistry.getActions();
		while (actions.hasNext()) {
			Object object = (Object) actions.next();
			if (object instanceof SelectionAction) {
				((SelectionAction) object).update();
			}
		}
	}

	public void selectEditPartsInCodeEditor(IStructuredSelection selection) {
		// 1. highlight TextEditor.
		StyledText styledText = getTextWidget();
		if (Display.getDefault().getFocusControl() == styledText) {
			return;
		}
		String content = styledText.getText();
		int startOffset = -1;
		int endOffset = 0;
		
		Object[] array = selection.toArray();
		for (Object object : array) {
			if (object instanceof EditPart) {
				EditPart editPart = (EditPart) object;
				Object model = editPart.getModel();
				if (model instanceof XamlNode) {
					XamlNode node = (XamlNode) model;
					IDOMNode textNode = getModelBuilder().getTextNode(node);
					if (textNode != null) {
						int nodeStartOffset = textNode.getStartOffset();
						int nodeEndOffset = textNode.getEndOffset();
						if (startOffset == -1) {
							startOffset = nodeStartOffset;
							endOffset = nodeEndOffset;
						}
						else {
							if (nodeStartOffset > startOffset) {
								if (nodeStartOffset < endOffset) {
									continue;
								}
								String segment = content.substring(endOffset, nodeStartOffset).trim();
								if (segment.length() == 0 ) {
									endOffset = nodeEndOffset;
								}
								else {
									startOffset = 0;
									endOffset = 0;
									break;
								}
							}
							else {
								if (nodeEndOffset > startOffset) {
									continue;
								}
								String segment = content.substring(nodeEndOffset, startOffset).trim();
								if (segment.length() == 0 ) {
									startOffset = nodeStartOffset;
								}
								else {
									startOffset = 0;
									endOffset = 0;
									break;
								}
								
							}
						}
					}
				}
			}
		}
		if (startOffset == -1) {
			startOffset = 0;
		}
		int length = endOffset - startOffset;

		getTextViewer().setRangeIndication(startOffset, length, false);
		StructuredTextEditor textEditor = getTextEditor();
		textEditor.selectAndReveal(startOffset, length);
	}

	public void gotoDefinition(XamlNode node) {
		if (isProcessHighlighting) {
			return;
		}
		IDOMNode textNode = getModelBuilder().getTextNode(node);
		if (textNode != null) {
			int startOffset = textNode.getStartOffset();
			int length = textNode.getEndOffset() - startOffset;
			StructuredTextEditor textEditor = getTextEditor();
			getTextViewer().setRangeIndication(startOffset, length, false);
			textEditor.selectAndReveal(startOffset, length);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.commands.CommandStackListener#commandStackChanged(java.util.EventObject)
	 */
	public void commandStackChanged(EventObject event) {
		if (actionGroup != null) {
			actionGroup.updateActions(ActionGroup.STACK_GRP);
		}
	}

	public StructuredTextEditor getTextEditor() {
		return fTextEditor;
	}

	/*
	 * This method is just to make firePropertyChanged accessible from some (anonomous) inner classes.
	 */
	void _firePropertyChange(int property) {
		super.firePropertyChange(property);
	}

	public IProject getProject() {
		return getInputFile().getProject();
	}

	/**
	 * EditPart Factory.
	 * 
	 * @return
	 */
	public EditPartFactory getEditPartFactory() {
		if (editPartFactory == null) {
			editPartFactory = createEditPartFactory();
		}
		return editPartFactory;
	}

	protected abstract EditPartFactory createEditPartFactory();

	protected abstract IVisualRenderer createVisualsRender();

	/**
	 * DropContext.
	 * 
	 * @return
	 */
	protected abstract DropContext getDropContext();

	/**
	 * Create PalettePage.
	 */
	protected abstract CustomPalettePage createPalettePage();

	/**
	 * Internal IPropertyListener
	 */
	class PropertyListener implements IPropertyListener {
		public void propertyChanged(Object source, int propId) {
			switch (propId) {
			// had to implement input changed "listener" so that
			// StructuredTextEditor could tell it containing editor that
			// the input has change, when a 'resource moved' event is
			// found.
			case IEditorPart.PROP_INPUT: {
			}
			case IEditorPart.PROP_DIRTY: {
				if (source == getTextEditor()) {
					if (getTextEditor().getEditorInput() != getEditorInput()) {
						setInput(getTextEditor().getEditorInput());
						/*
						 * title should always change when input changes. create runnable for following post call
						 */
						Runnable runnable = new Runnable() {
							public void run() {
								_firePropertyChange(IWorkbenchPart.PROP_TITLE);
							}
						};
						/*
						 * Update is just to post things on the display queue (thread). We have to do this to get the dirty property to get updated after other things on the queue are executed.
						 */
						((Control) getTextEditor().getAdapter(Control.class)).getDisplay().asyncExec(runnable);
					}
				}
				break;
			}
			case IWorkbenchPart.PROP_TITLE: {
				// update the input if the title is changed
				if (source == getTextEditor()) {
					if (getTextEditor().getEditorInput() != getEditorInput()) {
						setInput(getTextEditor().getEditorInput());
					}
				}
				break;
			}
			default: {
				// propagate changes. Is this needed? Answer: Yes.
				if (source == getTextEditor()) {
					_firePropertyChange(propId);
				}
				break;
			}
			}

		}
	}

}