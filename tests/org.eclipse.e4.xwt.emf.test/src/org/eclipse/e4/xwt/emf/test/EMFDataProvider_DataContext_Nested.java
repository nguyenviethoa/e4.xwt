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
package org.eclipse.e4.xwt.emf.test;

import java.net.URL;

import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.emf.EMFBinding;
import org.eclipse.e4.xwt.emf.test.books.Book;
import org.eclipse.e4.xwt.emf.test.books.BooksFactory;
import org.eclipse.e4.xwt.emf.test.books.Title;

/**
 * @author yyang (yves.yang@soyatec.com)
 */
public class EMFDataProvider_DataContext_Nested {
	public static void main(String[] args) {
		EMFBinding.initialze();
		URL url = EMFDataProvider_DataContext_Nested.class
				.getResource(EMFDataProvider_DataContext_Nested.class
						.getSimpleName()
						+ IConstants.XWT_EXTENSION_SUFFIX);
		try {
			XWT.open(url, createBook());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected static Book createBook() {
		Book harryPotter = BooksFactory.eINSTANCE.createBook();
		Title title = BooksFactory.eINSTANCE.createTitle();
		title.setLan("en");
		title.setText("Harry Potter");
		harryPotter.setTitle(title);
		harryPotter.setAuthor("Neal Stephenson");
		harryPotter.setPrice(29.99);
		harryPotter.setYear(2005);

		return harryPotter;
	}
}
