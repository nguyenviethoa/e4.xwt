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
package org.eclipse.e4.xwt.tools.ui.designer.core.editor.outline.dnd;

import org.eclipse.e4.xwt.tools.ui.xaml.XamlNode;
import org.eclipse.gef.commands.Command;
import org.eclipse.jface.viewers.ISelection;

/**
 * @author jliu (jin.liu@soyatec.com)
 */
public interface OutlineDropManager {

	/**
	 * Create a command for moving 'source' object after 'target' object.
	 * 
	 * @param source
	 * @param target
	 * @return command of dragging from outline.
	 */
	Command getMoveAfter(Object source, Object target);

	/**
	 * Create a command for moving 'source' object before 'target' object.
	 * 
	 * @param source
	 * @param target
	 * @return command of dragging from outline.
	 */
	Command getMoveBefore(Object source, Object target);

	/**
	 * Create a command for moving 'source' object on 'target' object.
	 * 
	 * @param source
	 * @param target
	 * @return command of dragging from outline.
	 */
	Command getMoveOn(Object source, Object target);

	/**
	 * Execute given command with commandStack or not.
	 * 
	 * @param command
	 */
	boolean execute(Command command);

	/**
	 * Create source from the selection of Drag Transfer.
	 * 
	 * @param selection
	 * @return
	 */
	XamlNode getSource(ISelection selection);
}
