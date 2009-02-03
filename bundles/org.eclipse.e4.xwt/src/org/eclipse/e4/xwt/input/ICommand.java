package org.eclipse.e4.xwt.input;

public interface ICommand {
	boolean canExecute();

	void execute(Object parameter);

	void addCanExecuteChangedListener(CanExecuteChangedListener listener);

	void removeCanExecuteChangedListener(CanExecuteChangedListener listener);
}
