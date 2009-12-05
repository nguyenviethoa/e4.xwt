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
package org.eclipse.e4.xwt.tools.ui.designer.parts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.e4.xwt.tools.ui.designer.core.editor.EditDomain;
import org.eclipse.e4.xwt.tools.ui.designer.core.editor.IVisualRenderer;
import org.eclipse.e4.xwt.tools.ui.designer.core.parts.VisualEditPart;
import org.eclipse.e4.xwt.tools.ui.designer.core.parts.root.BindingLayer;
import org.eclipse.e4.xwt.tools.ui.designer.core.utils.DisplayUtil;
import org.eclipse.e4.xwt.tools.ui.designer.core.visuals.IVisualInfo;
import org.eclipse.e4.xwt.tools.ui.designer.databinding.BindingHelper;
import org.eclipse.e4.xwt.tools.ui.designer.databinding.BindingInfo;
import org.eclipse.e4.xwt.tools.ui.designer.databinding.DataContext;
import org.eclipse.e4.xwt.tools.ui.designer.databinding.IObservable;
import org.eclipse.e4.xwt.tools.ui.designer.editor.XWTVisualRenderer;
import org.eclipse.e4.xwt.tools.ui.designer.loader.XWTProxy;
import org.eclipse.e4.xwt.tools.ui.designer.policies.ComponentEditPolicy;
import org.eclipse.e4.xwt.tools.ui.designer.policies.GraphicalNodeEditPolicy;
import org.eclipse.e4.xwt.tools.ui.designer.visuals.WidgetVisualInfo;
import org.eclipse.e4.xwt.tools.ui.xaml.XamlNode;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.swt.widgets.Widget;

/**
 * @author jliu jin.liu@soyatec.com
 */
public class WidgetEditPart extends VisualEditPart implements NodeEditPart {
	private Widget widget;

	public WidgetEditPart(Widget widget, XamlNode model) {
		super(model);
		this.widget = widget;
	}

	/**
	 * @return the widget
	 */
	public Widget getWidget() {
		if (!validate()) {
			XWTVisualRenderer controlRender = (XWTVisualRenderer) EditDomain.getEditDomain(this).getViewerData(getViewer(), IVisualRenderer.KEY);
			if (controlRender != null) {
				Object component = controlRender.getVisual(getCastModel());
				if (component instanceof Widget) {
					widget = (Widget) component;
				}
			}
			if (validate()) {
				((WidgetVisualInfo) getVisualInfo()).setVisualable(widget);
			}
		}
		return widget;
	}

	public boolean validate() {
		return widget != null && !widget.isDisposed();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.soyatec.xaml.ve.editor.editparts.GraphicalNodeEditPart#createFigure()
	 */
	protected IFigure createFigure() {
		getWidget();
		return super.createFigure();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.soyatec.xaml.ve.editor.editparts.GraphicalNodeEditPart#createVisualInfo()
	 */
	protected IVisualInfo createVisualInfo() {
		return new WidgetVisualInfo(widget);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.soyatec.xaml.ve.editor.editparts.GraphicalNodeEditPart#getModelChildren()
	 */
	protected final List getModelChildren() {
		List modelChildren = new ArrayList(super.getModelChildren());
		Collection<?> externalModels = getExternalModels();
		if (externalModels != null && !externalModels.isEmpty()) {
			modelChildren.addAll(externalModels);
		}
		return modelChildren;
	}

	protected Collection<?> getExternalModels() {
		if (isRoot()) {
			DataContext dataContext = BindingHelper.getDataContext(getWidget());
			if (dataContext != null) {
				return Collections.singletonList(dataContext);
			}
		}
		return Collections.emptyList();
	}

	public boolean isRoot() {
		XWTVisualRenderer controlRender = (XWTVisualRenderer) EditDomain.getEditDomain(this).getViewerData(getViewer(), IVisualRenderer.KEY);
		return controlRender != null && controlRender.getRoot() == getWidget();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.soyatec.tools.designer.parts.GraphicalNodeEditPart#createEditPolicies()
	 */
	protected void createEditPolicies() {
		super.createEditPolicies();
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new ComponentEditPolicy());
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE, new GraphicalNodeEditPolicy());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.soyatec.xaml.ve.editor.editparts.GraphicalNodeEditPart#activate()
	 */
	public void activate() {
		refreshVisuals();
		super.activate();
	}

	/**
	 * @see org.eclipse.gef.editparts.AbstractEditPart#refreshVisuals()
	 */
	protected void refreshVisuals() {
		getWidget();
		final IVisualInfo visualInfo = getVisualInfo();
		if (visualInfo != null) {
			DisplayUtil.asyncExec(new Runnable() {
				public void run() {
					visualInfo.refreshImage();
				}
			});
		}
		super.refreshVisuals();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#addChildVisual(org.eclipse.gef.EditPart, int)
	 */
	protected void addChildVisual(EditPart childEditPart, int index) {
		if (childEditPart instanceof DataContextEditPart) {
			IFigure child = ((GraphicalEditPart) childEditPart).getFigure();
			IFigure layer = getLayer(BindingLayer.ID);
			if (child != null && layer != null) {
				layer.add(child);
			}
		} else {
			super.addChildVisual(childEditPart, index);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#removeChildVisual(org.eclipse.gef.EditPart)
	 */
	protected void removeChildVisual(EditPart childEditPart) {
		if (childEditPart instanceof DataContextEditPart) {
			IFigure child = ((GraphicalEditPart) childEditPart).getFigure();
			IFigure layer = getLayer(BindingLayer.ID);
			if (child != null && child.getParent() == layer) {
				layer.remove(child);
			}
		} else {
			super.removeChildVisual(childEditPart);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#getModelSourceConnections()
	 */
	protected List getModelSourceConnections() {
		XWTVisualRenderer controlRender = (XWTVisualRenderer) EditDomain.getEditDomain(this).getViewerData(getViewer(), IVisualRenderer.KEY);
		Object root = controlRender.getRoot();
		if (root != null && root instanceof Widget) {
			root = XWTProxy.getModel((Widget) root);
			EditPart rootEp = (EditPart) getViewer().getEditPartRegistry().get(root);
			XamlNode model = getCastModel();
			if (rootEp != null) {
				List<BindingInfo> sources = new ArrayList<BindingInfo>();
				List<BindingInfo> allBindings = BindingHelper.getBindings(rootEp);
				for (BindingInfo bindingInfo : allBindings) {
					IObservable observeModel = bindingInfo.getModel();
					if (model == observeModel.getSource()) {
						sources.add(bindingInfo);
					}
				}
				return sources;
			}
		}
		return super.getModelSourceConnections();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#getModelTargetConnections()
	 */
	protected List getModelTargetConnections() {
		List<BindingInfo> bindings = BindingHelper.getBindings(this);
		if (bindings != null) {
			return bindings;
		}
		return super.getModelTargetConnections();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.NodeEditPart#getSourceConnectionAnchor(org.eclipse.gef.ConnectionEditPart)
	 */
	public ConnectionAnchor getSourceConnectionAnchor(ConnectionEditPart connection) {
		return new ChopboxAnchor(getFigure());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.NodeEditPart#getSourceConnectionAnchor(org.eclipse.gef.Request)
	 */
	public ConnectionAnchor getSourceConnectionAnchor(Request request) {
		return new ChopboxAnchor(getFigure());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.NodeEditPart#getTargetConnectionAnchor(org.eclipse.gef.ConnectionEditPart)
	 */
	public ConnectionAnchor getTargetConnectionAnchor(ConnectionEditPart connection) {
		return new ChopboxAnchor(getFigure());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.NodeEditPart#getTargetConnectionAnchor(org.eclipse.gef.Request)
	 */
	public ConnectionAnchor getTargetConnectionAnchor(Request request) {
		return new ChopboxAnchor(getFigure());
	}

}