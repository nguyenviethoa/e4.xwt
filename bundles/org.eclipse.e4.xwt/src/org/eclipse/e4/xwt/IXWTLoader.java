/*******************************************************************************
 * Copyright (c) 2006, 2008 Soyatec (http://www.soyatec.com) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Soyatec - initial API and implementation
 *******************************************************************************/
package org.eclipse.e4.xwt;

import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.e4.xwt.XWTLoader.ConverterService;
import org.eclipse.e4.xwt.core.TriggerBase;
import org.eclipse.e4.xwt.input.ICommand;
import org.eclipse.e4.xwt.internal.core.NameScope;
import org.eclipse.e4.xwt.metadata.IMetaclass;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;

/**
 * XWT loader interface
 * 
 * @author yyang (yves.yang@soyatec.com)
 */
public interface IXWTLoader {

	// Properties
	/**
	 * style of type int is used to create SWT element
	 */
	public static final String CONTAINER_PROPERTY = "XWT.Container";
	public static final String INIT_STYLE_PROPERTY = "XWT.Style";

	/**
	 * Used for editor/designer to pass design mode
	 */	
	public static final String DESIGN_MODE_ROPERTY = "XWT.DesignMode";
	
	/**
	 * Default styles to apply. The value should be a collection or Array of IStyle
	 * 
	 */
	public static final String DEFAULT_STYLES_PROPERTY = "XWT.DefaultStyles";
	/**
	 * Enabled or disabled the styles. By default, it is enabled
	 * 
	 */
	public static final String DISBALE_STYLES_PROPERTY = "XWT.DisabledStyles";
	/**
	 * The DataContext to setup in root element
	 * 
	 */
	public static final String DATACONTEXT_PROPERTY = "XWT.DataContext";

	/**
	 * The DataContext to setup in root element
	 * 
	 */
	public static final String CLASS_PROPERTY = "XWT.Class";

	/**
	 * Resources to associate to root element
	 * 
	 */
	public static final String RESOURCE_DICTIONARY_PROPERTY = "XWT.Resources";

	/**
	 * 
	 * @param nsmapace
	 * @param handler
	 */
	public void registerNamespaceHandler(String nsmapace, INamespaceHandler handler);

	/**
	 * 
	 * @param nsmapace
	 */
	public void unregisterNamespaceHandler(String nsmapace);

	/**
	 * 
	 * @param nsmapace
	 * @return
	 */
	public INamespaceHandler getNamespaceHandler(String nsmapace);

	/**
	 * 
	 * @return
	 */
	public Realm getRealm();

	/**
	 * Get the system logger.
	 * 
	 * @return
	 */
	public abstract ILogger getLogger();

	/**
	 * Change the system logger
	 * 
	 * @param logger
	 */
	public abstract void setLogger(ILogger log);

	/**
	 * This namespace service returns the associated or declared namespace for a given class.
	 * 
	 * @param javaclass
	 * @return
	 */
	public abstract String getNamespace(Class<?> javaclass);

	/**
	 * Get the name of the element, which is defined by <code>Name</code> or <code>x:Name</code>. Return <code>null</code>
	 * 
	 * @param object
	 * @return
	 */
	public abstract String getElementName(Object object);

	/**
	 * A NameContext is a manager of UI element's name in a scope. A name in a NameContext must be unique.
	 * 
	 * @param widget
	 * @return
	 */
	public abstract NameScope findNameContext(Widget widget);

	/**
	 * Find a named UI element.
	 * 
	 * @param context
	 *            the start point of research.
	 * @param name
	 * @return
	 */
	public abstract Object findElementByName(Widget context, String name);

	/**
	 * Get the DataContext of given element
	 * 
	 * @param context
	 * @return
	 */
	public abstract Object getDataContext(Widget element);

	/**
	 * Get the Triggers of given element
	 * 
	 * @param context
	 * @return
	 */
	public abstract TriggerBase[] getTriggers(Widget element);

	/**
	 * Change the DataContext of given element
	 * 
	 * @param context
	 * @return
	 */
	public abstract void setDataContext(Widget widget, Object dataContext);

	/**
	 * Change the Triggers of given element
	 * 
	 * @param context
	 * @return
	 */
	public abstract void setTriggers(Widget widget, TriggerBase[] triggers);

	/**
	 * Get the CLR (Common Language Runtime) object. If no CLR object is found in this element, the research will be propagated in it parent.
	 * 
	 * @param widget
	 * @return
	 */
	public abstract Object getCLR(Widget widget);

	/**
	 * Find the root shell
	 * 
	 * @param context
	 * @return
	 */
	public abstract Shell findShell(Widget context);

	/**
	 * Find the closet parent of type Composite
	 * 
	 * @param context
	 * @return
	 */
	public abstract Composite findCompositeParent(Widget context);

	/**
	 * Get the Metaclass of the given object
	 * 
	 * @param context
	 * @return
	 */
	public abstract IMetaclass getMetaclass(Object object);

	/**
	 * Load the file content. All widget will be created but they are showed. This method return the root element.
	 * 
	 */
	public abstract Control load(URL file) throws Exception;

	/**
	 * Load the file content with a DataContext. All widget will be created but they are showed. This method returns the root element. The DataContext will be associated to the root element.
	 */
	public abstract Control load(URL file, Object dataContext) throws Exception;

	/**
	 * Load the file content under a Composite. All widget will be created. This method returns the root element. The DataContext will be associated to the root element.
	 */
	public abstract Control load(Composite parent, URL file) throws Exception;

	/**
	 * Load the file content under a Composite with a DataContext. All widget will be created. This method returns the root element. The DataContext will be associated to the root element.
	 */
	public abstract Control load(Composite parent, URL file, Object dataContext) throws Exception;

	/**
	 * Load the file content under a Composite with a DataContext. All widget will be created. This method returns the root element. The DataContext will be associated to the root element.
	 */
	public abstract Control load(Composite parent, Class<?> viewType, Object dataContext) throws Exception;

	/**
	 * Load the file content under a Composite with a DataContext. All widget will be created. This method returns the root element. The DataContext will be associated to the root element.
	 */
	public abstract Control loadWithOptions(Class<?> viewType, Map<String, Object> options) throws Exception;

	/**
	 * Open and show the file content in a new Shell.
	 */
	public abstract void open(Class<?> type) throws Exception;

	/**
	 * Open and show the file content in a new Shell.
	 */
	public abstract void open(final URL url) throws Exception;

	/**
	 * load the content from a stream with a style, a DataContext and a ResourceDictionary. The root elements will be hold by Composite parent
	 */
	public abstract Control load(Composite parent, InputStream stream, URL file, Object dataContext) throws Exception;

	/**
	 * load the file content. The corresponding UI element is not yet created
	 */
	public abstract void open(URL url, Object dataContext) throws Exception;

	/**
	 * load the file content. The corresponding UI element is not yet created
	 */
	public abstract void open(Class<?> type, Object dataContext) throws Exception;

	/**
	 * load the file content. The corresponding UI element is not yet created
	 */
	public abstract void open(final URL url, final Map<String, Object> options) throws Exception;

	/**
	 * Data conversion service from String to a given type
	 * 
	 * @param type
	 * @param string
	 * @return
	 */
	public abstract Object convertFrom(IMetaclass type, String string);

	/**
	 * Data conversion service from String to a given type
	 * 
	 * @param targetType
	 * @param string
	 * @return
	 */
	public abstract Object convertFrom(Class<?> targetType, String string);

	public abstract Control loadWithOptions(URL url, Map<String, Object> options) throws Exception;

	/**
	 * 
	 * @param stream
	 * @param url
	 * @param options
	 * @return
	 * @throws Exception
	 */
	public abstract Control load(InputStream stream, URL url) throws Exception;

	/**
	 * Generic load method
	 * 
	 * @param stream
	 * @param url
	 * @param loadData
	 * @return
	 * @throws Exception
	 */
	public abstract Control loadWithOptions(InputStream stream, URL url, Map<String, Object> options) throws Exception;

	/**
	 * Metaclass services to return all registered Metaclasses.
	 * 
	 * @param stream
	 * @param url
	 * @param loadData
	 * @return
	 * @throws Exception
	 */
	public abstract IMetaclass[] getAllMetaclasses();

	/**
	 * Get the corresponding Metaclass
	 * 
	 * @param tagName
	 * @param ns
	 *            The namespace
	 * @return
	 */
	public abstract IMetaclass getMetaclass(String tagName, String ns);

	/**
	 * Register UI type
	 * 
	 * @param javaclass
	 */
	public abstract IMetaclass registerMetaclass(Class<?> type);

	/**
	 * Register Metaclass factory
	 * 
	 * @param javaclass
	 */
	public abstract void registerMetaclassFactory(IMetaclassFactory metaclassFactory);

	/**
	 * Register UI type
	 * 
	 * @param javaclass
	 */
	public abstract IMetaclass register(Class<?> javaclass, String namespace);

	public abstract ConverterService getConverterService();

	/**
	 * Find a Data converter
	 * 
	 * @param converter
	 * @param type
	 */
	public abstract IConverter findConvertor(Class<?> source, Class<?> target);

	/**
	 * Register a Data converter
	 * 
	 * @param converter
	 * @param type
	 */
	public abstract void registerConvertor(IConverter converter);

	/**
	 * Add a tracking option
	 * 
	 * @param tracking
	 */
	public abstract void addTracking(Tracking tracking);

	/**
	 * Test if the tracking on argument is enabled.
	 * 
	 * @param tracking
	 * @return
	 */
	public abstract boolean isTracking(Tracking tracking);

	/**
	 * Get all tracking options
	 * 
	 * @return
	 */
	public abstract Set<Tracking> getTrackings();

	/**
	 * Remove a tracking option.
	 * 
	 * @param tracking
	 */
	public abstract void removeTracking(Tracking tracking);

	/**
	 * Register a command to a name
	 * 
	 * @param name
	 * @param command
	 */
	public abstract void registerCommand(String name, ICommand command);

	/**
	 * Register a command to a name
	 * 
	 * @param name
	 * @param command
	 */
	public abstract void registerEventGroup(Class<?> type, IEventGroup eventGroup);

	/**
	 * Find a command by name
	 * 
	 * @param name
	 * @return
	 */
	public abstract ICommand getCommand(String name);

	/**
	 * Return all registered commands
	 * 
	 * @return
	 */
	public abstract Map<String, ICommand> getCommands();

	/**
	 * Unregister a command
	 * 
	 * @param name
	 */
	public abstract void unregisterCommand(String name);

	/**
	 * Add a default style
	 * 
	 * @param style
	 * @return
	 */
	public abstract void addDefaultStyle(IStyle style);

	/**
	 * Remove a default style
	 * 
	 * @param style
	 * @return
	 */
	public abstract void removeDefaultStyle(IStyle style);

	public abstract Collection<IStyle> getDefaultStyles();

	public abstract void addDataProviderFactory(IDataProviderFactory dataProviderFactory);

	public abstract void removeDataProviderFactory(IDataProviderFactory dataProvider);

	public abstract Collection<IDataProviderFactory> getDataProviderFactories();

	public abstract IDataProvider findDataProvider(Object dataContext);

	public abstract ILoadingContext findLoadingContext(Object container);

	public abstract ILoadingContext getLoadingContext(Composite object);

	public abstract ILoadingContext getLoadingContext();

	public abstract void setLoadingContext(ILoadingContext loadingContext);

}