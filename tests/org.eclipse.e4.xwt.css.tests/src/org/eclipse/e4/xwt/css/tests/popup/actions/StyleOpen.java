package org.eclipse.e4.xwt.css.tests.popup.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.css.CSSStyle;
import org.eclipse.e4.xwt.ui.utils.DisplayUtil;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

public class StyleOpen implements IObjectActionDelegate {
	protected IFile file;

	/**
	 * Constructor for Action1.
	 */
	public StyleOpen() {
		super();
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		ClassLoader classLoader = Thread.currentThread()
				.getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(
					StyleOpen.class.getClassLoader());
			XWT.addDefaultStyle(new CSSStyle(StyleOpen.class
					.getResource("style.css")));
			if (file != null) {
				DisplayUtil.open(file);
			}
		} finally {
			Thread.currentThread().setContextClassLoader(classLoader);
		}
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		file = null;
		if (selection.isEmpty()) {
			return;
		}
		IStructuredSelection structuredSelection = (IStructuredSelection) selection;
		file = (IFile) structuredSelection.getFirstElement();
	}
}
