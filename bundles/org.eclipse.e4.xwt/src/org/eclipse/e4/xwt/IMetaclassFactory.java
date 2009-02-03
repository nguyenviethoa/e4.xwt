package org.eclipse.e4.xwt;

import org.eclipse.e4.xwt.metadata.IMetaclass;

/**
 * Metaclass factory provides a customizable Metaclass creation mechanism.
 * 
 * @author yyang
 * 
 */
public interface IMetaclassFactory {
	boolean isFactoryOf(Class<?> type);

	IMetaclass create(Class<?> type, IMetaclass superMetaclass);
}
