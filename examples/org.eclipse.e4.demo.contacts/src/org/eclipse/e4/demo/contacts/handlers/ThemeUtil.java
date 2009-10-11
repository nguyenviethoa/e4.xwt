/*******************************************************************************
 * Copyright (c) 2009 Siemens AG and others.
 * 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 * 
 * Contributors:
 *     Kai Tödter - initial implementation
 ******************************************************************************/

package org.eclipse.e4.demo.contacts.handlers;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.e4.ui.css.core.engine.CSSEngine;
import org.eclipse.e4.workbench.ui.IWorkbench;
import org.eclipse.e4.workbench.ui.internal.Workbench;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class ThemeUtil {

	private static Shell initAnimation(Shell baseShell) {
		GC gc = new GC(baseShell.getDisplay());
		
		// Ensure that the platform supports advanced graphics
		gc.setAdvanced(true);
		if (!gc.getAdvanced()) {
			gc.dispose();
			return null;
		}
		
		Rectangle psRect = baseShell.getBounds();
		Shell animationShell = new Shell(baseShell, SWT.NO_TRIM | SWT.ON_TOP);			
		animationShell.setBounds(psRect);

		// Capture the background image
//		long startTime = System.currentTimeMillis();
		Image backingStore = new Image(animationShell.getDisplay(), psRect);
		gc.setAdvanced(true);
		gc.copyArea(backingStore, psRect.x, psRect.y);
		gc.dispose();
		
		animationShell.setAlpha(255);
		animationShell.setBackgroundImage(backingStore);
		animationShell.setVisible(true);
//		System.out.println("Capture time = " + (System.currentTimeMillis()- startTime)); //$NON-NLS-1$
		
		return animationShell;
	}
	
	private static void runAnimation(Shell animationShell) {
		if (animationShell == null)
			return;
		
		Rectangle ab = animationShell.getBounds();
		long area = ab.width * ab.height;
		
		// Naive attempt to use fewer steps on large shells
		int stepSize = 25;
		if (area < 500000) stepSize = 10;
		else if (area < 1000000) stepSize = 17;

		
		// 'Fade' the animation shell 
		while (animationShell.getAlpha() > 0) {
			int newAlpha = (int) (animationShell.getAlpha() - stepSize);
			if (newAlpha < 0) newAlpha = 0;
			animationShell.setAlpha(newAlpha);
			animationShell.update();
			
			// Special safety-check, some platforms lie about supporting advanced graphics..;-)
			if (animationShell.getAlpha() == 255)
				break;  // it's broken so this makes sense...;-)
		}
		animationShell.getBackgroundImage().dispose();
		animationShell.dispose();
	}
	
	public static void switchTheme(IWorkbench workbench, final String css) {
		if (workbench instanceof Workbench) {
			Workbench wb = (Workbench) workbench;
			final Shell shell = (Shell) wb.getWindow();
			Display display = shell.getDisplay();
			final CSSEngine engine = (CSSEngine) display
					.getData("org.eclipse.e4.ui.css.core.engine");

			display.syncExec(new Runnable() {
				public void run() {
					try {
						URL url = FileLocator.resolve(new URL(
								"platform:/plugin/org.eclipse.e4.demo.contacts/css/"
										+ css));

						Shell animationShell = initAnimation(shell);
						
						InputStream stream = url.openStream();
						InputStreamReader streamReader = new InputStreamReader(
								stream);
						engine.reset();
						engine.parseStyleSheet(streamReader);
						stream.close();
						streamReader.close();
						engine.applyStyles(shell, true, false);
						shell.layout(true, true);
						
						runAnimation(animationShell);
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
		}
	}
}
