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
package org.eclipse.e4.xwt.tools.ui.designer.core.parts;

import java.util.List;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.e4.xwt.tools.ui.designer.core.figures.ContentPaneFigure;
import org.eclipse.e4.xwt.tools.ui.designer.core.figures.ImageFigure;
import org.eclipse.e4.xwt.tools.ui.designer.core.figures.OutlineBorder;
import org.eclipse.e4.xwt.tools.ui.designer.core.images.IImageListener;
import org.eclipse.e4.xwt.tools.ui.designer.core.images.ImageFigureController;
import org.eclipse.e4.xwt.tools.ui.designer.core.policies.DefaultComponentEditPolicy;
import org.eclipse.e4.xwt.tools.ui.designer.core.visuals.IVisualInfo;
import org.eclipse.e4.xwt.tools.ui.xaml.XamlNode;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.swt.graphics.Image;

/**
 * @author jliu jin.liu@soyatec.com
 */
public abstract class VisualEditPart extends AbstractGraphicalEditPart {

	private boolean useBorder = false;
	private boolean transparent;
	private ImageFigureController imageFigureController;
	private IImageListener imageListener;
	private IVisualInfo visualInfo;

	public VisualEditPart(XamlNode model) {
		setModel(model);
	}

	/**
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	protected IFigure createFigure() {
		ContentPaneFigure figure = new ContentPaneFigure();
		ImageFigure imageFigure = new ImageFigure();
		if (isUseBorder())
			imageFigure.setBorder(new OutlineBorder(150, ColorConstants.lightGray, null, Graphics.LINE_SOLID));
		imageFigure.setOpaque(!isTransparent());
		if (!isTransparent()) {
			imageFigureController = new ImageFigureController();
			imageFigureController.setImageFigure(imageFigure);
		}
		figure.setContentPane(imageFigure);
		return figure;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#getContentPane()
	 */
	public IFigure getContentPane() {
		return getContentPaneFigure().getContentPane();
	}

	protected ContentPaneFigure getContentPaneFigure() {
		return ((ContentPaneFigure) getFigure());
	}

	/**
	 * @see org.eclipse.gef.editparts.AbstractEditPart#getModelChildren()
	 */
	protected List getModelChildren() {
		if (getCastModel() != null) {
			return getCastModel().getChildNodes();
		}
		return super.getModelChildren();
	}

	/**
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	protected void createEditPolicies() {
		// Default component role allows delete and basic behavior of a component within a parent edit part that contains it
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new DefaultComponentEditPolicy());

	}

	/**
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#activate()
	 */
	public void activate() {
		if (!isTransparent() && imageFigureController != null) {
			imageFigureController.setImageNotifier(getVisualInfo());
		}
		super.activate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.editparts.AbstractEditPart#refreshVisuals()
	 */
	protected void refreshVisuals() {
		final IVisualInfo visualInfo = getVisualInfo();
		if (visualInfo == null) {
			return;
		}
		if (imageListener == null) {
			imageListener = new IImageListener() {
				public void imageChanged(Image image) {
					IFigure figure = getFigure();
					if (image != null && figure != null && figure.getParent() != null) {
						Rectangle r = new Rectangle(visualInfo.getBounds());
						figure.setBounds(r);
						setLayoutConstraint(VisualEditPart.this, figure, r);
					}
				}
			};
		}
		visualInfo.removeImageListener(imageListener);
		visualInfo.addImageListener(imageListener);
		IFigure figure = getFigure();
		if (figure != null && figure.getParent() != null) {
			Rectangle r = new Rectangle(visualInfo.getBounds());
			figure.setBounds(r);
			setLayoutConstraint(VisualEditPart.this, figure, r);
		}
	}

	/**
	 * @return the visualComponent
	 */
	public IVisualInfo getVisualInfo() {
		if (visualInfo == null) {
			visualInfo = createVisualInfo();
		}
		return visualInfo;
	}

	/**
	 * @return
	 */
	protected abstract IVisualInfo createVisualInfo();

	/**
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#deactivate()
	 */
	public void deactivate() {
		super.deactivate();
		if (imageFigureController != null)
			imageFigureController.deactivate();
	}

	/**
	 * @param useBorder
	 *            the useBorder to set
	 */
	public void setUseBorder(boolean useBorder) {
		this.useBorder = useBorder;
	}

	/**
	 * @return the useBorder
	 */
	public boolean isUseBorder() {
		return useBorder;
	}

	/**
	 * @param transparent
	 *            the transparent to set
	 */
	public void setTransparent(boolean transparent) {
		this.transparent = transparent;
	}

	/**
	 * @return the transparent
	 */
	public boolean isTransparent() {
		return transparent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.editparts.AbstractEditPart#getModel()
	 */
	public XamlNode getCastModel() {
		return (XamlNode) super.getModel();
	}
}
