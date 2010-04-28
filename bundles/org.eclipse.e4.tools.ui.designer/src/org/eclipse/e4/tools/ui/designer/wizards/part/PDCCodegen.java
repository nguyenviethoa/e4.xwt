/*******************************************************************************
 * Copyright (c) 2006, 2010 Soyatec (http://www.soyatec.com) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Soyatec - initial API and implementation
 *******************************************************************************/
package org.eclipse.e4.tools.ui.designer.wizards.part;

import java.beans.PropertyDescriptor;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.Path;
import org.eclipse.e4.tools.ui.designer.utils.ASTHelper;
import org.eclipse.e4.tools.ui.designer.utils.XWTCodegen;
import org.eclipse.e4.tools.ui.designer.utils.XWTCodegen.PrintResult;
import org.eclipse.e4.xwt.IConstants;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Layout;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class PDCCodegen {

	public static final String EVENT_HANDLER_KEY = "eventhandlerkey";

	public static final String JAVA_LANG_PREFIX = "j";
	public static final String EMBED_XWT_PREFIX = "p";

	private PartDataContext dataContext;

	public PDCCodegen(PartDataContext dataContext) {
		this.dataContext = dataContext;
		Assert.isNotNull(dataContext);
		Assert.isNotNull(dataContext.getType());
	}

	/**
	 * An new parameter <code>dataContextProperties</code> is added for creating
	 * <code>dataContext</code> components, if this dataContextProperties is not
	 * null, all items of dataContext which contains dataContextProperties will
	 * be generated.
	 * 
	 * For a EMF dataContext, all properties are EStructuredFeatures; and for a
	 * common bean dataContext, all properties are name of each
	 * PropertyDescriptor.
	 */
	public void createFile(IType host, IFile file) {
		String hostClassName = host.getFullyQualifiedName();
		ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
		PrintStream printStream = new PrintStream(arrayOutputStream);
		Map<String, String> namespaces = new HashMap<String, String>();

		List<String> imports = new ArrayList<String>();
		imports.add("java.lang");
		namespaces.put("java.lang", JAVA_LANG_PREFIX);

		IPackageFragment pfrag = host.getPackageFragment();
		if (pfrag.getElementName() != null) {
			imports.add(pfrag.getElementName());
			namespaces.put(pfrag.getElementName(), EMBED_XWT_PREFIX);
		}
		PrintResult result = printRoot(printStream, Composite.class,
				hostClassName, imports.toArray(new String[0]), namespaces,
				GridLayoutFactory.swtDefaults().numColumns(2).create());
		if (result.hasExternalContents()) {
			Map<String, Object> externalContents = result.getExternalContents();
			Set<Entry<String, Object>> entrySet = externalContents.entrySet();
			for (Entry<String, Object> entry : entrySet) {
				Object object = entry.getValue();
				if (object == null) {
					continue;
				}
				String typeName = null;
				if (object instanceof EClass) {
					typeName = ((EClass) object).getName();
				} else if (object instanceof Class<?>) {
					typeName = ((Class<?>) object).getSimpleName();
				} else {
					typeName = object.getClass().getSimpleName();
				}
				// FIXME: check type name first, if exist? rename : go on.
				IType embededType = ASTHelper.createType(pfrag, typeName, null,
						Composite.class);
				if (embededType == null) {
					continue;
				}
				IContainer parent = file.getParent();
				IFile embedFile = parent.getFile(new Path(typeName
						+ IConstants.XWT_EXTENSION_SUFFIX));
				createFile(embededType, embedFile, new PartDataContext(object));
			}
		}
		try {
			byte[] content = arrayOutputStream.toByteArray();
			printStream.close();
			arrayOutputStream.close();
			ByteArrayInputStream input = new ByteArrayInputStream(content);
			if (file.exists()) {
				file.setContents(input, IResource.FORCE
						| IResource.KEEP_HISTORY, null);
			} else {
				file.create(input, true, null);
			}
		} catch (Exception e) {
		}
	}
	public PrintResult printRoot(PrintStream printStream, Class<?> rootType,
			String clr, String[] imports, Map<String, String> namespaces,
			Layout layout) {
		PrintResult result = new PrintResult();
		String content = null;
		if (dataContext != null) {
			try {
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				PrintStream ps = new PrintStream(out);
				result.merge(printDataContext(ps, "\t"));
				byte[] bytes = out.toByteArray();
				content = new String(bytes);
				ps.close();
				out.close();
			} catch (IOException e) {
			}
		}
		result.merge(XWTCodegen.printRoot(printStream, rootType, clr, imports,
				namespaces, content, layout));
		return result;
	}

	private PrintResult printDataContext(PrintStream printStream,
			String prefixOffset) {
		Object type = dataContext.getType();
		if (type instanceof EObject) {
			return printDataContextEMF(printStream, prefixOffset);
		} else {
			return printDataContextBean(printStream, prefixOffset);
		}
	}
	public PrintResult printDataContextBean(PrintStream printStream,
			String prefixOffset) {
		if (printStream == null) {
			return PrintResult.FAILED;
		}
		PrintResult result = new PrintResult();

		for (Object property : dataContext.getProperties()) {
			if (!(property instanceof PropertyDescriptor)) {
				continue;
			}
			PropertyDescriptor pd = (PropertyDescriptor) property;
			String name = pd.getName();
			if (name == null || "class".equals(name)) {
				continue;
			}
			Class<?> propertyType = pd.getPropertyType();
			if (propertyType == null) {
				continue;
			} else if (propertyType.isArray()
					|| Collection.class.isAssignableFrom(propertyType)) {
				try {
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					PrintStream ps = new PrintStream(out);
					printTableBean(ps, pd, null, prefixOffset + "\t");
					byte[] bytes = out.toByteArray();
					String content = new String(bytes);
					XWTCodegen.printSurroundWithGroup(printStream, name,
							content, new FillLayout(), GridDataFactory
									.fillDefaults().grab(true, false)
									.span(2, 1).create(), prefixOffset);
					ps.close();
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else if (propertyType.isPrimitive()
					|| propertyType == String.class
					|| propertyType == URL.class) {
				result.merge(XWTCodegen.printLabelText(printStream, pd
						.getDisplayName(), pd.getName(), prefixOffset));
			} else if (Date.class == propertyType) {
				result.merge(XWTCodegen.printLabelDateTime(printStream, pd
						.getDisplayName(), pd.getName(), prefixOffset));
			} else if (propertyType.isEnum()) {
				List<String> items = new ArrayList<String>();
				for (Object object : propertyType.getEnumConstants()) {
					items.add(object.toString());
				}
				result.merge(XWTCodegen.printLabelCombo(printStream, name,
						name, items.toArray(new String[0]), prefixOffset));
			} else {
				String elementType = propertyType.getSimpleName();
				String content = "<p:" + elementType
						+ " DataContext=\"{Binding path=" + name + "}\"/>";
				result.merge(XWTCodegen.printSurroundWithGroup(printStream,
						name, content, new FillLayout(), GridDataFactory
								.fillDefaults().create(), prefixOffset));
				result.getExternalContents().put(elementType, propertyType);
			}
		}
		return result;
	}

	public PrintResult printDataContextEMF(PrintStream printStream,
			String prefixOffset) {
		if (printStream == null) {
			return PrintResult.FAILED;
		}
		PrintResult result = new PrintResult();
		for (Object property : dataContext.getProperties()) {
			if (!(property instanceof EStructuralFeature)) {
				continue;
			}
			EStructuralFeature feature = (EStructuralFeature) property;
			String name = feature.getName();
			if (name == null) {
				continue;
			}
			EClassifier propertyType = feature.getEType();
			if (feature.isMany()) {
				try {
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					PrintStream ps = new PrintStream(out);
					printTableEMF(ps, feature, null, prefixOffset);
					byte[] bytes = out.toByteArray();
					String content = new String(bytes);
					XWTCodegen.printSurroundWithGroup(printStream, feature
							.getName(), content, new FillLayout(),
							GridDataFactory.fillDefaults().grab(true, false)
									.span(2, 1).create(), prefixOffset);
					ps.close();
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else if (propertyType instanceof EEnum) {
				List<String> items = new ArrayList<String>();
				for (EEnumLiteral object : ((EEnum) propertyType)
						.getELiterals()) {
					items.add(object.toString());
				}
				result.merge(XWTCodegen.printLabelCombo(printStream, name,
						name, items.toArray(new String[0]), prefixOffset));
			} else if (propertyType instanceof EDataType) {
				result.merge(XWTCodegen.printLabelText(printStream, name, name,
						prefixOffset));
			} else {
				String elementType = propertyType.getName();
				String content = "<p:" + elementType
						+ " DataContext=\"{Binding path=" + name + "}\"/>";
				result.merge(XWTCodegen.printSurroundWithGroup(printStream,
						name, content, new FillLayout(), GridDataFactory
								.fillDefaults().span(2, 1).create(),
						prefixOffset));
				result.getExternalContents().put(elementType, propertyType);
			}
		}
		return result;
	}

	public PrintResult printTableBean(PrintStream printStream,
			PropertyDescriptor propertyDescriptor, Object layoutData,
			String prefixOffset) {
		if (propertyDescriptor == null
				|| propertyDescriptor.getPropertyType() == null) {
			return PrintResult.FAILED;
		}
		Class<?> propertyType = propertyDescriptor.getPropertyType();
		if (!propertyType.isArray()
				&& !Collection.class.isAssignableFrom(propertyType)) {
			return PrintResult.FAILED;
		}
		if (prefixOffset == null) {
			prefixOffset = "";
		}
		String nextOffset = prefixOffset + "\t";
		String name = propertyDescriptor.getName();
		printStream
				.println(prefixOffset
						+ "<TableViewer Name=\""
						+ name
						+ "TableViewer\" x:style=\"SWT.FULL_SELECTION\" input=\"{Binding Path="
						+ name + "}\">");
		Class<?> eType = propertyType.getComponentType();
		if (eType != null) {
			printStream.println(nextOffset + "<TableViewer.columns>");
			List<PropertyDescriptor> properties = PDC.collectProperties(eType,
					false, false);
			for (PropertyDescriptor pd : properties) {
				XWTCodegen.printTableColumn(printStream, pd.getDisplayName(),
						pd.getName(), 100, nextOffset + "\t");
			}
			printStream.println(nextOffset + "</TableViewer.columns>");
		}

		String eventHandler = getEventHandler(propertyDescriptor);
		printStream.println(nextOffset
				+ "<TableViewer.table HeaderVisible=\"true\" " + eventHandler
				+ "/>");
		if (layoutData != null) {
			printStream
					.println(nextOffset + "<TableViewer.control.layoutData>");
			XWTCodegen.printlayoutData(printStream, nextOffset, layoutData);
			printStream.println(nextOffset
					+ "</TableViewer.control.layoutData>");
		}

		printStream.println(prefixOffset + "</TableViewer>");
		return PrintResult.OK;
	}
	public PrintResult printTableEMF(PrintStream printStream,
			EStructuralFeature feature, Object layoutData, String prefixOffset) {
		if (feature == null || !feature.isMany()) {
			return PrintResult.FAILED;
		}
		if (prefixOffset == null) {
			prefixOffset = "";
		}
		String nextOffset = prefixOffset + "\t";
		String name = feature.getName();
		printStream
				.println(prefixOffset
						+ "<TableViewer Name=\""
						+ name
						+ "TableViewer\" x:style=\"SWT.FULL_SELECTION\" input=\"{Binding Path="
						+ name + "}\">");
		EClassifier eType = feature.getEType();
		if (eType != null && eType instanceof EClass) {
			printStream.println(nextOffset + "<TableViewer.columns>");
			EList<EStructuralFeature> features = ((EClass) eType)
					.getEStructuralFeatures();
			for (EStructuralFeature sf : features) {
				EClassifier columnFeatureType = sf.getEType();
				if (!(columnFeatureType instanceof EDataType)) {
					continue;
				}
				XWTCodegen.printTableColumn(printStream, sf.getName(), sf
						.getName(), 100, nextOffset + "\t");
			}
			printStream.println(nextOffset + "</TableViewer.columns>");
		}

		String eventHandler = getEventHandler(feature);
		printStream.println(nextOffset
				+ "<TableViewer.table HeaderVisible=\"true\" " + eventHandler
				+ "/>");
		if (layoutData != null) {
			printStream
					.println(nextOffset + "<TableViewer.control.layoutData>");
			XWTCodegen.printlayoutData(printStream, nextOffset, layoutData);
			printStream.println(nextOffset
					+ "</TableViewer.control.layoutData>");
		}

		printStream.println(prefixOffset + "</TableViewer>");
		return PrintResult.OK;
	}
	private String getEventHandler(Object property) {
		List<String> handlers = dataContext.getEventHandlers(property);
		if (handlers != null) {
			StringBuffer buffer = new StringBuffer();
			for (int i = 0; i < handlers.size(); i++) {
				buffer.append(handlers.get(i));
				if (i < handlers.size() - 1) {
					buffer.append(" ");
				}
			}
			return buffer.toString();
		}
		return "";
	}

	public static void createFile(IType type, IFile file,
			PartDataContext dataContext) {
		PDCCodegen codegen = new PDCCodegen(dataContext);
		codegen.createFile(type, file);
	}
}
