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
package org.eclipse.e4.tools.ui.designer.commands;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class EObjectDeleteCommand extends AbstractDeleteCommand {

	private EObject contanier;
	private EStructuralFeature containmentSF;

	public EObjectDeleteCommand(EObject target) {
		super(target);
	}

	protected void doExecute() {
		contanier = target.eContainer();
		containmentSF = target.eContainingFeature();
		EcoreUtil.remove(target);
	}

	protected void doUndo() {
		target.eSet(containmentSF, contanier);
	}

}
