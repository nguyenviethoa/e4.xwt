/*******************************************************************************
 * Copyright (c) 2006, 2008 Soyatec (http://www.soyatec.com) and others.       *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *                                                                             *  
 * Contributors:                                                               *        
 *     Soyatec - initial API and implementation                                *
 *******************************************************************************/
package org.eclipse.e4.xwt.dataproviders.impl;

import java.io.ByteArrayInputStream;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.eclipse.e4.xwt.dataproviders.IXmlDataProvider;
import org.w3c.dom.Document;

/**
 * @author jliu (jin.liu@soyatec.com)
 */
public class XmlDataProvider extends AbstractDataProvider implements IXmlDataProvider {

	private URL source;

	private String xPath;

	private String xDataContent;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.dataproviders.IXmlDataProvider#getDocument()
	 */
	public Document getDocument() {
		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		domFactory.setNamespaceAware(true); // never forget this!

		DocumentBuilder builder = null;
		try {
			builder = domFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			return null;
		}
		if (source != null) {
			try {
				return builder.parse(source.openStream());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (xDataContent != null) {
			try {
				return builder.parse(new ByteArrayInputStream(xDataContent.getBytes()));
			} catch (Exception e) {
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.dataproviders.IXmlDataProvider#getSource()
	 */
	public URL getSource() {
		return source;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.dataproviders.IXmlDataProvider#getXPath()
	 */
	public String getXPath() {
		return xPath;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.dataproviders.IXmlDataProvider#setSource(java.io.InputStream)
	 */
	public void setSource(URL xmlSource) {
		this.source = xmlSource;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.dataproviders.IXmlDataProvider#setXPath(java.lang.String)
	 */
	public void setXPath(String xPath) {
		this.xPath = xPath;
	}

	private Object getRoot() {
		Document doc = getDocument();
		if (doc == null) {
			return null;
		}
		if (xPath != null) {
			try {
				XPathFactory factory = XPathFactory.newInstance();
				XPath xpath = factory.newXPath();
				XPathExpression expr = xpath.compile(xPath);
				return expr.evaluate(doc, XPathConstants.NODE);
			} catch (XPathExpressionException e) {
				e.printStackTrace();
			}
		}
		return doc;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.dataproviders.IDataProvider#getData()
	 */
	public Object getData(String path) {
		Object root = getRoot();
		if (root == null || path == null) {
			return null;
		}
		try {
			XPathFactory factory = XPathFactory.newInstance();
			XPath xpath = factory.newXPath();
			XPathExpression expr = xpath.compile(path);
			String evaluate = expr.evaluate(root);
			return evaluate;
		} catch (XPathExpressionException e) {
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.dataproviders.IXmlDataProvider#getXDataContent()
	 */
	public String getXDataContent() {
		return xDataContent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.dataproviders.IXmlDataProvider#setXDataContent(java.lang.String)
	 */
	public void setXDataContent(String content) {
		this.xDataContent = content;
	}

}
