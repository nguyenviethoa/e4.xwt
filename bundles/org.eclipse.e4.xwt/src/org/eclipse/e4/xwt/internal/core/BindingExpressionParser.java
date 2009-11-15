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
package org.eclipse.e4.xwt.internal.core;

import java.util.ArrayList;
import java.util.Stack;

import org.eclipse.e4.xwt.XWTException;

public class BindingExpressionParser {
	
	public static int lastIndexOf(String value) {
		int level = 0;
		Stack<Character> separators = new Stack<Character>();
		int start = 0;
		char[] array = value.toCharArray();
		for (int i = array.length - 1; i >= 0 ; i--) {
			switch (array[i]) {
			case '{':
				{
					Character character = separators.pop();
					if (character.charValue() != '}') {
						throw new XWTException("Syntax error is binding expression " + value + " at " + i);
					}
				}
				level --;
				break;
			case '[':
				{
					Character character = separators.pop();
					if (character.charValue() != '}') {
						throw new XWTException("Syntax error is binding expression " + value + " at " + i);
					}
				}
				level --;
				break;
			case '(':
				{
					Character character = separators.pop();
					if (character.charValue() != ')') {
						throw new XWTException("Syntax error is binding expression " + value + " at " + i);
					}
				}
				level --;
				break;
			case '}':
			case ']':
			case ')':
				separators.push(array[i]);
				level ++;
				break;
			case '.':
				if (level == 0) {
					return i;
				}
				break;
			}
			
			if (array[i] == '.') {
			}
		}
		return -1;
	}

	
	public static ArrayList<String> splitRoots(String value) {
		int level = 0;
		Stack<Character> separators = new Stack<Character>();
		ArrayList<String> collector = new ArrayList<String>();
		int start = 0;
		char[] array = value.toCharArray();
		for (int i = 0; i < array.length; i++) {
			switch (array[i]) {
			case '}':
				{
					Character character = separators.pop();
					if (character.charValue() != '{') {
						throw new XWTException("Syntax error is binding expression " + value + " at " + i);
					}
				}
				level --;
				break;
			case ']':
				{
					Character character = separators.pop();
					if (character.charValue() != '[') {
						throw new XWTException("Syntax error is binding expression " + value + " at " + i);
					}
				}
				level --;
				break;
			case ')':
				{
					Character character = separators.pop();
					if (character.charValue() != '(') {
						throw new XWTException("Syntax error is binding expression " + value + " at " + i);
					}
				}
				level --;
				break;
			case '{':
			case '[':
			case '(':
				separators.push(array[i]);
				level ++;
				break;
			case '.':
				if (level == 0) {
					collector.add(value.substring(start, i));
					start = i + 1;
				}
				break;
			}
			
			if (array[i] == '.') {
			}
		}
		if (level == 0) {
			collector.add(value.substring(start, array.length));
		}
		return collector;
	}
}
