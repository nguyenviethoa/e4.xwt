package org.eclipse.e4.xwt.core;

import java.util.ArrayList;
import java.util.Collection;


public class Trigger extends TriggerBase {
	protected String property;
	protected String sourceName;
	protected Object value;
	protected Collection<Setter> setters = new ArrayList<Setter>();

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public String getSourceName() {
		return sourceName;
	}

	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public Collection<Setter> getSetters() {
		return setters;
	}

	public void setSetters(Collection<Setter> setters) {
		this.setters = setters;
	}
}
