package org.eclipse.e4.xwt.input;

public class InputBinding {
	protected KeyGesture gesture;
	protected Object commandTarget;
	protected Object commandParameter;
	protected ICommand command;

	public Object getCommandTarget() {
		return commandTarget;
	}

	public void setCommandTarget(Object commandTarget) {
		this.commandTarget = commandTarget;
	}

	public Object getCommandParameter() {
		return commandParameter;
	}

	public void setCommandParameter(Object commandParameter) {
		this.commandParameter = commandParameter;
	}

	public ICommand getCommand() {
		return command;
	}

	public void setCommand(ICommand command) {
		this.command = command;
	}

	public KeyGesture getGesture() {
		return gesture;
	}

	public void setGesture(KeyGesture gesture) {
		this.gesture = gesture;
	}
}
