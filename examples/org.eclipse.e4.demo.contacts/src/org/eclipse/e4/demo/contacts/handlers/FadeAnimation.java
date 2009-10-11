/*******************************************************************************
 * Copyright (c) 2006, 2008 Soyatec (http://www.soyatec.com) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Eric Moffatt (emoffatt@ca.ibm.com) - initial implementation sample
 *     Yves YANG (yves.yang@soyatec.com) - make it as reusable class
 *******************************************************************************/
package org.eclipse.e4.demo.contacts.handlers;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

/**
 * Simple fade transition animation.
 * 
 */
public class FadeAnimation {
	private Composite composite;
	private Shell animationShell;
	private Image backingStore;
	private int step = -1;

	public FadeAnimation(Composite composite) {
		this.composite = composite;
		if (composite != null && composite.isVisible()) {
			initAnimation();
		}
	}

	public int getStep() {
		return step;
	}

	public void setStep(int step) {
		this.step = step;
	}

	public void dispose() {
		if (animationShell != null && !animationShell.isDisposed()) {
			animationShell.getBackgroundImage().dispose();
			animationShell.dispose();
		}
	}
	
	private Shell initAnimation() {
		Shell baseShell = (composite instanceof Shell) ? (Shell) composite
				: composite.getShell();
		GC gc = new GC(baseShell.getDisplay());

		// Ensure that the platform supports advanced graphics
		gc.setAdvanced(true);
		if (!gc.getAdvanced()) {
			gc.dispose();
			return null;
		}

		Rectangle psRect = composite.getBounds();
		if (!(composite instanceof Shell)) {
			Point position = composite.toDisplay(psRect.x, psRect.y);
			psRect.x = position.x;
			psRect.y = position.y;
		}

		animationShell = new Shell(baseShell, SWT.NO_TRIM | SWT.ON_TOP);
		animationShell.setBounds(psRect);

		// Capture the background image
		backingStore = new Image(animationShell.getDisplay(), psRect);
		gc.setAdvanced(true);
		gc.copyArea(backingStore, psRect.x, psRect.y);
		gc.dispose();

		animationShell.setAlpha(255);
		animationShell.setBackgroundImage(backingStore);
		animationShell.setVisible(true);

		// Naive attempt to use fewer steps on large shells
		if (step == -1) {
			Rectangle ab = animationShell.getBounds();
			long area = ab.width * ab.height;

			step = 25;
			if (area < 500000)
				step = 10;
			else if (area < 1000000)
				step = 17;
		}

		return animationShell;
	}

	public void play() {
		if (animationShell == null) {
			return;
		}

		// 'Fade' the animation shell
		while (animationShell.getAlpha() > 0) {
			int newAlpha = (int) (animationShell.getAlpha() - step);
			if (newAlpha < 0)
				newAlpha = 0;
			animationShell.setAlpha(newAlpha);
			animationShell.update();

			// Special safety-check, some platforms lie about supporting
			// advanced graphics..;-)
			if (animationShell.getAlpha() == 255)
				break; // it's broken so this makes sense...;-)
		}
		dispose();
	}
}
