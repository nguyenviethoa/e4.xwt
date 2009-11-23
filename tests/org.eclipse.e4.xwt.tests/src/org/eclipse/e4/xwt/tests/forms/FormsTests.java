package org.eclipse.e4.xwt.tests.forms;

import java.net.URL;

import org.eclipse.e4.xwt.IConstants;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class FormsTests extends FormTestCase {	
	public void testSection() throws Exception {
		URL url = FormsTests.class
				.getResource(Section.class
						.getSimpleName()
						+ IConstants.XWT_EXTENSION_SUFFIX);
		runTest(url, new Runnable() {
			public void run() {
				checkVisibility("Section", org.eclipse.ui.forms.widgets.Section.class);
				checkVisibility("Section.Label", Label.class);
			}
		});
	}

	public void testForm_Label() throws Exception {
		URL url = FormsTests.class
				.getResource(Form_Label.class
						.getSimpleName()
						+ IConstants.XWT_EXTENSION_SUFFIX);
		runTest(url, new Runnable() {
			public void run() {
				checkVisibility("Form", org.eclipse.ui.forms.widgets.Form.class);
				checkVisibility("Form.Label", Label.class);
			}
		});
	}

	public void testForm_Button() throws Exception {
		URL url = FormsTests.class
				.getResource(Form_Button.class
						.getSimpleName()
						+ IConstants.XWT_EXTENSION_SUFFIX);
		runTest(url, new Runnable() {
			public void run() {
				checkVisibility("Form", org.eclipse.ui.forms.widgets.Form.class);
				checkVisibility("Form.Button", Button.class);
			}
		});
	}

	public void testForm_LabelButton() throws Exception {
		URL url = FormsTests.class
				.getResource(Form_LabelButton.class
						.getSimpleName()
						+ IConstants.XWT_EXTENSION_SUFFIX);
		runTest(url, new Runnable() {
			public void run() {
				checkVisibility("Form", org.eclipse.ui.forms.widgets.Form.class);
				checkVisibility("Form.Label", Label.class);
				checkVisibility("Form.Button", Button.class);
			}
		});
	}

	public void testForm_Section() throws Exception {
		URL url = FormsTests.class
				.getResource(Form_Section.class
						.getSimpleName()
						+ IConstants.XWT_EXTENSION_SUFFIX);
		runTest(url, new Runnable() {
			public void run() {
				checkVisibility("Form", org.eclipse.ui.forms.widgets.Form.class);
				checkVisibility("Form.Section", org.eclipse.ui.forms.widgets.Section.class);
				checkVisibility("Form.Section.Label", Label.class);
			}
		});
	}
	
	public void testForm_ButtonSection() throws Exception {
		URL url = FormsTests.class
				.getResource(Form_ButtonSection.class
						.getSimpleName()
						+ IConstants.XWT_EXTENSION_SUFFIX);
		runTest(url, new Runnable() {
			public void run() {
				checkVisibility("Form", org.eclipse.ui.forms.widgets.Form.class);
				checkVisibility("Form.Button", Button.class);
				checkVisibility("Form.Section", org.eclipse.ui.forms.widgets.Section.class);
				checkVisibility("Form.Section.Label", Label.class);
			}
		});
	}
	

	public void testScrolledForm() throws Exception {
		URL url = FormsTests.class
				.getResource(ScrolledForm.class
						.getSimpleName()
						+ IConstants.XWT_EXTENSION_SUFFIX);
		runTest(url, new Runnable() {
			public void run() {
				checkVisibility("ScrolledForm", org.eclipse.ui.forms.widgets.ScrolledForm.class);
				checkVisibility("ScrolledForm.SashForm", SashForm.class);
				checkVisibility("ScrolledForm.SashForm.Section1", org.eclipse.ui.forms.widgets.Section.class);
				checkVisibility("ScrolledForm.SashForm.Section1.Composite", Composite.class);
				checkVisibility("ScrolledForm.SashForm.Section1.Composite.Label", Label.class);
				checkVisibility("ScrolledForm.SashForm.Section1.Composite.Text", Text.class);
				checkVisibility("ScrolledForm.SashForm.Section2", org.eclipse.ui.forms.widgets.Section.class);
				checkVisibility("ScrolledForm.SashForm.Section2.Label", Label.class);
				checkVisibility("ScrolledForm.Label", Label.class);
				checkChildren("ScrolledForm", 1);
				checkChildren("ScrolledForm", "Body", 2);
			}
		});
	}
}
