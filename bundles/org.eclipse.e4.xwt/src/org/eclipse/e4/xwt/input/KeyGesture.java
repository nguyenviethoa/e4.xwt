package org.eclipse.e4.xwt.input;

public class KeyGesture {
	protected String key;
	protected String displayString;
	protected ModifierKeys modifiers;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getDisplayString() {
		return displayString;
	}

	public void setDisplayString(String displayString) {
		this.displayString = displayString;
	}

	public ModifierKeys getModifiers() {
		return modifiers;
	}

	public void setModifiers(ModifierKeys modifiers) {
		this.modifiers = modifiers;
	}
}
