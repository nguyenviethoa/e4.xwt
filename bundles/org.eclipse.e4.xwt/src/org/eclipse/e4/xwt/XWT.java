/*******************************************************************************
 * Copyright (c) 2006, 2008 Soyatec (http://www.soyatec.com) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Soyatec - initial API and implementation
 *     Anyware-tech - add multiple loaders
 *******************************************************************************/
package org.eclipse.e4.xwt;

import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.e4.xwt.input.ICommand;
import org.eclipse.e4.xwt.internal.core.NameScope;
import org.eclipse.e4.xwt.metadata.IMetaclass;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;

/**
 * XWT is the main class of the XWT framework. It provides most of the services in API.
 * 
 * @author yyang
 */
public class XWT {
	/**
	 * Get the system logger.
	 * 
	 * @returnF
	 */
	public static ILogger getLogger() {
		return XWTLoaderManager.getActive().getLogger();
	}

	/**
	 * 
	 * @param nsmapace
	 * @param handler
	 */
	public static void registerNamspaceHandler(String nsmapace, INamespaceHandler handler) {
		XWTLoaderManager.getActive().registerNamespaceHandler(nsmapace, handler);
	}

	/**
	 * 
	 * @param nsmapace
	 */
	public static void unregisterNamspaceHandler(String nsmapace) {
		XWTLoaderManager.getActive().unregisterNamespaceHandler(nsmapace);
	}

	/**
	 * 
	 * @param nsmapace
	 * @return
	 */
	public static INamespaceHandler getNamspaceHandler(String nsmapace) {
		return XWTLoaderManager.getActive().getNamespaceHandler(nsmapace);
	}

	/**
	 * Change the system logger
	 * 
	 * @param logger
	 */
	public static void setLogger(ILogger log) {
		XWTLoaderManager.getActive().setLogger(log);
	}

	/**
	 * Get the name of the element, which is defined by <code>Name</code> or <code>x:Name</code>. Return <code>null</code>
	 * 
	 * @param object
	 * @return
	 */
	public static String getElementName(Object object) {
		return XWTLoaderManager.getActive().getElementName(object);
	}

	/**
	 * A NameContext is a manager of UI element's name in a scope. A name in a NameContext must be unique.
	 * 
	 * @param widget
	 * @return
	 */
	public static NameScope findNameContext(Widget widget) {
		return XWTLoaderManager.getActive().findNameContext(widget);
	}

	/**
	 * Find a named UI element.
	 * 
	 * @param context
	 *            the start point of research.
	 * @param name
	 * @return
	 */
	public static Object findElementByName(Widget context, String name) {
		return XWTLoaderManager.getActive().findElementByName(context, name);
	}

	/**
	 * Get the DataContext of given element
	 * 
	 * @param context
	 * @return
	 */
	public static Object getDataContext(Widget element) {
		return XWTLoaderManager.getActive().getDataContext(element);
	}

	/**
	 * Get the CLR (Common Language Runtime) object. If no CLR object is found in this element, the research will be propagated in it parent.
	 * 
	 * @param widget
	 * @return
	 */
	public static Object getCLR(Widget widget) {
		return XWTLoaderManager.getActive().getCLR(widget);
	}

	/**
	 * Find the root shell
	 * 
	 * @param context
	 * @return
	 */
	public static Shell findShell(Widget context) {
		return XWTLoaderManager.getActive().findShell(context);
	}

	public static IMetaclass getMetaclass(Object object) {
		return XWTLoaderManager.getActive().getMetaclass(object);
	}

	/**
	 * Load the file content. All widget will be created but they are showed. This method return the root element.
	 * 
	 */
	static public synchronized Control load(URL file) throws Exception {
		return XWTLoaderManager.getActive().load(file);
	}

	/**
	 * Load the file content under a Composite. All widget will be created. This method returns the root element. The DataContext will be associated to the root element.
	 */
	static public synchronized Control load(Composite parent, URL file) throws Exception {
		return XWTLoaderManager.getActive().load(parent, file);
	}

	/**
	 * Load the file content under a Composite with a DataContext. All widget will be created. This method returns the root element. The DataContext will be associated to the root element.
	 */
	static public synchronized Control load(Composite parent, URL file, Object dataContext) throws Exception {
		return XWTLoaderManager.getActive().load(parent, file, dataContext);
	}

	/**
	 * Open and show the file content in a new Shell.
	 */
	static public synchronized void open(final URL url) throws Exception {
		XWTLoaderManager.getActive().open(url);
	}

	/**
	 * load the content from a stream with a style, a DataContext and a ResourceDictionary. The root elements will be hold by Composite parent
	 */
	static public synchronized Control load(Composite parent, InputStream stream, URL file, Object dataContext) throws Exception {
		return XWTLoaderManager.getActive().load(parent, stream, file, dataContext);
	}

	/**
	 * load the file content. The corresponding UI element is not yet created
	 */
	static public synchronized void open(URL url, Object dataContext) throws Exception {
		XWTLoaderManager.getActive().open(url, dataContext);
	}

	/**
	 * load the file content. The corresponding UI element is not yet created
	 */
	static public synchronized void open(Class<?> type, Object dataContext) throws Exception {
		open(type.getResource(type.getSimpleName() + IConstants.XWT_EXTENSION_SUFFIX), dataContext);
	}

	/**
	 * load the file content. The corresponding UI element is not yet created
	 */
	static public synchronized void open(URL url, Map<String, Object> options) throws Exception {
		XWTLoaderManager.getActive().open(url, options);
	}

	static public Object convertFrom(Class<?> targetType, String string) {
		return XWTLoaderManager.getActive().convertFrom(targetType, string);
	}

	static public synchronized Control loadWithOptions(URL url, Map<String, Object> options) throws Exception {
		return XWTLoaderManager.getActive().loadWithOptions(url, options);
	}

	/**
	 * 
	 * @param stream
	 * @param url
	 * @param options
	 * @return
	 * @throws Exception
	 */
	static public synchronized Control load(InputStream stream, URL url) throws Exception {
		return XWTLoaderManager.getActive().load(stream, url);
	}

	/**
	 * Metaclass services to return all registered Metaclasses.
	 * 
	 * @param stream
	 * @param url
	 * @param loadData
	 * @return
	 * @throws Exception
	 */
	static public IMetaclass[] getAllMetaclasses() {
		return XWTLoaderManager.getActive().getAllMetaclasses();
	}

	/**
	 * Get the corresponding Metaclass
	 * 
	 * @param tagName
	 * @param ns
	 *            The namespace
	 * @return
	 */
	static public IMetaclass getMetaclass(String tagName, String ns) {
		return XWTLoaderManager.getActive().getMetaclass(tagName, ns);
	}

	/**
	 * Register a Data converter
	 * 
	 * @param converter
	 * @param type
	 */
	public static void registerConvertor(IConverter converter) {
		XWTLoaderManager.getActive().registerConvertor(converter);
	}

	/**
	 * Register a command to a name
	 * 
	 * @param name
	 * @param command
	 */
	public static void registerCommand(String name, ICommand command) {
		XWTLoaderManager.getActive().registerCommand(name, command);
	}

	/**
	 * Add a default style
	 * 
	 * @param style
	 * @return
	 */
	public static void addDefaultStyle(IStyle style) {
		XWTLoaderManager.getActive().addDefaultStyle(style);
	}

	public static void addDataProviderFactory(IDataProviderFactory dataProviderFactory) {
		XWTLoaderManager.getActive().addDataProviderFactory(dataProviderFactory);
	}

	/**
	 * Register UI type
	 * 
	 * @param javaclass
	 */
	static public IMetaclass registerMetaclass(Class<?> type) {
		return XWTLoaderManager.getActive().registerMetaclass(type);
	}

	/**
	 * Find a Data converter
	 * 
	 * @param converter
	 * @param type
	 */
	static public IConverter findConvertor(Class<?> source, Class<?> target) {
		return XWTLoaderManager.getActive().findConvertor(source, target);
	}

	/**
	 * Switch current loading context
	 * 
	 */
	public static void setLoadingContext(ILoadingContext loadingContext) {
		XWTLoaderManager.getActive().setLoadingContext(loadingContext);
	}

	/**
	 * Return current loading context
	 * 
	 * @return ILoadingContext
	 */
	public static ILoadingContext getLoadingContext() {
		return XWTLoaderManager.getActive().getLoadingContext();
	}

	/**
	 * Add a tracking option
	 * 
	 * @param tracking
	 */
	static public void addTracking(Tracking tracking) {
		XWTLoaderManager.getActive().addTracking(tracking);
	}

	/**
	 * Test if the tracking on argument is enabled.
	 * 
	 * @param tracking
	 * @return
	 */
	static public boolean isTracking(Tracking tracking) {
		return XWTLoaderManager.getActive().isTracking(tracking);
	}

	/**
	 * Get all tracking options
	 * 
	 * @return
	 */
	static public Set<Tracking> getTrackings() {
		return XWTLoaderManager.getActive().getTrackings();
	}

	/**
	 * Find a command by name
	 * 
	 * @param name
	 * @return
	 */
	static public ICommand getCommand(String name) {
		return XWTLoaderManager.getActive().getCommand(name);
	}

	public static IDataProvider findDataProvider(Object dataContext) {
		return XWTLoaderManager.getActive().findDataProvider(dataContext);
	}

	static public Realm getRealm() {
		return XWTLoaderManager.getActive().getRealm();
	}

	static public boolean isXWTNamespace(String namespace) {
		return IConstants.XWT_X_NAMESPACE.equals(namespace) || IConstants.XWT_NAMESPACE.equals(namespace) || namespace.startsWith(IConstants.XAML_CLR_NAMESPACE_PROTO);
	}
}
