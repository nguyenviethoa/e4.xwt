package org.eclipse.e4.xwt.tests.databinding.status;

import org.eclipse.e4.xwt.tests.BaseBean;

public class Domain extends BaseBean {

	private String valueOne = "1";
	private String valueTwo = "2";

	public String getValueOne() {
		return valueOne;
	}

	public void setValueOne(String valueOne) {
		String oldValue = this.valueOne;
		this.valueOne = valueOne;
		changeSupport.firePropertyChange("valueOne", oldValue, this.valueOne);
	}

	public String getValueTwo() {
		return valueTwo;
	}

	public void setValueTwo(String valueTwo) {
		String oldValue = this.valueTwo;
		this.valueTwo = valueTwo;
		changeSupport.firePropertyChange("valueTwo", oldValue, this.valueTwo);
	}
}
