package org.eclipse.e4.xwt.tools.ui.designer.editor.sash;

import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.internal.utils.LoggerManager;
import org.eclipse.e4.xwt.metadata.IMetaclass;
import org.eclipse.e4.xwt.tools.ui.designer.commands.AddNewChildCommand;
import org.eclipse.e4.xwt.tools.ui.designer.core.util.SashUtil;
import org.eclipse.e4.xwt.tools.ui.designer.parts.SashEditPart;
import org.eclipse.e4.xwt.tools.ui.designer.parts.SashFormEditPart;
import org.eclipse.e4.xwt.tools.ui.xaml.XamlAttribute;
import org.eclipse.e4.xwt.tools.ui.xaml.XamlFactory;
import org.eclipse.e4.xwt.tools.ui.xaml.XamlNode;
import org.eclipse.gef.EditPart;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.Sash;

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
		boolean result = super.canExecute() && host instanceof SashFormEditPart;
		if (!result) {
			return false;
		}
		XamlNode child = getChild();
		String name = child.getName();
		String ns = child.getNamespace();
		IMetaclass metaclass = XWT.getMetaclass(name, ns);
		return metaclass.getType() != Sash.class;
	}
	
	@Override
	public void execute() {
		XamlNode sashForm = (XamlNode) host.getModel();
		SashFormEditPart sashFormEditPart = (SashFormEditPart) host;
		SashForm form = (SashForm) sashFormEditPart.getWidget();
		oldWeights = form.getWeights();
		
		int children = 0;
		
		for (Object child : sashFormEditPart.getChildren()) {
			if (!(child instanceof SashEditPart)) {
				children ++;
			}
		}
		
		if (children == 0) {
			try {
				// add in the list first
				super.execute();
			} catch (Exception e) {
				LoggerManager.log(e);
			}
			return;
		}

		int[] weights = new int[children + 1];
		int sum = 0;
		if (oldWeights.length != 0) {
			for (int i = 0; i < oldWeights.length; i++) {
				weights[i] = oldWeights[i];
				sum += oldWeights[i];
			}
			if (children > oldWeights.length) {
				int delta = sum/(children - oldWeights.length);				
				for (int i = oldWeights.length; i < children -1; i++) {
					weights[i] = delta;
				}
				weights[children -1] = sum - (delta*(children - oldWeights.length));
			}
			
			int index = getIndex();
			if (index == -1) {
				index = children - 1;
			}
			else if (after) {
				index--;
			}
			
			int part1 = weights[index]/2;
			int part2 = weights[index] - part1;
			
			for (int i = children - 1; i > index; i--) {
				weights[i+1] = oldWeights[i];
			}
			weights[index] = part1;
			weights[index+1] = part2;
		}
		else {
			int delta = 1000/weights.length;
			for (int i = 0; i < weights.length - 1; i++) {
				weights[i] = delta;
			}
			weights[weights.length -1] = sum - (delta*(children));			
		}
		
		try {
			// add in the list first
			super.execute();

			if (weights.length > 1) {
				// update the weights after, since the Notifier as update it. Her we just override it.
				String value = SashUtil.weightsValue(weights);
				XamlAttribute attribute = sashForm.getAttribute(WIEGHTS_ATTR, IConstants.XWT_NAMESPACE);
				if (attribute == null) {
					attribute = XamlFactory.eINSTANCE.createAttribute(WIEGHTS_ATTR, IConstants.XWT_NAMESPACE);
					sashForm.getAttributes().add(attribute);
				}
				attribute.setValue(value);
			}
		} catch (Exception e) {
			LoggerManager.log(e);
		}
	}
	
	@Override
	public boolean canUndo() {
		return super.canUndo() && oldWeights != null;
	}
	
	@Override
	public void undo() {
		super.undo();
		XamlNode sashForm = (XamlNode) host.getModel();
		String value = SashUtil.weightsValue(oldWeights);
		XamlAttribute attribute = sashForm.getAttribute(WIEGHTS_ATTR, IConstants.XWT_NAMESPACE);
		attribute.setValue(value);
	}
}
