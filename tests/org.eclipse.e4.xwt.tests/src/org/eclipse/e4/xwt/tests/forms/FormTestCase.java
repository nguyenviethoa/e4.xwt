package org.eclipse.e4.xwt.tests.forms;

import java.net.URL;
import java.util.Map;

import org.eclipse.e4.xwt.forms.XWTForms;
import org.eclipse.e4.xwt.tests.XWTTestCase;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class FormTestCase extends XWTTestCase {
	protected void runTest(final URL url, Map<String, Object> options,
			Runnable... checkActions) {
		ClassLoader classLoader = Thread.currentThread()
				.getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(
					this.getClass().getClassLoader());
			root = XWTForms.loadWithOptions(url, options);
			assertNotNull(root);
			Shell shell = root.getShell();
			shell.open();
			Display display = shell.getDisplay();

			for (Runnable runnable : checkActions) {
				while (display.readAndDispatch())
					;
				display.syncExec(runnable);
				while (display.readAndDispatch())
					;
			}
			assertFalse(root.isDisposed());
			shell.close();
			while (display.readAndDispatch())
				;
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		} finally {
			Thread.currentThread().setContextClassLoader(classLoader);
		}
	}
}