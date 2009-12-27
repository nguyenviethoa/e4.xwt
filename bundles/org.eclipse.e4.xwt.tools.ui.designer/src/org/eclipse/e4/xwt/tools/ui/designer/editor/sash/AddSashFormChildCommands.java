package org.eclipse.e4.xwt.tools.ui.designer.editor.sash;

import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.tools.ui.designer.commands.AddNewChildCommand;
import org.eclipse.e4.xwt.tools.ui.designer.core.util.SashUtil;
import org.eclipse.e4.xwt.tools.ui.designer.parts.SashFormEditPart;
import org.eclipse.e4.xwt.tools.ui.xaml.XamlAttribute;
import org.eclipse.e4.xwt.tools.ui.xaml.XamlNode;
import org.eclipse.gef.EditPart;
import org.eclipse.swt.custom.SashForm;

public class AddSashFormChildCommands extends AddNewChildCommand {
	public static final String WIEGHTS_ATTR = "weights";
	private EditPart host;
	private int[] oldWeights;
	private boolean after;

	public AddSashFormChildCommands(EditPart parent, XamlNode child) {
		this(parent, child, -1, false);
	}

	public AddSashFormChildCommands(EditPart parent, XamlNode child, int index) {
		this(parent, child, index, false);
	}

	public AddSashFormChildCommands(EditPart parent, XamlNode child, int index, boolean after) {
		super((XamlNode)parent.getModel(),  child, index);
		this.host = parent;
		this.after = after;
	}
	
	@Override
	public boolean canExecute() {
		return super.canExecute() && host instanceof SashFormEditPart;
	}
	
	@Override
	public void execute() {
		XamlNode sashForm = (XamlNode) host.getModel();
		SashFormEditPart sashFormEditPart = (SashFormEditPart) host;
		SashForm form = (SashForm) sashFormEditPart.getWidget();
		oldWeights = form.getWeights();
		
		// TODO: EMF transaction framework should be used here. 
		try {
			super.execute();
		} catch (Exception e) {
		}

		int[] weights = new int[oldWeights.length + 1];
		int index = getIndex();
		if (index == -1) {
			index = oldWeights.length - 1;
		}
		else if (after) {
			index--;
		}
		for (int i = 0; i <= index; i++) {
			weights[i] = oldWeights[i];
		}
		int part = weights[index]/2;
		weights[index+1] = weights[index] - part;
		weights[index] = part;
		for (int i = index+1; i < oldWeights.length; i++) {
			weights[i+1] = oldWeights[i];
		}
		
		String value = SashUtil.weightsValue(weights);
		XamlAttribute attribute = sashForm.getAttribute(WIEGHTS_ATTR, IConstants.XWT_NAMESPACE);
		attribute.setValue(value);
	}
	
	@Override
	public boolean canUndo() {
		return super.canUndo() && oldWeights != null;
	}
	
	@Override
	public void undo() {
		super.undo();
		XamlNode sashForm = (XamlNode) host.getModel();
		String value = SashUtil.weightsDisplayString(oldWeights);
		XamlAttribute attribute = sashForm.getAttribute(WIEGHTS_ATTR, IConstants.XWT_NAMESPACE);
		attribute.setValue(value);
	}
}
