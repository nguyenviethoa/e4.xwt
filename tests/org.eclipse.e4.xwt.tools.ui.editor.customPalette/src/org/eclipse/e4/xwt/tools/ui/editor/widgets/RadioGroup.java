/*******************************************************************************
 * Copyright (c) 2006, 2008 Soyatec (http://www.soyatec.com) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Anaik Trihoreau <anaik@anyware-tech.com> - Bug 274057
 *     Yves YANG <yves.yang@soyatec.com> - integration and improvement
 *******************************************************************************/
package org.eclipse.e4.xwt.tools.ui.editor.widgets;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * This widget allows to handle group for radio buttons
 * 
 * @author Anaik
 * 
 */
public class RadioGroup extends Composite
{
	/**
	 * This value is not really used, but when parsing <RadioGroup input="{Binding path=...}">, the
	 * binding is created when this setInput is called.
	 */
	private Object _value = null;

	/**
	 * @param parent
	 * @param style
	 */
	public RadioGroup(Composite parent, int style)
	{
		super(parent, style);
	}

	public Object getInput()
	{
		return _value;
	}

	public void setInput(Object object)
	{
		_value = object;
	}

	public List<Button> getRadioButtons()
	{
		List<Button> result = new ArrayList<Button>();
		for (Control child: this.getChildren())
		{
			boolean isRadioButton = (child instanceof Button)
				&& ((((Button) child).getStyle() & SWT.RADIO) != 0);
			if (isRadioButton)
			{
				result.add((Button) child);
			}
		}
		return result;
	}

}
