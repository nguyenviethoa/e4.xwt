package org.eclipse.e4.xwt.tests.controls.tree;

import java.net.URL;

import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.XWT;

/**
 * @author jliu
 */
public class Tree_Test2 {
	public static void main(String[] args) {

		URL url = Tree_Test2.class.getResource(Tree_Test2.class.getSimpleName()
				+ IConstants.XWT_EXTENSION_SUFFIX);
		try {
			XWT.open(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
