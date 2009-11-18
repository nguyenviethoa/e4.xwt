package org.eclipse.e4.xwt.tests.jface.tableviewer.master.detail.set;

import java.net.URL;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.tests.XWTTestCase;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

public class JFaceTableViewer_Set_MasterDetail_Tests extends XWTTestCase {

	public void testTableViewer_MasterDetail() throws Exception {
		URL url = JFaceTableViewer_Set_MasterDetail_Tests.class
				.getResource(TableViewer_MasterDetail.class.getSimpleName()
						+ IConstants.XWT_EXTENSION_SUFFIX);
		runTest(url, new Runnable() {
			public void run() {
				Object element = XWT.findElementByName(root, "TableViewer");
				assertTrue(element instanceof TableViewer);
				TableViewer tableViewer = (TableViewer) element;
				TableItem[] items = tableViewer.getTable().getItems();
				assertTrue(items.length == 2);
				if ("Jin".equals(items[1].getText(0))) {
					assertEquals(items[1].getText(1), "27");
					assertEquals(items[0].getText(0), "Thomas");
					assertEquals(items[0].getText(1), "32");
				} else if ("Thomas".equals(items[1].getText(0))) {
					assertEquals(items[1].getText(1), "32");
					assertEquals(items[0].getText(0), "Jin");
					assertEquals(items[0].getText(1), "27");
				}
			}
		});
	}

	public void testTableViewer_MasterDetail_DetailDisplay() throws Exception {
		URL url = JFaceTableViewer_Set_MasterDetail_Tests.class
				.getResource(TableViewer_MasterDetail.class.getSimpleName()
						+ IConstants.XWT_EXTENSION_SUFFIX);
		runTest(url, new Runnable() {
			public void run() {
				Object element = XWT.findElementByName(root, "TableViewer");
				assertTrue(element instanceof TableViewer);
				TableViewer tableViewer = (TableViewer) element;

				IObservableValue observableValue = XWT.findObservableValue(
						root, tableViewer, "singleSelection");

				Company company = (Company) XWT.getDataContext(element);
				Employee employee = company.getEmployees().iterator().next();
				observableValue.setValue(employee);
			}
		}, new Runnable() {
			public void run() {
				checkListViewer();
			}

			public void checkListViewer() {
				Object element = XWT.findElementByName(root, "TableViewer");
				assertTrue(element instanceof TableViewer);
				TableViewer tableViewer = (TableViewer) element;
				TableItem[] items = tableViewer.getTable().getItems();
				assertTrue(items.length == 2);

				Label label = (Label) XWT.findElementByName(root, "Label");
				assertTrue(label != null);

				IObservableValue observableValue = XWT.findObservableValue(
						root, tableViewer, "singleSelection");
				Object selected = observableValue.getValue();
				assertTrue(selected instanceof Employee);
				Employee employee = (Employee) selected;
				assertEquals(label.getText(), employee.getName());
			}
		});
	}

	public void testTableViewer_MasterDetail_DetailEdit() throws Exception {
		final String newName = "Employee new name";
		URL url = JFaceTableViewer_Set_MasterDetail_Tests.class
				.getResource(TableViewer_MasterDetail_Edit.class
						.getSimpleName()
						+ IConstants.XWT_EXTENSION_SUFFIX);
		runTest(url, new Runnable() {
			public void run() {
				Object element = XWT.findElementByName(root, "TableViewer");
				assertTrue(element instanceof TableViewer);
				TableViewer tableViewer = (TableViewer) element;

				IObservableValue observableValue = XWT.findObservableValue(
						root, tableViewer, "singleSelection");

				Company company = (Company) XWT.getDataContext(element);
				Employee employee = company.getEmployees().iterator().next();
				observableValue.setValue(employee);
			}
		}, new Runnable() {
			public void run() {
				Object element = XWT.findElementByName(root, "TableViewer");
				assertTrue(element instanceof TableViewer);
				TableViewer tableViewer = (TableViewer) element;
				TableItem[] items = tableViewer.getTable().getItems();
				assertTrue(items.length == 2);

				Text text = (Text) XWT.findElementByName(root, "Text");
				assertTrue(text != null);

				IObservableValue observableValue = XWT.findObservableValue(
						root, tableViewer, "singleSelection");
				Object selected = observableValue.getValue();
				assertTrue(selected instanceof Employee);
				Employee employee = (Employee) selected;
				assertEquals(text.getText(), employee.getName());

				text.setText(newName);
			}
		}, new Runnable() {
			public void run() {
				Object element = XWT.findElementByName(root, "TableViewer");
				assertTrue(element instanceof TableViewer);
				TableViewer tableViewer = (TableViewer) element;

				IObservableValue observableValue = XWT.findObservableValue(
						root, tableViewer, "singleSelection");
				Object selected = observableValue.getValue();
				assertTrue(selected instanceof Employee);
				Employee employee = (Employee) selected;
				assertEquals(newName, employee.getName());
			}
		});
	}

	public void testTableViewer_MasterDetail_NestedTable() throws Exception {
		URL url = JFaceTableViewer_Set_MasterDetail_Tests.class
				.getResource(TableViewer_MasterDetail_NestedTable.class
						.getSimpleName()
						+ IConstants.XWT_EXTENSION_SUFFIX);
		runTest(url, new Runnable() {
			public void run() {
				// select a table company
				Object element = XWT.findElementByName(root, "IndustryViewer");
				assertTrue(element instanceof TableViewer);
				TableViewer tableViewer = (TableViewer) element;
				TableItem[] items = tableViewer.getTable().getItems();
				assertTrue(items.length == 2);

				Industry industry = (Industry) XWT.getDataContext(element);
				Company company = null;
				for (Company memeber : industry.getMembers()) {
					if ("Soyatec".equals(memeber.getName())) {
						company = memeber;
						break;
					}
				}
				IObservableValue observableValue = XWT.findObservableValue(
						root, tableViewer, "singleSelection");
				observableValue.setValue(company);
			}
		}, new Runnable() {
			public void run() {
				Object element = XWT.findElementByName(root, "CompanyViewer");
				assertTrue(element instanceof TableViewer);
				TableViewer tableViewer = (TableViewer) element;
				TableItem[] items = tableViewer.getTable().getItems();
				assertTrue(items.length == 2);

				if ("Jin".equals(items[1].getText(0))) {
					assertEquals(items[1].getText(1), "27");
					assertEquals(items[0].getText(0), "Thomas");
					assertEquals(items[0].getText(1), "32");
				} else if ("Thomas".equals(items[1].getText(0))) {
					assertEquals(items[1].getText(1), "32");
					assertEquals(items[0].getText(0), "Jin");
					assertEquals(items[0].getText(1), "27");
				}
			}
		});
	}

	public void testTableViewer_MasterDetail_NestedTable_AddInput()
			throws Exception {
		URL url = JFaceTableViewer_Set_MasterDetail_Tests.class
				.getResource(TableViewer_MasterDetail_NestedTable_AddInput.class
						.getSimpleName()
						+ IConstants.XWT_EXTENSION_SUFFIX);
		runTest(url, new Runnable() {
			public void run() {
				// select a table company
				Object element = XWT.findElementByName(root, "AddButton");
				assertTrue(element instanceof Button);
				selectButton((Button) element);
			}
		}, new Runnable() {
			public void run() {
				Object element = XWT.findElementByName(root, "CompanyViewer1");
				assertTrue(element instanceof TableViewer);
				TableViewer tableViewer = (TableViewer) element;
				TableItem[] items = tableViewer.getTable().getItems();
				assertTrue(items.length == 3);
			}
		});
	}

	public void testTableViewer_MasterDetail_NestedTable_AddPath()
			throws Exception {
		URL url = JFaceTableViewer_Set_MasterDetail_Tests.class
				.getResource(TableViewer_MasterDetail_NestedTable_AddPath.class
						.getSimpleName()
						+ IConstants.XWT_EXTENSION_SUFFIX);
		runTest(url, new Runnable() {
			public void run() {
				// select a table company
				Object element = XWT.findElementByName(root, "AddButton");
				assertTrue(element instanceof Button);
				selectButton((Button) element);
			}
		}, new Runnable() {
			public void run() {
				Object element = XWT.findElementByName(root, "CompanyViewer1");
				assertTrue(element instanceof TableViewer);
				TableViewer tableViewer = (TableViewer) element;
				TableItem[] items = tableViewer.getTable().getItems();
				assertTrue(items.length == 3);
			}
		});
	}
}
