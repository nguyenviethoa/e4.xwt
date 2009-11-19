/**
 * 
 */
package org.eclipse.e4.xwt.emf;

import org.eclipse.e4.xwt.XWTException;
import org.eclipse.e4.xwt.IDataProvider.DataModelService;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

public class EMFDataModelService implements DataModelService {

	public Object toModelType(Object data) {
		return EMFHelper.toType(data);
	}

	public Object loadModelType(String className) {
		throw new UnsupportedOperationException();
	}

	public Object toModelPropertyType(Object object, String propertyName) {
		EClass type = (EClass) object;
		EStructuralFeature structuralFeature = type
				.getEStructuralFeature(propertyName);

		if (structuralFeature == null) {
			throw new XWTException(" Property \"" + propertyName
					+ "\" is not found in the class " + type.getName());
		}
		return structuralFeature.getEType();
	}
}