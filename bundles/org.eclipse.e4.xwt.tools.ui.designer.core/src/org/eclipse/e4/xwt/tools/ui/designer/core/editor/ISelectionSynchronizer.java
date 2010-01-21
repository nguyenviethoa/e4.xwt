package org.eclipse.e4.xwt.tools.ui.designer.core.editor;

import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;

public interface ISelectionSynchronizer {

	/**
	 * Adds a viewer to the set of synchronized viewers
	 * 
	 * @param viewer
	 *            the viewer
	 */
	public abstract void addViewer(ISelectionProvider viewer);

	/**
	 * Removes the viewer from the set of synchronized viewers
	 * 
	 * @param viewer
	 *            the viewer to remove
	 */
	public abstract void removeViewer(ISelectionProvider viewer);

	/**
	 * Receives notification from one viewer, and maps selection to all other
	 * members.
	 * 
	 * @param event
	 *            the selection event
	 */
	public abstract void selectionChanged(SelectionChangedEvent event);

	/**
	 * Enables or disabled synchronization between viewers.
	 * 
	 * @since 3.1
	 * @param value
	 *            <code>true</code> if synchronization should occur
	 */
	public abstract void setEnabled(boolean value);

}