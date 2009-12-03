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
package org.eclipse.e4.xwt.tools.ui.xaml.impl;

import org.eclipse.e4.xwt.tools.ui.xaml.Comment;
import org.eclipse.e4.xwt.tools.ui.xaml.XamlNode;
import org.eclipse.e4.xwt.tools.ui.xaml.XamlPackage;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Comment</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.e4.xwt.tools.ui.xaml.impl.CommentImpl#getContent <em>Content</em>}</li>
 * <li>{@link org.eclipse.e4.xwt.tools.ui.xaml.impl.CommentImpl#getPrev <em>Prev</em>}</li>
 * <li>{@link org.eclipse.e4.xwt.tools.ui.xaml.impl.CommentImpl#getNext <em>Next</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class CommentImpl extends EObjectImpl implements Comment {
	/**
	 * The default value of the '{@link #getContent() <em>Content</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getContent()
	 * @generated
	 * @ordered
	 */
	protected static final String CONTENT_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getContent() <em>Content</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getContent()
	 * @generated
	 * @ordered
	 */
	protected String content = CONTENT_EDEFAULT;

	/**
	 * The cached value of the '{@link #getPrev() <em>Prev</em>}' containment reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getPrev()
	 * @generated
	 * @ordered
	 */
	protected XamlNode prev;

	/**
	 * The cached value of the '{@link #getNext() <em>Next</em>}' containment reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getNext()
	 * @generated
	 * @ordered
	 */
	protected XamlNode next;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected CommentImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return XamlPackage.Literals.COMMENT;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public String getContent() {
		return content;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setContent(String newContent) {
		String oldContent = content;
		content = newContent;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, XamlPackage.COMMENT__CONTENT, oldContent, content));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public XamlNode getPrev() {
		return prev;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public NotificationChain basicSetPrev(XamlNode newPrev, NotificationChain msgs) {
		XamlNode oldPrev = prev;
		prev = newPrev;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, XamlPackage.COMMENT__PREV, oldPrev, newPrev);
			if (msgs == null)
				msgs = notification;
			else
				msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setPrev(XamlNode newPrev) {
		if (newPrev != prev) {
			NotificationChain msgs = null;
			if (prev != null)
				msgs = ((InternalEObject) prev).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - XamlPackage.COMMENT__PREV, null, msgs);
			if (newPrev != null)
				msgs = ((InternalEObject) newPrev).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - XamlPackage.COMMENT__PREV, null, msgs);
			msgs = basicSetPrev(newPrev, msgs);
			if (msgs != null)
				msgs.dispatch();
		} else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, XamlPackage.COMMENT__PREV, newPrev, newPrev));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public XamlNode getNext() {
		return next;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public NotificationChain basicSetNext(XamlNode newNext, NotificationChain msgs) {
		XamlNode oldNext = next;
		next = newNext;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, XamlPackage.COMMENT__NEXT, oldNext, newNext);
			if (msgs == null)
				msgs = notification;
			else
				msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setNext(XamlNode newNext) {
		if (newNext != next) {
			NotificationChain msgs = null;
			if (next != null)
				msgs = ((InternalEObject) next).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - XamlPackage.COMMENT__NEXT, null, msgs);
			if (newNext != null)
				msgs = ((InternalEObject) newNext).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - XamlPackage.COMMENT__NEXT, null, msgs);
			msgs = basicSetNext(newNext, msgs);
			if (msgs != null)
				msgs.dispatch();
		} else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, XamlPackage.COMMENT__NEXT, newNext, newNext));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
		case XamlPackage.COMMENT__PREV:
			return basicSetPrev(null, msgs);
		case XamlPackage.COMMENT__NEXT:
			return basicSetNext(null, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
		case XamlPackage.COMMENT__CONTENT:
			return getContent();
		case XamlPackage.COMMENT__PREV:
			return getPrev();
		case XamlPackage.COMMENT__NEXT:
			return getNext();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
		case XamlPackage.COMMENT__CONTENT:
			setContent((String) newValue);
			return;
		case XamlPackage.COMMENT__PREV:
			setPrev((XamlNode) newValue);
			return;
		case XamlPackage.COMMENT__NEXT:
			setNext((XamlNode) newValue);
			return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
		case XamlPackage.COMMENT__CONTENT:
			setContent(CONTENT_EDEFAULT);
			return;
		case XamlPackage.COMMENT__PREV:
			setPrev((XamlNode) null);
			return;
		case XamlPackage.COMMENT__NEXT:
			setNext((XamlNode) null);
			return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
		case XamlPackage.COMMENT__CONTENT:
			return CONTENT_EDEFAULT == null ? content != null : !CONTENT_EDEFAULT.equals(content);
		case XamlPackage.COMMENT__PREV:
			return prev != null;
		case XamlPackage.COMMENT__NEXT:
			return next != null;
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy())
			return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (content: ");
		result.append(content);
		result.append(')');
		return result.toString();
	}

} // CommentImpl
