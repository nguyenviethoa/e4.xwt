package org.eclipse.e4.xwt.jface;

import java.util.Set;

import org.eclipse.core.databinding.observable.ChangeEvent;
import org.eclipse.core.databinding.observable.IChangeListener;
import org.eclipse.core.databinding.observable.map.IMapChangeListener;
import org.eclipse.core.databinding.observable.map.MapChangeEvent;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.jface.viewers.AbstractListViewer;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;

/**
 * A label provider based on one or more observable maps that track attributes
 * that this label provider uses for display. Clients may customize by
 * subclassing and overriding {@link #getColumnText(Object, int)},
 * {@link #getColumnImage(Object, int)}, for tables or trees with columns, or by
 * implementing additional mixin interfaces for colors, fonts etc.
 * 
 * @since 1.1
 * 
 */
public class ObservableMapLabelProvider extends LabelProvider implements
		ILabelProvider, ITableLabelProvider {

	private final XWTObservableWrapper[] attributeMaps;

	private IMapChangeListener mapChangeListener = new IMapChangeListener() {
		public void handleMapChange(MapChangeEvent event) {
			Set affectedElements = event.diff.getChangedKeys();
			LabelProviderChangedEvent newEvent = new LabelProviderChangedEvent(
					ObservableMapLabelProvider.this, affectedElements.toArray());
			fireLabelProviderChanged(newEvent);
		}
	};
	
	static class ViewerResher implements IChangeListener {
		protected Viewer viewer;
		public ViewerResher(Viewer viewer) {
			this.viewer = viewer;
		}
		
		public void handleChange(ChangeEvent event) {
			try {
				if (viewer instanceof ColumnViewer) {
					ColumnViewer columnViewer = (ColumnViewer) viewer;
					if (!columnViewer.isBusy()) {
						columnViewer.refresh();
					}
				}
				else if (viewer instanceof AbstractListViewer) {
					AbstractListViewer listViewer = (AbstractListViewer) viewer;
					listViewer.refresh();
				}
			} catch (Exception e) {
			}
		}
	}; 

	/**
	 * @param attributeMaps
	 */
	public ObservableMapLabelProvider(Viewer columnViewer, IObservableSet domain,
			String[] propertyNames) {		
		attributeMaps = new XWTObservableWrapper[propertyNames.length];
		
		for (int i = 0; i < attributeMaps.length; i++) {
			attributeMaps[i] = new XWTObservableWrapper(domain, columnViewer, propertyNames[i]);
			attributeMaps[i].addMapChangeListener(mapChangeListener);
		}
		domain.addChangeListener(new ViewerResher(columnViewer));
	}

	public void dispose() {
		for (int i = 0; i < attributeMaps.length; i++) {
			attributeMaps[i].removeMapChangeListener(mapChangeListener);
		}
		super.dispose();
	}

	public Image getImage(Object element) {
		return null;
	}

	public String getText(Object element) {
		return getColumnText(element, 0);
	}

	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		if (columnIndex < attributeMaps.length) {
			Object result = attributeMaps[columnIndex].get(element);
			return result == null ? "" : result.toString(); //$NON-NLS-1$
		}
		return null;
	}
}

