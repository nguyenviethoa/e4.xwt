package org.eclipse.e4.xwt.vex.palette.part;

import org.eclipse.gef.EditDomain;
import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.gef.ui.parts.PaletteViewerKeyHandler;

public class DynamicPaletteViewer extends PaletteViewer {

	public DynamicPaletteViewer() {
		setKeyHandler(new PaletteViewerKeyHandler(this));
		setEditPartFactory(new ToolPaletteEditPartFactory());

	}

}
