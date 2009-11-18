package org.eclipse.e4.xwt.tests.metaclass;

import junit.framework.TestCase;

import org.eclipse.e4.xwt.javabean.metadata.AbstractMetaclass;
import org.eclipse.e4.xwt.metadata.IObjectInitializer;

public class Metaclass_Tests extends TestCase {

	public void testInitilaizers() {
		AbstractMetaclass metaclass = new AbstractMetaclass() {
		};

		IObjectInitializer initializer1 = new IObjectInitializer() {
			public void initialize(Object object) {
			}
		};

		IObjectInitializer initializer2 = new IObjectInitializer() {
			public void initialize(Object object) {
			}
		};

		IObjectInitializer initializer3 = new IObjectInitializer() {
			public void initialize(Object object) {
			}
		};

		metaclass.addInitializer(initializer1);

		assertEquals(metaclass.getInitializers().length, 1);

		metaclass.addInitializer(initializer1);
		assertEquals(metaclass.getInitializers().length, 1);

		metaclass.addInitializer(initializer2);
		assertEquals(metaclass.getInitializers().length, 2);
		assertEquals(metaclass.getInitializers()[0], initializer1);
		assertEquals(metaclass.getInitializers()[1], initializer2);

		metaclass.addInitializer(initializer3);
		assertEquals(metaclass.getInitializers().length, 3);
		assertEquals(metaclass.getInitializers()[0], initializer1);
		assertEquals(metaclass.getInitializers()[1], initializer2);
		assertEquals(metaclass.getInitializers()[2], initializer3);

		metaclass.removeInitializer(initializer2);
		assertEquals(metaclass.getInitializers().length, 2);
		assertEquals(metaclass.getInitializers()[0], initializer1);
		assertEquals(metaclass.getInitializers()[1], initializer3);

		metaclass.removeInitializer(initializer1);
		assertEquals(metaclass.getInitializers().length, 1);
		assertEquals(metaclass.getInitializers()[0], initializer3);

		metaclass.removeInitializer(initializer3);
		assertEquals(metaclass.getInitializers().length, 0);
	}
}
