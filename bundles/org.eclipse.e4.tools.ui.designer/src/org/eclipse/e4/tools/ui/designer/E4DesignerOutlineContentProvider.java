package org.eclipse.e4.tools.ui.designer;

import java.util.List;

import org.eclipse.e4.xwt.tools.ui.designer.core.editor.outline.OutlineContentProvider;
import org.eclipse.e4.xwt.tools.ui.designer.core.parts.root.DesignerRootEditPart;
import org.eclipse.gef.EditPart;

public class E4DesignerOutlineContentProvider extends OutlineContentProvider {

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof DesignerRootEditPart) {
			DesignerRootEditPart editPart = (DesignerRootEditPart) parentElement;
			List<EditPart> list = editPart.getChildren();
			if (list == null || list.isEmpty()) {
				return EMPTY;
			}
			parentElement = list.get(0);
		}
		return super.getChildren(parentElement);
	}
}
