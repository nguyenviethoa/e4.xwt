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
package org.eclipse.e4.xwt.tools.ui.designer.editor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.xwt.tools.ui.designer.core.editor.Designer;
import org.eclipse.e4.xwt.tools.ui.designer.core.editor.IModelBuilder;
import org.eclipse.e4.xwt.tools.ui.designer.core.editor.ModelBuildListener;
import org.eclipse.e4.xwt.tools.ui.designer.core.editor.text.StructuredTextHelper;
import org.eclipse.e4.xwt.tools.ui.designer.utils.XWTModelUtil;
import org.eclipse.e4.xwt.tools.ui.xaml.XamlAttribute;
import org.eclipse.e4.xwt.tools.ui.xaml.XamlDocument;
import org.eclipse.e4.xwt.tools.ui.xaml.XamlElement;
import org.eclipse.e4.xwt.tools.ui.xaml.XamlFactory;
import org.eclipse.e4.xwt.tools.ui.xaml.XamlNode;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.UIJob;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.xml.core.internal.document.TextImpl;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * @author jliu jin.liu@soyatec.com
 */
@SuppressWarnings("restriction")
public class XWTModelBuilder extends EContentAdapter implements IModelBuilder {

	private static final int NONE = 0;
	private static final int LOADING = 1;
	private static final int BUILDING_MODEL = 2;
	private static final int BUILDING_TEXT = 3;
	private static final int RELOADING = 4;

	private static final String CHARACTERS = "abcdefghigklmnopqrstuvwxyz";
	private Designer designer;
	private XamlDocument document;
	private IDocument jfaceDom;
	private List<ModelBuildListener> listeners;
	private IFile input;
	private static Random RANDOM = new Random();
	private BraceHandler braceHandler;
	private ModelMapper mapper = new ModelMapper();
	private ReloadJob reloadJob;
	private NodeAdapter nodeAdapter = new NodeAdapter();

	private int buildingType = NONE;

	public static String generateID(String typeName) {
		return typeName + RANDOM.nextInt(Integer.MAX_VALUE);
	}

	public boolean doLoad(Designer designer, final IProgressMonitor monitor) {
		this.designer = designer;
		input = designer.getInputFile();
		jfaceDom = designer.getDocument();
		if (jfaceDom == null) {
			return false;
		}
		if (monitor != null) {
			monitor.beginTask("Loading Document", 100);
		}
		jfaceDom.addDocumentListener(new IDocumentListener() {
			public void documentAboutToBeChanged(DocumentEvent event) {
			}

			public void documentChanged(DocumentEvent event) {
				// Revert File.
				if (event.getOffset() == 0 && event.getLength() != 0
						&& event.getText() != null) {
					new ReloadJob().schedule();
				}
			}
		});
		if (input != null) {
			document = ModelCacheUtility.doLoadFromCache(input, monitor);
		}
		if (document == null) {
			document = XamlFactory.eINSTANCE.createXamlDocument();
			ModelCacheUtility.doSaveCache(document, input, monitor);
		}
		IDOMDocument textDocument = getTextDocument(jfaceDom);
		if (textDocument == null) {
			return false;
		}
		if (!textDocument.getAdapters().contains(nodeAdapter)) {
			textDocument.addAdapter(nodeAdapter);
		}
		final IDOMElement textElement = (IDOMElement) textDocument
				.getDocumentElement();

		if (buildingType == NONE) {
			buildingType = LOADING;
			loadingModel(textElement, monitor);
			buildingType = NONE;
		}
		return true;
	}

	private BraceHandler getBraceHandler() {
		if (braceHandler == null) {
			braceHandler = createBraceHandler(document);
		}
		return braceHandler;
	}

	protected BraceHandler createBraceHandler(XamlDocument document) {
		return new BraceHandler(document);
	}

	private boolean hasProblems() {
		if (designer == null) {
			return true;
		}
		// if (designer.getProblemHandler().hasProblems()) {
		// return true;
		// }
		return false;
	}

	/**
	 * @param textDocument
	 * @param document
	 */
	protected void handleDeclaredNamespaces(String prefix, String namespace) {
		if (document != null) {
			document.getDeclaredNamespaces().put(prefix, namespace);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.soyatec.xaml.ve.editor.IDiagramModelBuilder#getModelRoot()
	 */
	public XamlDocument getDocumentRoot() {
		return document;
	}

	protected void loadingModel(IDOMElement text, IProgressMonitor monitor) {
		if (text == null || buildingType == BUILDING_MODEL) {
			return;
		}
		String name = text.getLocalName();
		String nsURI = text.getNamespaceURI();
		String prefix = text.getPrefix();
		XamlElement rootElement = document.getRootElement();

		document.eResource().eAdapters().add(this);

		boolean isNew = false;
		if (rootElement == null || !name.equals(rootElement.getName())) {
			rootElement = XamlFactory.eINSTANCE.createElement(name, nsURI);
			isNew = true;
		}
		if (monitor != null) {
			monitor.subTask("Load element " + name);
			monitor.worked(1);
		}
		rootElement.setPrefix(prefix);
		createChild(rootElement, text, monitor);

		map(rootElement, text);
		// addAdapter(document);
		if (isNew) {
			document.setRootElement(rootElement);
		}
	}

	protected void createChild(XamlNode parent, IDOMElement text,
			IProgressMonitor monitor) {
		List<IDOMNode> attributes = getAttributes(text);
		List<XamlAttribute> oldAttrs = new ArrayList<XamlAttribute>(parent
				.getAttributes());
		for (int i = 0; i < attributes.size(); i++) {
			IDOMNode attr = attributes.get(i);
			oldAttrs.remove(createAttribute(parent, attr, i));
			if (monitor != null) {
				monitor.subTask("Load attr " + attr.getNodeName());
				monitor.worked(1);
			}
		}
		if (!oldAttrs.isEmpty()) {
			parent.getAttributes().removeAll(oldAttrs);
		}

		List<IDOMElement> childNodes = getChildNodes(text);
		List<XamlElement> oldChildren = new ArrayList<XamlElement>(parent
				.getChildNodes());
		for (int i = 0; i < childNodes.size(); i++) {
			IDOMElement child = childNodes.get(i);
			oldChildren.remove(createElement(parent, child, i));
			if (monitor != null) {
				monitor.subTask("Load child " + child.getNodeName());
				monitor.worked(1);
			}
		}
		if (!oldChildren.isEmpty()) {
			parent.getChildNodes().removeAll(oldChildren);
		}

		String content = getContent(text);
		if (content != null) {
			parent.setValue(content);
		}
	}

	protected List<IDOMElement> getChildNodes(IDOMNode parent) {
		if (parent == null) {
			return Collections.emptyList();
		}
		List<IDOMElement> childNodes = new ArrayList<IDOMElement>();
		Node child = parent.getFirstChild();
		while (child != null) {
			if (child instanceof IDOMElement
					&& child.getLocalName().indexOf(".") == -1) {
				childNodes.add((IDOMElement) child);
			}
			child = child.getNextSibling();
		}
		return childNodes;
	}

	protected List<Text> getContentNodes(IDOMNode node) {
		NodeList childNodes = node.getChildNodes();
		int length = childNodes.getLength();
		if (length == 0) {
			return Collections.emptyList();
		}
		List<Text> contentTexts = new ArrayList<Text>();
		for (int i = 0; i < length; i++) {
			Node item = childNodes.item(i);
			if (item.getNodeType() == Node.TEXT_NODE) {
				contentTexts.add((Text) item);
			}
		}
		return contentTexts;
	}

	/**
	 * Return content of a Node, "<j:String>hello world</j:String>"
	 */
	protected String getContent(IDOMNode parent) {
		List<Text> textNodes = getContentNodes(parent);
		if (textNodes.isEmpty()) {
			return null;
		}
		StringBuilder content = new StringBuilder();
		for (Text text : textNodes) {
			String value = text.getNodeValue();
			if (value == null) {
				continue;
			}
			value = filter(value.trim());
			if (value.length() != 0) {
				content.append(value);
			}
		}
		return content.length() > 0 ? content.toString() : null;
	}

	protected String filter(String value) {
		value = value.replace("\n", "");
		value = value.replace("\t", "");
		value = value.replace("\r", "");
		return value;
	}

	protected List<IDOMNode> getAttributes(IDOMNode parent) {
		if (parent == null) {
			return Collections.emptyList();
		}
		List<IDOMNode> attributes = new ArrayList<IDOMNode>();
		NamedNodeMap attrMap = parent.getAttributes();
		if (attrMap != null) {
			int length = attrMap.getLength();
			for (int i = 0; i < length; i++) {
				IDOMAttr item = (IDOMAttr) attrMap.item(i);
				String name = item.getLocalName();
				String value = item.getNodeValue();
				String prefix = item.getPrefix();
				if (name.indexOf(".") != -1) {
					name = name.substring(name.indexOf(".") + 1);
					prefix = null;
				}
				if ("xmlns".equals(name)) {
					handleDeclaredNamespaces(null, value);
					continue;
				}
				if ("xmlns".equals(prefix)) {
					handleDeclaredNamespaces(name, value);
					continue;
				}
				attributes.add(item);
			}
		}
		NodeList children = parent.getChildNodes();
		if (children != null) {
			for (int i = 0; i < children.getLength(); i++) {
				Node item = children.item(i);
				String localName = item.getLocalName();
				if (item instanceof IDOMElement && localName.indexOf(".") != -1) {
					attributes.add((IDOMElement) item);
				}
			}
		}
		return attributes;
	}

	private String normalizeName(String tagName) {
		if (tagName == null) {
			return null;
		}
		StringBuffer buffer = new StringBuffer();

		boolean isH = false;
		for (int i = 0, len = tagName.length(); i < len; i++) {
			char c = tagName.charAt(i);
			if (i == 0) {
				buffer.append(Character.toUpperCase(c));
			} else {
				switch (c) {
				case '-':
					isH = true;
					break;
				case '.':
					isH = true;
					buffer.append(c);
					break;
				default:
					if (isH) {
						buffer.append(Character.toUpperCase(c));
						isH = false;
					} else {
						buffer.append(c);
					}
					break;
				}
			}
		}
		return buffer.toString();
	}

	protected XamlElement createElement(XamlNode parent, IDOMElement child,
			int index) {
		String name = child.getLocalName();
		String prefix = child.getPrefix();
		String ns = document.getDeclaredNamespace(prefix);
		XamlElement e = parent.getChild(index);
		if (e == null || e.getName() == null || e.getNamespace() == null
				|| !e.getName().equals(name) || !e.getNamespace().equals(ns)) {
			e = XamlFactory.eINSTANCE.createElement(normalizeName(name), ns);
			e.setId(generateID(name));
		}
		map(e, child);
		e.setPrefix(prefix);
		createChild(e, child, null);
		return addChild(parent, e, index);
	}

	/**
	 * @param parent
	 * @param attr
	 */
	protected XamlNode createAttribute(XamlNode parent, IDOMNode attr, int index) {
		if (attr == null) {
			return null;
		}
		String localName = attr.getLocalName();
		String value = attr.getNodeValue();
		String prefix = attr.getPrefix();
		String ns = document.getDeclaredNamespace(prefix);
		// StringTokenizer stk = new StringTokenizer(localName, ".");
		// List<String> names = new ArrayList<String>();
		// while (stk.hasMoreTokens()) {
		// String nextToken = stk.nextToken();
		// if (nextToken.equals(parent.getName())) {
		// continue;
		// }
		// names.add(nextToken);
		// }
		// String name = null;
		// String groupName = null;
		// int length = names.size();
		// if (length == 1) {
		// name = names.get(0);
		// } else {
		// if (length == 2) {
		// String prefixName = names.get(0);
		// if (!prefixName.equals(parent.getName())) {
		// groupName = normalizeName(prefixName);
		// }
		// name = names.get(1);
		// }
		// for (int i = 0; i < length - 1; i++) {
		// String attrName = names.get(i);
		// XamlNode oldParent = parent;
		// parent = getAttribute(parent, attrName, ns);
		// if (parent == null) {
		// parent =
		// XamlFactory.eINSTANCE.createAttribute(normalizeName(attrName), ns);
		// oldParent.getAttributes().add((XamlAttribute) parent);
		// }
		// name = names.get(i + 1);
		// }
		// }

		String name = localName;
		int i = localName.indexOf(".");
		if (i != -1) {
			name = localName.substring(i + 1);
		}

		// TODO: support groupName;

		if (name == null || "".equals(name)) {
			return null;
		}
		if ("xmlns".equals(name)) {
			handleDeclaredNamespaces(null, value);
			return null;
		}
		if ("xmlns".equals(prefix)) {
			handleDeclaredNamespaces(name, value);
			return null;
		}

		XamlAttribute a = getAttribute(parent, name, ns);
		if (a == null) {
			a = XamlFactory.eINSTANCE.createAttribute(normalizeName(name), ns);
			a.setId(generateID(name));
		}
		// a.setGroupName(groupName);
		if (attr instanceof IDOMElement) {
			IDOMElement child = (IDOMElement) attr;
			createChild(a, child, null);
		}
		a.setPrefix(prefix);
		if (handleBraces(value)) {
			XamlNode valueNode = getBraceHandler().parse(a, value);
			if (valueNode != null && valueNode instanceof XamlElement) {
				addChild(a, (XamlElement) valueNode, -1);
			}
			for (Iterator<XamlElement> iterator = a.getChildNodes().iterator(); iterator
					.hasNext();) {
				if (iterator.next() != valueNode) {
					iterator.remove();
				}
			}
			a.setValue(null);
		} else {
			a.setValue(value);
			if (value != null) {
				a.getChildNodes().clear();
			}
		}
		map(a, attr);
		return addAttribute(parent, a, index);
	}

	private boolean handleBraces(String value) {
		return value != null && value.startsWith("{") && value.endsWith("}");
	}

	protected XamlAttribute getAttribute(XamlNode parent, String attrName,
			String namespace) {
		return XWTModelUtil.getAdaptableAttribute(parent, attrName, namespace);
	}

	protected XamlElement addChild(XamlNode parent, XamlElement n, int index) {
		if (parent == null || n == null) {
			return n;
		}
		EList<XamlElement> childNodes = parent.getChildNodes();
		int size = childNodes.size();
		if (!parent.getChildNodes().contains(n)) {
			if (index < 0 || index > size) {
				index = size - 1;
			}
			if (index < 0) {
				index = 0;
			}
			childNodes.add(index, n);
		} else if (index >= 0 && index < size) {
			int oldIndex = childNodes.indexOf(n);
			if (oldIndex != index) {
				childNodes.move(index, oldIndex);
			}
		}
		return n;
	}

	protected XamlAttribute addAttribute(XamlNode parent, XamlAttribute a,
			int index) {
		if (parent == null || a == null) {
			return null;
		}
		EList<XamlAttribute> attributes = parent.getAttributes();
		int size = attributes.size();
		if (!parent.getAttributes().contains(a)) {
			if (index < 0 || index > size) {
				index = size - 1;
			}
			if (index < 0) {
				index = 0;
			}
			attributes.add(index, a);
		} else if (index >= 0 && index < size) {
			int oldIndex = attributes.indexOf(a);
			if (oldIndex != index) {
				attributes.move(index, oldIndex);
			}
		}
		return a;
	}

	protected IDOMDocument getTextDocument(IDocument doc) {
		IStructuredModel model = StructuredModelManager.getModelManager()
				.getExistingModelForRead(doc);
		if (model != null && model instanceof IDOMModel) {
			return ((IDOMModel) model).getDocument();
		}

		return null;
	}

	protected void reverseAttr(IDOMNode parent, XamlAttribute model) {
		IDOMDocument textDocument = getTextDocument(jfaceDom);
		// Node generate = model.generate(textDocument, parent);
		// if (generate != null && generate instanceof IDOMNode) {
		// map(model, (IDOMNode) generate);
		// }
		validatePrefix(model);
		String localName = parent.getLocalName();
		String value = model.getValue();
		String name = model.getName();
		if (value == null && model.isUseFlatValue()) {
			String flatValue = model.getFlatValue();
			if (flatValue != null && !"".equals(flatValue)) {
				value = "{" + flatValue + "}";
			}
		}
		if (value != null && parent instanceof IDOMElement) {
			IDOMAttr attr = (IDOMAttr) textDocument.createAttribute(name);
			attr.setNodeValue(value);
			attr.setPrefix(model.getPrefix());
			((IDOMElement) parent).setAttributeNode(attr);
			map(model, attr);
		} else {
			String childName = localName + "." + name;
			while (localName != null && localName.indexOf(".") != -1) {
				/*
				 * If the parent like: "TableViewer.table", we should add the
				 * new attribute("layoutData") to TableViewer element directly,
				 * the new element should be "TableViewer.table.layoutData" and
				 * its parent should be "TableViewer" but not
				 * "TableViewer.table".
				 */
				parent = (IDOMElement) parent.getParentNode();
				localName = parent.getLocalName();
			}
			EList<XamlElement> childNodes = model.getChildNodes();
			EList<XamlAttribute> attributes = model.getAttributes();
			if (!attributes.isEmpty()) {
				for (XamlAttribute attr : attributes) {
					if (attr.getValue() != null) {
						IDOMElement node = (IDOMElement) textDocument
								.createElement(childName);
						reverseAttr(node, attr);
						parent.appendChild(node);
						map(model, node);
					} else {
						IDOMElement node = (IDOMElement) textDocument
								.createElement(childName);
						EList<XamlElement> childNodes2 = attr.getChildNodes();
						for (XamlElement child : childNodes2) {
							reverseNode(node, child);
						}
						parent.appendChild(node);
						map(model, node);
					}
				}
			} else if (!childNodes.isEmpty()) {
				IDOMElement node = (IDOMElement) textDocument
						.createElement(childName);
				for (XamlElement child : childNodes) {
					reverseNode(node, child);
				}
				parent.appendChild(node);
				map(model, node);
			}
		}
	}

	public void map(XamlNode model, IDOMNode text) {
		mapper.map(model, text);
		if (!text.getAdapters().contains(nodeAdapter)) {
			text.addAdapter(nodeAdapter);
		}
	}

	private String validatePrefix(XamlNode node) {
		IDOMDocument textDocument = getTextDocument(jfaceDom);
		String prefix = node.getPrefix();
		String namespace = node.getNamespace();
		if (prefix == null && namespace != null) {
			prefix = getPrefix(namespace);
		}
		if ((prefix != null && !prefix.equals(node.getPrefix()))
				|| (prefix == null && node.getPrefix() != null)) {
			node.setPrefix(prefix);
		}
		if (prefix != null) {
			Element root = textDocument.getDocumentElement();
			Attr prefixNode = root.getAttributeNode(prefix);
			if (prefixNode == null) {
				root.setAttribute("xmlns:" + prefix, namespace);
				document.addDeclaredNamespace(prefix, namespace);
			}
		}
		for (XamlAttribute attr : node.getAttributes()) {
			validatePrefix(attr);
		}
		for (XamlElement child : node.getChildNodes()) {
			validatePrefix(child);
		}
		return prefix;
	}

	protected void reverseNode(IDOMNode parent, XamlElement element) {
		String name = element.getName();
		IDOMDocument textDocument = getTextDocument(jfaceDom);
		IDOMElement node = (IDOMElement) textDocument.createElement(name);
		EList<XamlAttribute> attributes = element.getAttributes();
		EList<XamlElement> childnodes = element.getChildNodes();
		for (XamlAttribute attribute : attributes) {
			reverseAttr(node, attribute);
		}
		for (XamlElement child : childnodes) {
			reverseNode(node, child);
		}
		String prefix = validatePrefix(element);
		if (prefix != null) {
			node.setPrefix(prefix);
		}
		XamlNode next = null;
		XamlNode container = (XamlNode) element.eContainer();
		if (container != null) {
			int i = container.getChildNodes().indexOf(element);
			try {
				next = container.getChildNodes().get(i + 1);
			} catch (Exception e) {
			}
		}
		if (next != null) {
			IDOMNode nextNode = mapper.getTextNode(next);
			parent.insertBefore(node, nextNode);
		} else {
			parent.appendChild(node);
		}
		String value = element.getValue();
		if (value != null) {
			reverseContent(node, value);
		}
		map(element, node);
	}

	protected String getPrefix(String namespace) {
		EMap<String, String> declaredNamespaces = document
				.getDeclaredNamespaces();
		Set<String> existings = declaredNamespaces.keySet();
		for (String p : existings) {
			if (namespace.equals(declaredNamespaces.get(p))) {
				return p;
			}
		}
		char[] c = CHARACTERS.toCharArray();
		for (char d : c) {
			String prefix = Character.toString(d);
			if (!existings.contains(prefix)) {
				return prefix;
			}
		}
		return "j";
	}

	protected void reverseContent(IDOMNode node, String value) {
		String content = getContent(node);
		if (value == null && content == null || value.equals(content)) {
			return;
		}
		value = value == null ? "" : value;
		if (content != null) {
			List<Text> contentNodes = getContentNodes(node);
			for (Text text : contentNodes) {
				String nodeValue = text.getNodeValue();
				if (nodeValue == null || filter(nodeValue).length() == 0) {
					continue;
				}
				text.setData(value);
			}
		} else {
			IDOMDocument textDocument = getTextDocument(jfaceDom);
			Text textNode = textDocument.createTextNode(value == null ? ""
					: value);
			node.appendChild(textNode);
		}
	}

	/**
	 * @see org.eclipse.emf.common.notify.impl.AdapterImpl#notifyChanged(org.eclipse.emf.common.notify.Notification)
	 */
	public void notifyChanged(final Notification msg) {
		if (!isValidThread()) {
			return;
		}
		if (buildingType != LOADING) {
			fireModelEvent(msg);
		}
		if (buildingType == NONE) {
			UIJob rewirteJob = new UIJob("Rewrite") {
				public IStatus runInUIThread(IProgressMonitor monitor) {
					tryToUpdateText(msg);
					return Status.OK_STATUS;
				}
			};
			rewirteJob.setRule(new ISchedulingRule() {
				public boolean contains(ISchedulingRule rule) {
					return getClass() == rule.getClass();
				}

				public boolean isConflicting(ISchedulingRule rule) {
					return getClass() == rule.getClass();
				}
			});
			rewirteJob.schedule(500);
		}
	}

	protected void tryToUpdateText(Notification msg) {
		if (buildingType != NONE || getTextDocument(jfaceDom) == null) {
			return;
		}
		if (designer != null) {
			designer.getCommandStack().stop();
		}
		super.notifyChanged(msg);

		IDOMDocument textDocument = getTextDocument(jfaceDom);
		Object notifier = msg.getNotifier();
		Object oldValue = msg.getOldValue();
		Object newValue = msg.getNewValue();
		if (oldValue != null && oldValue.equals(newValue)) {
			return;
		}
		synchronized (textDocument) {
			buildingType = BUILDING_MODEL;
			IDOMNode textNode = mapper.getTextNode(notifier);
			IDOMNode oldNode = mapper.getTextNode(oldValue);
			IDOMNode newNode = mapper.getTextNode(newValue);

			int eventType = msg.getEventType();
			switch (eventType) {
			case Notification.ADD: {
				if (textNode == null && notifier instanceof EObject) {
					EObject eContainer = ((EObject) notifier).eContainer();
					IDOMNode superParent = mapper.getTextNode(eContainer);
					if (superParent != null) {
						if (notifier instanceof XamlAttribute) {
							reverseAttr(superParent, (XamlAttribute) notifier);
						} else if (notifier instanceof XamlElement) {
							reverseNode(superParent, (XamlElement) newValue);
						}
					}
					textNode = mapper.getTextNode(notifier);
				}
				if (textNode == null
						|| (!(textNode instanceof IDOMElement) && !(textNode instanceof IDOMAttr))) {
					break;
				}
				if (notifier instanceof XamlAttribute
						&& textNode instanceof IDOMAttr
						&& newValue instanceof XamlElement) {
					validatePrefix((XamlElement) newValue);
					String flatValue = ((XamlElement) newValue).getFlatValue();
					if (flatValue == null) {
						flatValue = "";
					}
					((IDOMAttr) textNode).setValue("{" + flatValue + "}");
				} else if (newValue instanceof XamlAttribute) {
					reverseAttr(textNode, (XamlAttribute) newValue);
				} else if (newValue instanceof XamlElement) {
					reverseNode(textNode, (XamlElement) newValue);
				}
				break;
			}
			case Notification.ADD_MANY: {
				System.err.println("ADD_MANY");
				break;
			}
			case Notification.MOVE: {
				int oldPos = ((Integer) oldValue);
				int newPos = msg.getPosition();
				IDOMNode parentNode = textNode;
				IDOMNode moveable = newNode;
				if (parentNode == null || moveable == null
						|| parentNode != moveable.getParentNode()) {
					break;
				}
				List<IDOMElement> eles = getChildNodes(parentNode);
				int offset = newPos - oldPos;
				int oldIndex = eles.indexOf(moveable);
				int newIndex = offset > 0 ? oldIndex + offset + 1 : oldIndex
						+ offset;
				Node nextSibling = moveable.getNextSibling();
				parentNode.removeChild(moveable);
				if (nextSibling instanceof Text) {
					// String nodeValue = ((Text) nextSibling).getNodeValue();
					// if ((nodeValue == null || nodeValue.equals(""))) {
					parentNode.removeChild(nextSibling);
					// }
				}
				if (newIndex >= 0 && newIndex <= eles.size() - 1) {
					IDOMElement insert = eles.get(newIndex);
					parentNode.insertBefore(moveable, insert);
				} else {
					parentNode.appendChild(moveable);
				}
				break;
			}
			case Notification.REMOVE: {
				if (textNode != null) {
					if (oldNode instanceof IDOMElement) {
						NodeList nodelist = textNode.getChildNodes();
						// before remove, we must check if there is the oldNode
						// in the textNode.Because the oldNode may have be
						// deleted.
						for (int i = 0; i < nodelist.getLength(); i++) {
							if (nodelist.item(i) == oldNode) {
								textNode.removeChild(oldNode);
							}
						}
					} else if (textNode instanceof IDOMElement
							&& oldNode instanceof IDOMAttr) {
						if (contains(textNode, (Attr) oldNode)) {
							((IDOMElement) textNode)
									.removeAttributeNode((Attr) oldNode);
						}
					}
					mapper.remove(oldNode);
				}
				break;
			}
			case Notification.REMOVE_MANY: {
				System.err.println("REMOVE_MANY");
				break;
			}
			case Notification.SET:
			case Notification.UNSET:
				if (textNode != null) {
					if (textNode instanceof Attr) {
						String value = newValue == null ? "" : newValue
								.toString();
						Attr attr = (Attr) textNode;
						if (!value.equals(attr.getNodeValue())) {
							attr.setNodeValue(value);
						}
					} else if (newValue != null && oldValue != null
							&& oldValue.equals(getContent(textNode))) {
						reverseContent(textNode, newValue.toString());
					}
				}
				break;
			}
			format();
		}
		if (designer != null) {
			designer.getCommandStack().start();
		}
		buildingType = NONE;
	}

	/**
	 * Format Text of Editor.
	 */
	protected void format() {
		// Format in main thread.
		if (jfaceDom != null) {
			IDOMDocument textDocument = getTextDocument(jfaceDom);
			if (textDocument != null) {
				IDOMElement textElement = (IDOMElement) textDocument
						.getDocumentElement();
				formatRemoveEmpty(textElement);
			}
			StructuredTextHelper.format(jfaceDom);
		}
	}

	/**
	 * @param textElement
	 */
	protected void formatRemoveEmpty(Node node) {
		if (node == null) {
			return;
		}
		NodeList childNodes = node.getChildNodes();
		if (childNodes != null) {
			for (int i = 0; i < childNodes.getLength(); i++) {
				Node item = childNodes.item(i);
				if (item.getNodeType() == Node.TEXT_NODE) {
					TextImpl t = (TextImpl) item;
					if (t.isWhitespace() || t.isInvalid()) {
						Node next = t.getNextSibling();
						while (next != null
								&& next.getNodeType() == Node.TEXT_NODE
								&& (((TextImpl) next).isWhitespace() || ((TextImpl) next)
										.isInvalid())) {
							node.removeChild(next);
							next = next.getNextSibling();
						}
					}
				} else {
					formatRemoveEmpty(item);
				}
			}
		}
	}

	protected void fireModelEvent(Notification n) {
		if (!listeners.isEmpty()) {
			for (ModelBuildListener listener : listeners) {
				listener.notifyChanged(n);
			}
		}
	}

	/**
	 * @see org.eclipse.e4.xwt.tools.ui.designer.core.editor.soyatec.IModelBuilder.ve.editor.model.IDiagramModelBuilder#addModelBuildListener(org.eclipse.e4.xwt.tools.ui.designer.core.editor.soyatec.ModelBuildListener.ve.editor.model.IModelChangeListener)
	 */
	public void addModelBuildListener(ModelBuildListener listener) {
		if (listeners == null) {
			listeners = new ArrayList<ModelBuildListener>();
		}
		listeners.add(listener);
	}

	/**
	 * @see org.eclipse.e4.xwt.tools.ui.designer.core.editor.soyatec.IModelBuilder.ve.editor.model.IDiagramModelBuilder#removeModelBuildListener(org.eclipse.e4.xwt.tools.ui.designer.core.editor.soyatec.ModelBuildListener.ve.editor.model.IModelChangeListener)
	 */
	public void removeModelBuildListener(ModelBuildListener listener) {
		if (listeners == null) {
			return;
		}
		listeners.remove(listener);
	}

	protected boolean contains(Node parent, Attr attr) {
		NamedNodeMap attributes = parent.getAttributes();
		for (int i = attributes.getLength() - 1; i >= 0; i--) {
			if (attr == attributes.item(i)) {
				return true;
			}
		}

		return false;
	}

	public void reload() {
		if (buildingType == RELOADING || buildingType == BUILDING_MODEL
				|| !isValidThread()) {
			return;
		}
		if (document == null || jfaceDom == null) {
			return;
		}
		IDOMDocument textDocument = getTextDocument(jfaceDom);
		if (textDocument == null) {
			return;
		}
		buildingType = RELOADING;
		IDOMElement textElement = (IDOMElement) textDocument
				.getDocumentElement();
		loadingModel(textElement, null);
		buildingType = NONE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.soyatec.xaml.ve.editor.IDiagramModelBuilder#dispose()
	 */
	public void dispose() {
		doSave(null);// save to cache;
		document = null;
		jfaceDom = null;
		mapper.clear();
	}

	private Display getDisplay() {
		Display display = Display.getCurrent();
		if (display == null || PlatformUI.isWorkbenchRunning()) {
			display = PlatformUI.getWorkbench().getDisplay();
		}
		return display;
	}

	protected boolean isValidThread() {
		return getDisplay() == null
				|| getDisplay().getThread() == Thread.currentThread();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.soyatec.xaml.ve.editor.IDiagramModelBuilder#saveCache()
	 */
	public void doSave(IProgressMonitor monitor) {
		ModelCacheUtility.doSaveCache(document, input, monitor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.soyatec.xaml.ve.editor.IDiagramModelBuilder#hasListener(org.soyatec
	 * .xaml.ve.editor.IModelChangeListener)
	 */
	public boolean hasListener(ModelBuildListener listener) {
		if (listeners == null) {
			return false;
		}
		return listeners.contains(listener);
	}

	public XamlNode getModel(Object textNode) {
		return mapper.getModel(textNode);
	}

	public IDOMNode getTextNode(Object model) {
		return mapper.getTextNode(model);
	}

	private class NodeAdapter implements INodeAdapter {

		public boolean isAdapterForType(Object type) {
			return type == NodeAdapter.class;
		}

		public void notifyChanged(INodeNotifier notifier, int eventType,
				Object changedFeature, Object oldValue, Object newValue, int pos) {
			if (buildingType == BUILDING_MODEL) {
				return;
			}
			buildingType = BUILDING_TEXT;
			if (hasProblems()) {
				buildingType = NONE;
				return;
			}
			XamlNode parentNode = mapper.getModel(notifier);
			XamlNode changedNode = mapper.getModel(changedFeature);
			if (eventType == INodeNotifier.CHANGE && changedFeature != null) {
				if (changedNode != null && newValue == null) {
					// remove.
					parentNode.getAttributes().remove(changedNode);
				} else if (parentNode != null
						&& changedFeature instanceof IDOMAttr) {
					createAttribute(parentNode, (IDOMAttr) changedFeature, -1);
				}
				buildingType = NONE;
			} else if (eventType == INodeNotifier.REMOVE && parentNode != null
					&& changedNode != null) {
				if (changedNode instanceof XamlElement) {
					parentNode.getChildNodes().remove(changedNode);
				} else if (changedNode instanceof XamlAttribute) {
					parentNode.getAttributes().remove(changedNode);
				}
				buildingType = NONE;
			} else if (eventType == INodeNotifier.STRUCTURE_CHANGED) {
				IJobManager jobManager = Job.getJobManager();
				Job currentJob = jobManager.currentJob();
				if (!(currentJob instanceof ReloadJob)
						&& (reloadJob == null || reloadJob.getResult() != null)) {
					reloadJob = new ReloadJob();
					reloadJob.setRule(new ISchedulingRule() {
						public boolean contains(ISchedulingRule rule) {
							return getClass() == rule.getClass();
						}

						public boolean isConflicting(ISchedulingRule rule) {
							return getClass() == rule.getClass();
						}
					});
					reloadJob.schedule(200);
				}
			}
		}

	}

	private class ReloadJob extends UIJob {
		public ReloadJob() {
			super("Reload");
			setDisplay(getDisplay());
			setPriority(SHORT);
			setSystem(true);
		}

		public IStatus runInUIThread(IProgressMonitor monitor) {
			reload();
			return Status.OK_STATUS;
		}
	}

}
