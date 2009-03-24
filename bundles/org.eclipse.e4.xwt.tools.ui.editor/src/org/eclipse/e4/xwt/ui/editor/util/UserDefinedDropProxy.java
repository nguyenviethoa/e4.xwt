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
package org.eclipse.e4.xwt.ui.editor.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.e4.xwt.ui.editor.XWTEditor;
import org.eclipse.e4.xwt.ui.utils.ProjectContext;
import org.eclipse.e4.xwt.vex.VEXTextEditorHelper;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.eclipse.wst.sse.ui.internal.StructuredTextViewer;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author jliu
 */
public class UserDefinedDropProxy {

	private XWTEditor editor;
	private Map<String, String> nsURIs = new HashMap<String, String>();
	private String name;
	private String namespace;
	private String prefix;

	public UserDefinedDropProxy(XWTEditor editor) {
		this.editor = editor;
		updateNsURIs();
	}

	public boolean isUserDefined(Object obj) {
		if (obj instanceof ICompilationUnit) {
			ICompilationUnit unit = (ICompilationUnit) obj;
			IType type = unit.findPrimaryType();
			String name = type.getElementName();
			IJavaProject javaProject = unit.getJavaProject();
			String fullyQualifiedName = type.getFullyQualifiedName();
			try {
				ProjectContext context = ProjectContext.getContext(javaProject);
				Class<?> clazz = context.getClassLoader().loadClass(fullyQualifiedName);
				this.name = clazz.getSimpleName();
				this.namespace = "clr-namespace" + ":" + type.getPackageFragment().getElementName();
				return Composite.class.isAssignableFrom(clazz) && clazz.getResource(name + ".xwt") != null;
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		} else if (obj instanceof IFile) {
			IFile file = (IFile) obj;
			String ext = file.getFileExtension();
			String name = file.getName();

			if ("xwt".equals(ext)) {
				IResource java = file.getParent().findMember(name.replace("xwt", "java"));
				if (java.exists()) {
					IProject project = file.getProject();
					IJavaProject javaProject = JavaCore.create(project);
					try {
						ICompilationUnit unit = (ICompilationUnit) JavaCore.create(java);
						IType type = unit.findPrimaryType();
						String fullyQualifiedName = type.getFullyQualifiedName();
						ProjectContext context = ProjectContext.getContext(javaProject);
						Class<?> clazz = context.getClassLoader().loadClass(fullyQualifiedName);
						this.name = clazz.getSimpleName();
						this.namespace = "clr-namespace" + ":" + type.getPackageFragment().getElementName();
						return Composite.class.isAssignableFrom(clazz);
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return false;
	}

	public String getName() {
		updateNsURIs();
		if (namespace != null) {
			prefix = nsURIs.get(namespace);
			if (prefix == null) {
				prefix = genPrefix(nsURIs.values());
			}
		}
		return prefix + ":" + name;
	}

	private void updateNsURIs() {
		nsURIs.clear();
		StructuredTextEditor textEditor = editor.getTextEditor();
		StructuredTextViewer textViewer = textEditor.getTextViewer();
		IDOMNode node = VEXTextEditorHelper.getNode(textViewer, 0);
		updateNsURIs(node, nsURIs);
	}

	private String genPrefix(Collection<String> existings) {
		char[] c = { 'q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p', 'a', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'l', 'z', 'c', 'v', 'b', 'n', 'm' };
		Random random = new Random();
		String prefix = Character.toString(c[Math.abs(random.nextInt()) % c.length]);
		while (existings.contains(prefix)) {
			prefix = Character.toString(c[Math.abs(random.nextInt()) % c.length]);
		}
		return prefix;
	}

	public void updateNsURIs(Object obj, Map<String, String> nsURIs) {
		if (obj instanceof Node) {
			Node node = (Node) obj;
			String p = node.getPrefix();
			String ns = node.getNamespaceURI();
			if (ns != null) {
				nsURIs.put(ns, p);
			}
			NodeList childNodes = node.getChildNodes();
			for (int i = 0; i < childNodes.getLength(); i++) {
				updateNsURIs(childNodes.item(i), nsURIs);
			}
		}
	}

	public String getContent() {
		return "<" + getName() + "/>";
	}

	public String getPrefix() {
		return prefix;
	}

	public boolean isNsURINew() {
		return !nsURIs.values().contains(prefix);
	}

	public String getNamespace() {
		return namespace;
	}

}
