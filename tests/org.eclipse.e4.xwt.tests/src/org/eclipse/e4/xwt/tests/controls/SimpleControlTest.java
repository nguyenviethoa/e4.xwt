package org.eclipse.e4.xwt.tests.controls;

import java.net.URL;

import junit.framework.TestCase;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.utils.ResourceManager;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

public class SimpleControlTest extends TestCase {
	public void testControlLocation() throws Exception {
		final URL url = Control_Location.class
				.getResource(Control_Location.class.getSimpleName()
						+ IConstants.XWT_EXTENSION_SUFFIX);
		Realm.runWithDefault(SWTObservables.getRealm(PlatformUI.getWorkbench()
				.getDisplay()), new Runnable() {
			public void run() {
				try {
					Control control = XWT.load(url);
					assertNotNull(control);
					Shell shell = control.getShell();
					shell.addDisposeListener(new DisposeListener() {
						public void widgetDisposed(DisposeEvent e) {
							ResourceManager.resources.dispose();
						}
					});
					shell.open();
					Display display = shell.getDisplay();
					while (display.readAndDispatch())
						;
					assertFalse(control.isDisposed());
					shell.close();
					while (display.readAndDispatch())
						;
				} catch (Exception e) {
					fail(e.getMessage());
				}
			}
		});
	}
}
