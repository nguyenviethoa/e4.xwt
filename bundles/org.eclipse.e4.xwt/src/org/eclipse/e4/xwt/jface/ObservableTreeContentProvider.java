package org.eclipse.e4.xwt.jface;

import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.masterdetail.IObservableFactory;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.internal.core.Core;
import org.eclipse.e4.xwt.internal.core.UpdateSourceTrigger;
import org.eclipse.jface.databinding.viewers.ObservableListTreeContentProvider;
import org.eclipse.jface.databinding.viewers.ObservableSetTreeContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class ObservableTreeContentProvider implements ITreeContentProvider {
	private String contentPath;
	private Object context;
	private Object value;
	private ITreeContentProvider delegate;

	public String getContentPath() {
		return contentPath;
	}

	public void setContentPath(String contentPath) {
		this.contentPath = contentPath;
	}
		
	public void dispose() {
		if (delegate == null) {
			return;
		}
		delegate.dispose();
	}
	
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {	
		if (newInput == null) {
			return;
		}
		updateContext(viewer, newInput);
		delegate.inputChanged(viewer, oldInput, newInput);
	}
	
	
	public Object[] getChildren(Object parentElement) {
		if (delegate == null) {
			return Core.EMPTY_ARRAY;
		}
		return delegate.getChildren(parentElement);
	}
	
	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}
	
	public boolean hasChildren(Object element) {
		if (delegate == null) {
			return false;
		}
		return delegate.hasChildren(element);
	}
		
	public Object getParent(Object element) {
		if (delegate == null) {
			return null;
		}
		return delegate.getParent(element);
	}
	
	public IObservableSet getKnownElements() {
		if (delegate == null) {
			return null;
		}
		if (delegate instanceof ObservableSetTreeContentProvider) {
			ObservableSetTreeContentProvider contentProvider = (ObservableSetTreeContentProvider) delegate;
			return contentProvider.getKnownElements();
		}
		if (delegate instanceof ObservableListTreeContentProvider) {
			ObservableListTreeContentProvider contentProvider = (ObservableListTreeContentProvider) delegate;
			return contentProvider.getKnownElements();
		}
		throw new IllegalStateException();
	}
		
	public void updateContext(Object context, Object value) {
		if (context == this.context && this.value == value) {
			return;
		}
		this.context = context;
		this.value = value;
		IObservableFactory observableFactory = XWT.observableFactory(context, getContentPath(), UpdateSourceTrigger.Default);
		IObservable observable = observableFactory.createObservable(value);
		
		if (observable instanceof IObservableList) {
			delegate = new ObservableListTreeContentProvider(observableFactory, null);			
		} 
		else if (observable instanceof IObservableSet) {
			delegate = new ObservableSetTreeContentProvider(observableFactory, null);						
		}
		else {
			throw new UnsupportedOperationException(observable.getClass().getName());
		}
	}
}