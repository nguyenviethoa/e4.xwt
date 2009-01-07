package org.eclipse.e4.xwt.vex.palette.part;


import org.eclipse.e4.xwt.vex.PalletteSelectionTransfer;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.dnd.AbstractTransferDropTargetListener;
import org.eclipse.swt.dnd.DropTargetEvent;

public class ToolTransferDropTargetListener extends
		AbstractTransferDropTargetListener {

	public ToolTransferDropTargetListener(EditPartViewer viewer) {
		super(viewer, PalletteSelectionTransfer.getInstance());
		setEnablementDeterminedByCommand(false);
	}

	@Override
	protected void updateTargetRequest() {

	}

	@Override
	public boolean isEnabled(DropTargetEvent event) {
		System.out.println(event.toString());
		return true;
//		return super.isEnabled(event);
	}
	
	
}
