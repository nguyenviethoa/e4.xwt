package org.eclipse.e4.xwt.tests.controls.shell;

import java.net.URL;

import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.tests.XWTTestCase;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

public class ShellTests extends XWTTestCase {
	public void testShell_Trim() throws Exception {
		URL url = Shell_Trim.class.getResource(Shell_Trim.class
				.getSimpleName()
				+ IConstants.XWT_EXTENSION_SUFFIX);
		runTest(url, new Runnable() {
			public void run() {
				Shell shell = (Shell) root;
				assertTrue((shell.getStyle() & SWT.SHELL_TRIM) == SWT.SHELL_TRIM);
			}
		});
	}

	public void testDialog_Trim() throws Exception {
		URL url = Shell_Dialog_Trim.class.getResource(Shell_Dialog_Trim.class
				.getSimpleName()
				+ IConstants.XWT_EXTENSION_SUFFIX);
		runTest(url, new Runnable() {
			public void run() {
				Shell shell = (Shell) root;
				assertTrue((shell.getStyle() & SWT.DIALOG_TRIM) == SWT.DIALOG_TRIM);
			}
		});
	}
}
