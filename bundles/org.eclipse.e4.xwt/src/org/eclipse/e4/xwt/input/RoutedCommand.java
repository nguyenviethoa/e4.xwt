package org.eclipse.e4.xwt.input;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;

public class RoutedCommand implements ICommand {
	protected PropertyChangeSupport canExecuteSupport = new PropertyChangeSupport(this);

	public boolean canExecute() {
		return true;
	}

	public void execute(Object parameter) {
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
}
