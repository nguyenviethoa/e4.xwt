package org.eclipse.e4.xwt.jface;

import org.eclipse.e4.xwt.metadata.IObjectInitializer;
import org.eclipse.jface.viewers.AbstractListViewer;
import org.eclipse.jface.viewers.AbstractTableViewer;
import org.eclipse.jface.viewers.ColumnViewer;

public class JFaceInitializer implements IObjectInitializer {

	public void initialize(Object object) {
		if (object instanceof AbstractListViewer) {
			AbstractListViewer viewer = (AbstractListViewer) object;
			viewer.setLabelProvider(new DefaultListViewerLabelProvider(viewer));
		}
		else if (object instanceof AbstractTableViewer) {
			ColumnViewer viewer = (ColumnViewer) object;
			viewer.setLabelProvider(new DefaultColumnViewerLabelProvider(viewer));			
		}
	}
}
