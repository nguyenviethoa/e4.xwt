/*******************************************************************************
 * Copyright (c) 2006, 2009 Soyatec (http://www.soyatec.com) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Soyatec - initial API and implementation
 *******************************************************************************/
package org.eclipse.e4.tools.ui.designer.utils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.e4.xwt.IConstants;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAnnotation;
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
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Text;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class XWTCodegen {

	public static final String EMF_FEATURE_MASTER_KEY = "emfobjectfeaturemasterkey";

	public static final String JAVA_LANG_PREFIX = "j";
	public static final String EMBED_XWT_PREFIX = "p";

	public static class PrintResult {
		public static PrintResult OK = new PrintResult();
		public static PrintResult FAILED = new PrintResult() {
			public IStatus getStatus() {
				return new Status(IStatus.ERROR, "XWT CODE GENERATE",
						"Print Failed");
			};
		};
		private IStatus status = Status.OK_STATUS;
		private Map<String, Object> externalContents;

		public void setStatus(IStatus status) {
			this.status = status;
		}
		public IStatus getStatus() {
			return status;
		}

		public boolean isOK() {
			return status != null && status.isOK();
		}

		public boolean hasExternalContents() {
			return !getExternalContents().isEmpty();
		}

		public Map<String, Object> getExternalContents() {
			if (externalContents == null) {
				externalContents = new HashMap<String, Object>();
			}
			return externalContents;
		}
		public void merge(PrintResult newResult) {
			if (newResult != null && newResult.getStatus().isOK()) {
				getExternalContents().putAll(newResult.getExternalContents());
			}
		}
	}
	public static void createFile(IType host, IFile file, Object dataContext) {
		createFile(host, file, dataContext, null);
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
	public static void createFile(IType host, IFile file, Object dataContext,
			List<String> dataContextProperties) {
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
		PrintResult result = XWTCodegen.printRoot(printStream, Composite.class,
				hostClassName, imports.toArray(new String[0]), namespaces,
				dataContext, dataContextProperties, GridLayoutFactory
						.swtDefaults().numColumns(2).create());
		if (result.hasExternalContents()) {
			Map<String, Object> externalContents = result.getExternalContents();
			for (String key : externalContents.keySet()) {
				Object object = externalContents.get(key);
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
				createFile(embededType, embedFile, object,
						computeDataContextProperties(object, false));
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

	public static List<String> computeDataContextProperties(Object dataContext,
			boolean all) {
		if (dataContext == null) {
			return null;
		}
		List<String> dataContextProperties = new ArrayList<String>();
		if (dataContext instanceof EObject) {
			EClass eClass = (dataContext instanceof EClass)
					? (EClass) dataContext
					: ((EObject) dataContext).eClass();
			for (EStructuralFeature feature : eClass.getEStructuralFeatures()) {
				if (all) {
					dataContextProperties.add(feature.getName());
				} else {
					if (feature.isMany()) {
						continue;
					}
					EClassifier eType = feature.getEType();
					if (eType == null || eType instanceof EEnum
							|| eType instanceof EDataType) {
						dataContextProperties.add(feature.getName());
					}
				}
			}
		} else {
			try {
				Class<?> type = (dataContext instanceof Class<?>)
						? (Class<?>) dataContext
						: dataContext.getClass();
				BeanInfo beanInfo = all
						? Introspector.getBeanInfo(type)
						: Introspector.getBeanInfo(type, type.getSuperclass());
				PropertyDescriptor[] propertyDescriptors = beanInfo
						.getPropertyDescriptors();
				for (PropertyDescriptor pd : propertyDescriptors) {
					if (all) {
						dataContextProperties.add(pd.getName());
					} else {
						Class<?> propertyType = pd.getPropertyType();
						if (propertyType.isPrimitive()
								|| propertyType == String.class
								|| propertyType == URL.class
								|| propertyType.isEnum()) {
							dataContextProperties.add(pd.getName());
						}
					}
				}
			} catch (Exception e) {
			}
		}
		return dataContextProperties;
	}

	public static List<Object> computeEmbededTypes(Object dataContext) {
		if (dataContext == null) {
			return Collections.emptyList();
		}
		List<Object> embededTypes = new ArrayList<Object>();
		if (dataContext instanceof EObject) {
			EClass eClass = (dataContext instanceof EClass)
					? (EClass) dataContext
					: ((EObject) dataContext).eClass();
			for (EStructuralFeature feature : eClass.getEStructuralFeatures()) {
				EClassifier eType = feature.getEType();
				if (eType == null || eType instanceof EEnum
						|| eType instanceof EDataType) {
					continue;
				}
				embededTypes.add(eType);
			}
		} else {
			try {
				Class<?> type = (dataContext instanceof Class<?>)
						? (Class<?>) dataContext
						: dataContext.getClass();
				BeanInfo beanInfo = java.beans.Introspector.getBeanInfo(type);
				PropertyDescriptor[] propertyDescriptors = beanInfo
						.getPropertyDescriptors();
				for (PropertyDescriptor pd : propertyDescriptors) {
					Class<?> propertyType = pd.getPropertyType();
					if (propertyType.isPrimitive()
							|| propertyType == String.class
							|| propertyType == URL.class
							|| propertyType.isEnum()
							|| propertyType == Object.class) {
						continue;
					}
					embededTypes.add(propertyType);
				}
			} catch (Exception e) {
			}
		}
		return embededTypes;
	}

	public static PrintResult printDataContext(PrintStream printStream,
			Object dataContext, List<String> dataContextProperties,
			String prefixOffset) {
		if (dataContext instanceof EObject) {
			EClass eClass = (dataContext instanceof EClass)
					? (EClass) dataContext
					: ((EObject) dataContext).eClass();
			return printDataContextEMF(printStream, eClass,
					dataContextProperties, prefixOffset);
		} else {
			Class<?> type = (dataContext instanceof Class<?>)
					? (Class<?>) dataContext
					: dataContext.getClass();
			return printDataContextBean(printStream, type,
					dataContextProperties, prefixOffset);
		}
	}

	public static PrintResult printDataContextBean(PrintStream printStream,
			Class<?> dataContextType, List<String> dataContextProperties,
			String prefixOffset) {
		if (dataContextType == null || dataContextProperties == null) {
			return PrintResult.FAILED;
		}
		PrintResult result = new PrintResult();
		try {
			BeanInfo beanInfo = java.beans.Introspector.getBeanInfo(
					dataContextType, Introspector.IGNORE_ALL_BEANINFO);
			PropertyDescriptor[] propertyDescriptors = beanInfo
					.getPropertyDescriptors();
			if (propertyDescriptors == null) {
				return PrintResult.FAILED;
			}

			for (PropertyDescriptor pd : propertyDescriptors) {
				String name = pd.getName();
				if (name == null || "class".equals(name)) {
					continue;
				}
				if (dataContextProperties != null
						&& !dataContextProperties.contains(name)) {
					continue;
				}
				Class<?> propertyType = pd.getPropertyType();
				if (propertyType.isPrimitive() || propertyType == String.class
						|| propertyType == URL.class) {
					result.merge(printLabelText(printStream, pd
							.getDisplayName(), pd.getName(), prefixOffset));
				} else if (propertyType.isEnum()) {
					List<String> items = new ArrayList<String>();
					for (Object object : propertyType.getEnumConstants()) {
						items.add(object.toString());
					}
					result.merge(printLabelCombo(printStream, name, name, items
							.toArray(new String[0]), prefixOffset));
				} else {
					String elementType = propertyType.getName();
					String content = "<p:" + elementType
							+ " DataContext=\"{Binding path=" + name + "}\"/>";
					result.merge(printSurroundWithGroup(printStream, name,
							content, new FillLayout(), GridDataFactory
									.fillDefaults().create(), prefixOffset));
					result.getExternalContents().put(elementType, propertyType);
				}
			}
		} catch (IntrospectionException e) {
		}
		return result;
	}

	public static PrintResult printDataContextEMF(PrintStream printStream,
			EClass dataContextType, List<String> dataContextProperties,
			String prefixOffset) {
		if (dataContextType == null || dataContextProperties == null) {
			return PrintResult.FAILED;
		}
		PrintResult result = new PrintResult();
		for (EStructuralFeature feature : dataContextType
				.getEStructuralFeatures()) {
			String name = feature.getName();
			if (name == null) {
				continue;
			}
			if (!dataContextProperties.contains(name)) {
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
					printSurroundWithGroup(printStream, feature.getName(),
							content, new FillLayout(), GridDataFactory
									.fillDefaults().grab(true, false)
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
				result.merge(printLabelCombo(printStream, name, name, items
						.toArray(new String[0]), prefixOffset));
			} else if (propertyType instanceof EDataType) {
				result.merge(printLabelText(printStream, name, name,
						prefixOffset));
			} else {
				String elementType = propertyType.getName();
				String content = "<p:" + elementType
						+ " DataContext=\"{Binding path=" + name + "}\"/>";
				result.merge(printSurroundWithGroup(printStream, name, content,
						new FillLayout(), GridDataFactory.fillDefaults().span(
								2, 1).create(), prefixOffset));
				result.getExternalContents().put(elementType, propertyType);
			}
		}
		return result;
	}

	public static PrintResult printTableEMF(PrintStream printStream,
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
				printTableColumn(printStream, sf.getName(), sf.getName(), 100,
						nextOffset + "\t");
			}
			printStream.println(nextOffset + "</TableViewer.columns>");
		}

		String eventHandler = "";
		EAnnotation eAnnotation = feature
				.getEAnnotation(EMF_FEATURE_MASTER_KEY);
		if (eAnnotation != null) {
			eventHandler = "SelectionEvent=\"handleSelectionEvent\"";
		}
		printStream.println(nextOffset
				+ "<TableViewer.table HeaderVisible=\"true\" " + eventHandler
				+ "/>");
		if (layoutData != null) {
			printStream
					.println(nextOffset + "<TableViewer.control.layoutData>");
			printlayoutData(printStream, nextOffset, layoutData);
			printStream.println(nextOffset
					+ "</TableViewer.control.layoutData>");
		}

		printStream.println(prefixOffset + "</TableViewer>");
		return PrintResult.OK;
	}

	public static PrintResult printTableColumn(PrintStream printStream,
			String displayName, String displayMemberPath, int width,
			String prefixOffset) {
		if (prefixOffset == null) {
			prefixOffset = "";
		}

		if (displayName == null) {
			displayName = "Column";
		}
		String memberContents = "";
		if (displayMemberPath != null) {
			memberContents = " bindingPath=\"" + displayMemberPath + "\"";
		}
		printStream.println(prefixOffset + "<TableViewerColumn width=\""
				+ width + "\" text=\"" + displayName + "\"" + memberContents
				+ "/>");
		return PrintResult.OK;
	}
	public static PrintResult printRoot(PrintStream printStream,
			Class<?> rootType, String clr, String[] imports,
			Map<String, String> namespaces, Object dataContext,
			List<String> dataContextProperties, Layout layout) {
		PrintResult result = new PrintResult();
		String content = null;
		if (dataContext != null) {
			try {
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				PrintStream ps = new PrintStream(out);
				result.merge(printDataContext(ps, dataContext,
						dataContextProperties, "\t"));
				byte[] bytes = out.toByteArray();
				content = new String(bytes);
				ps.close();
				out.close();
			} catch (IOException e) {
			}
		}
		result.merge(printRoot(printStream, rootType, clr, imports, namespaces,
				content, layout));
		return result;
	}

	public static PrintResult printRoot(PrintStream printStream,
			Class<?> rootType, String clr, String[] imports,
			Map<String, String> namespaces, String content, Layout layout) {
		PrintResult result = new PrintResult();
		String name = rootType.getSimpleName();
		printStream.println("<" + name + " xmlns=\"" + IConstants.XWT_NAMESPACE
				+ "\"");
		String prefix = "\t";
		printStream.println(prefix + " xmlns:x=\"" + IConstants.XWT_X_NAMESPACE
				+ "\"");
		if (imports != null) {
			if (namespaces == null) {
				namespaces = new HashMap<String, String>();
				namespaces.put("java.lang", JAVA_LANG_PREFIX);
			}
			if (clr != null && clr.indexOf(".") != -1) {
				String clrPack = clr.substring(0, clr.lastIndexOf("."));
				namespaces.put(clrPack, EMBED_XWT_PREFIX);
				if (imports != null
						&& !Arrays.asList(imports).contains(clrPack)) {
					String[] newImports = new String[imports.length + 1];
					System.arraycopy(imports, 0, newImports, 0, imports.length);
					newImports[imports.length] = clrPack;
					imports = newImports;
				}
			}
			for (String imp : imports) {
				String pre = namespaces.get(imp);
				if (pre == null) {
					Random r = new Random(imp.length());
					char p = imp.charAt(0);
					while (namespaces.values().contains(p)
							|| !Character.isLetter(p)) {
						p = imp.charAt(r.nextInt());
					}
					namespaces.put(imp, pre = Character.toString(p));
				}
				printStream
						.println(prefix + " xmlns:" + pre + "=\""
								+ IConstants.XAML_CLR_NAMESPACE_PROTO + ""
								+ imp + "\"");
			}
		}
		if (clr != null) {
			printStream.println(prefix + " x:Class=\"" + clr + "\">");
		} else {
			printStream.println(">");
		}
		if (layout != null) {
			printStream.println(prefix + " <" + name + ".layout>");
			result.merge(printLayout(printStream, prefix, layout));
			printStream.println(prefix + " </" + name + ".layout>");
		}
		if (content != null) {
			printStream.println(prefix + " " + content);
		}

		printStream.println("</" + name + ">");

		return result;
	}

	public static PrintResult printLabelText(PrintStream printStream,
			String displayName, String bindingPath, String prefixOffset) {
		PrintResult result = new PrintResult();
		if (displayName == null) {
			displayName = "label";
		}
		if (prefixOffset == null) {
			prefixOffset = "";
		}
		result.merge(printControl(Label.class, printStream, displayName,
				prefixOffset));
		result.merge(printControl(Text.class, printStream, null, bindingPath,
				"BORDER", GridDataFactory.fillDefaults().grab(true, false)
						.create(), prefixOffset));
		return result;
	}

	public static PrintResult printControl(Class<?> control,
			PrintStream printStream, String displayName, String prefixOffset) {
		return printControl(control, printStream, displayName, null, null,
				null, prefixOffset);
	}
	public static PrintResult printControl(Class<?> control,
			PrintStream printStream, String displayName, String bindingPath,
			String style, Object layoutData, String prefixOffset) {
		if (control == null) {
			return PrintResult.FAILED;
		}
		PrintResult result = new PrintResult();
		if (prefixOffset == null) {
			prefixOffset = "";
		}
		if (style == null) {
			style = "NONE";
		}
		String controlName = control.getSimpleName();
		if (displayName != null) {
			printStream.println(prefixOffset + " <" + controlName
					+ " x:style=\"" + style + "\" text=\"" + displayName
					+ "\">");
		} else if (bindingPath != null) {
			printStream.println(prefixOffset + " <" + controlName
					+ " x:style=\"" + style + "\" text=\"{Binding path="
					+ bindingPath + "}\">");
		} else {
			printStream.println(prefixOffset + " <" + controlName
					+ " x:style=\"" + style + "\">");
		}
		if (layoutData != null) {
			String nextPrefix = prefixOffset + "\t";
			printStream.println(nextPrefix + " <" + controlName
					+ ".layoutData>");
			result.merge(printlayoutData(printStream, nextPrefix + "\t",
					layoutData));
			printStream.println(nextPrefix + " </" + controlName
					+ ".layoutData>");
		}
		printStream.println(prefixOffset + " </" + controlName + ">");
		return result;
	}
	public static PrintResult printCombo(PrintStream printStream,
			String bindingPath, String[] items, Object layoutData,
			String prefixOffset) {
		PrintResult result = new PrintResult();

		if (bindingPath == null) {
			printStream.println(prefixOffset + " <Combo>");
		} else {
			printStream.println(prefixOffset + " <Combo text=\"{Binding path="
					+ bindingPath + "}\">");
		}

		String nextPrefix = prefixOffset + "\t";
		if (layoutData != null) {
			printStream.println(nextPrefix + " <Combo.layoutData>");
			result.merge(printlayoutData(printStream, nextPrefix + "\t",
					layoutData));
			printStream.println(nextPrefix + " </Combo.layoutData>");
		}

		if (items != null) {
			printStream.println(nextPrefix + " <Combo.items>");
			for (String item : items) {
				printStream.println(nextPrefix + "\t <j:String>" + item
						+ "</j:String>");
			}
			printStream.println(nextPrefix + " </Combo.items>");
		}
		printStream.println(prefixOffset + " </Combo>");
		return result;
	}
	public static PrintResult printLabelCombo(PrintStream printStream,
			String displayName, String bindingPath, String[] items,
			String prefixOffset) {
		PrintResult result = new PrintResult();
		if (displayName == null) {
			displayName = "label";
		}
		if (prefixOffset == null) {
			prefixOffset = "";
		}
		result.merge(printControl(Label.class, printStream, displayName,
				prefixOffset));
		result.merge(printCombo(printStream, bindingPath, items,
				GridDataFactory.fillDefaults().grab(true, false).create(),
				prefixOffset));
		return result;
	}

	public static PrintResult printSurroundWithGroup(PrintStream printStream,
			String displayName, String content, Layout layout,
			Object layoutData, String prefixOffset) {
		PrintResult result = new PrintResult();
		if (prefixOffset == null) {
			prefixOffset = "";
		}
		if (displayName == null) {
			printStream.println(prefixOffset + " <Group>");
		} else {
			printStream.println(prefixOffset + " <Group text=\"" + displayName
					+ "\">");
		}
		String nextPrefix = prefixOffset + "\t";
		printStream.println(nextPrefix + " <Group.layout>");
		result.merge(printLayout(printStream, nextPrefix + "\t", layout));
		printStream.println(nextPrefix + " </Group.layout>");

		if (content != null) {
			printStream.println(nextPrefix + " " + content);
		}

		printStream.println(nextPrefix + " <Group.layoutData>");
		result.merge(printlayoutData(printStream, nextPrefix + "\t", layoutData));
		printStream.println(nextPrefix + " </Group.layoutData>");

		printStream.println(prefixOffset + " </Group>");
		return result;
	}

	public static PrintResult printLayout(PrintStream printStream,
			String prefixOffset, Layout layout) {
		if (layout == null) {
			return PrintResult.FAILED;
		}
		if (prefixOffset == null) {
			prefixOffset = "";
		}
		if (layout instanceof GridLayout) {
			GridLayout gl = (GridLayout) layout;
			printStream.println(prefixOffset + " <GridLayout numColumns=\""
					+ gl.numColumns + "\" horizontalSpacing=\""
					+ gl.horizontalSpacing + "\"");
			printStream.println(prefixOffset + "\t makeColumnsEqualWidth=\""
					+ gl.makeColumnsEqualWidth + "\" marginBottom=\""
					+ gl.marginBottom + "\" marginHeight=\"" + gl.marginHeight
					+ "\" marginLeft=\"" + gl.marginLeft + "\"");
			printStream.println(prefixOffset + "\t marginRight=\""
					+ gl.marginRight + "\" marginTop=\"" + gl.marginTop
					+ "\" marginWidth=\"" + gl.marginWidth
					+ "\" verticalSpacing=\"" + gl.verticalSpacing + "\" />");

		} else if (layout instanceof FillLayout) {
			FillLayout fl = (FillLayout) layout;
			printStream.println(prefixOffset + " <FillLayout marginHeight=\""
					+ fl.marginHeight + "\" marginWidth=\"" + fl.marginWidth
					+ "\" spacing=\"" + fl.spacing + "\"");
			printStream.println(prefixOffset + "\t type=\""
					+ (fl.type == SWT.HORIZONTAL ? "HORIZONTAL" : "VERTICAL")
					+ "\" />");
		} else if (layout instanceof RowLayout) {
			RowLayout rl = (RowLayout) layout;
			printStream.println(prefixOffset + " <RowLayout type=\""
					+ (rl.type == SWT.HORIZONTAL ? "HORIZONTAL" : "VERTICAL")
					+ "\" center=\"" + rl.center + "\" fill=\"" + rl.fill
					+ "\" justify=\"" + rl.justify + "\"");
			printStream.println(prefixOffset + "\t marginBottom=\""
					+ rl.marginBottom + "\" marginHeight=\"" + rl.marginHeight
					+ "\" marginLeft=\"" + rl.marginLeft + "\" marginRight=\""
					+ rl.marginRight + "\"");
			printStream.println(prefixOffset + "\t marginTop=\"" + rl.marginTop
					+ "\" marginWidth=\"" + rl.marginWidth + "\" pack=\""
					+ rl.pack + "\" spacing=\"" + rl.spacing + "\" wrap=\""
					+ rl.wrap + "\" />");
		} else if (layout instanceof StackLayout) {
			// FIXME: finish codes.
			printStream.println(prefixOffset + " <StackLayout />");
		} else if (layout instanceof FormLayout) {
			// FIXME: finish codes.
			printStream.println(prefixOffset + " <FormLayout />");
		} else {
			return PrintResult.FAILED;
		}
		return PrintResult.OK;
	}

	public static PrintResult printlayoutData(PrintStream printStream,
			String prefixOffset, Object layoutData) {
		if (layoutData == null) {
			return PrintResult.FAILED;
		}
		if (prefixOffset == null) {
			prefixOffset = "";
		}
		if (layoutData instanceof GridData) {
			GridData gd = (GridData) layoutData;
			printStream.println(prefixOffset
					+ " <GridData grabExcessVerticalSpace=\""
					+ gd.grabExcessVerticalSpace + "\"");
			String alignment = null;
			switch (gd.horizontalAlignment) {
				case GridData.FILL :
					alignment = "GridData.FILL";
					break;
				case GridData.BEGINNING :
					alignment = "GridData.BEGINNING";
				case GridData.CENTER :
					alignment = "GridData.CENTER";
				case GridData.END :
					alignment = "GridData.END";
				case GridData.FILL_BOTH :
					alignment = "GridData.FILL_BOTH";
				case GridData.FILL_HORIZONTAL :
					alignment = "GridData.FILL_HORIZONTAL";
				default :
					break;
			}
			printStream.println(prefixOffset
					+ "\t grabExcessHorizontalSpace=\""
					+ gd.grabExcessHorizontalSpace
					+ "\" horizontalAlignment=\"" + alignment + "\"");
			printStream.println(prefixOffset + "\t verticalAlignment=\""
					+ alignment + "\" horizontalSpan=\"" + gd.horizontalSpan
					+ "\" verticalSpan=\"" + gd.verticalSpan + "\"/>");
		} else if (layoutData instanceof RowData) {
			RowData rd = (RowData) layoutData;
			printStream.println(prefixOffset + "<RowData width=\"" + rd.width
					+ "\" height=\"" + rd.height + "\" />");
		} else if (layoutData instanceof FormData) {
			printStream.println(prefixOffset + "\t<FormData/>");
		} else {
			return PrintResult.FAILED;
		}
		return PrintResult.OK;
	}

	public static void main(String[] args) {
		// appendLabelText(System.out, "hello", "hello", "");
		// appendLabelCombo(System.out, "world", "world", new String[]{"hello",
		// "world"}, "\t");
		// surroundWithGroup(System.out, "group", "<Text/>", new FillLayout(),
		// GridDataFactory.fillDefaults().create(), "");
		// appendControl(Label.class, System.out, "hello", "");
		// printControl(Text.class, System.out, null, "hello", "BORDER",
		// GridDataFactory.fillDefaults().create(), "");

		printRoot(System.out, Composite.class, "hello.example.World",
				new String[]{"java.lang", "org.eclipse.swt"}, null,
				"<Hello World/>", GridLayoutFactory.swtDefaults().create());
	}
}
