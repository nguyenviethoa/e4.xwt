package org.eclipse.e4.demo.contacts.handlers;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

public class FadeAnimation {
	protected Composite composite;
	protected Shell animationShell;
	protected Image backingStore;
	
	public FadeAnimation(Composite composite) {
		this.composite = composite;
		if (composite != null && composite.isVisible()) {
			initAnimation();
		}
	}
	
	private Shell initAnimation() {
		Shell baseShell = (composite instanceof Shell) ? (Shell) composite : composite.getShell();
		GC gc = new GC(baseShell. getDisplay());
		
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
//		long startTime = System.currentTimeMillis();
		backingStore = new Image(animationShell.getDisplay(), psRect);
		gc.setAdvanced(true);
		gc.copyArea(backingStore, psRect.x, psRect.y);
		gc.dispose();
		
		animationShell.setAlpha(255);
		animationShell.setBackgroundImage(backingStore);
		animationShell.setVisible(true);
//		System.out.println("Capture time = " + (System.currentTimeMillis()- startTime)); //$NON-NLS-1$
		
		return animationShell;
	}
	
	public void play() {
		if (animationShell == null) {
			return;
		}
		
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
		backingStore.dispose();
	}
}
