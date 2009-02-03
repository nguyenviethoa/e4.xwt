package org.eclipse.e4.xwt.ui.views;

import java.net.URL;

import org.eclipse.e4.xwt.ILoadingContext;

public interface IContentProvider {
	/**
	 * 
	 * @return
	 */
	ILoadingContext getLoadingContext();

	/**
	 * Content URL
	 * 
	 * @return
	 */
	URL getContentURL();
}
