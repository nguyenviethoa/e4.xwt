package org.eclipse.e4.xwt.input;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;

public class CommandBinding {
	protected PropertyChangeSupport canExecuteSupport = new PropertyChangeSupport(this);
	protected PropertyChangeSupport executeSupport = new PropertyChangeSupport(this);

	protected ICommand command;

	public ICommand getCommand() {
		return command;
	}

	public CommandBinding() {
	}

	public CommandBinding(ICommand command) {
		this.command = command;
	}

	public void setCommand(ICommand command) {
		this.command = command;
	}

	public void addCanExecuteChangedListener(CanExecuteChangedListener listener) {
		canExecuteSupport.addPropertyChangeListener(listener);
	}

	public void removeCanExecuteChangedListener(CanExecuteChangedListener listener) {
		canExecuteSupport.removePropertyChangeListener(listener);
	}

	public void fireCanExecuteChangedListener(PropertyChangeEvent args) {
		canExecuteSupport.firePropertyChange(args);
	}

	public void addExecuteChangedListener(CanExecuteChangedListener listener) {
		canExecuteSupport.addPropertyChangeListener(listener);
	}

	public void removeExecuteChangedListener(CanExecuteChangedListener listener) {
		canExecuteSupport.removePropertyChangeListener(listener);
	}

	public void fireExecuteChangedListener(PropertyChangeEvent args) {
		canExecuteSupport.firePropertyChange(args);
	}
}
