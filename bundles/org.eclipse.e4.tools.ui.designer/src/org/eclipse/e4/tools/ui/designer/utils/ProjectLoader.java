package org.eclipse.e4.tools.ui.designer.utils;

import java.net.URL;

import org.eclipse.jdt.core.IJavaProject;

public class ProjectLoader {
	protected IJavaProject javaProject;
	private ClassLoader classLoader;
	
	class RuntimeLoader extends ClassLoader {

		public RuntimeLoader(ClassLoader parent) {
			super(parent);
		}

		@Override
		protected Class<?> findClass(String name) throws ClassNotFoundException {
			Class<?> type = null;
			try {
				String className = name;
				int index = name.lastIndexOf('/');
				if (index != -1) {
					className = name.substring(index+1);
				}
				type = super.findClass(className);
			} catch (ClassNotFoundException e) {
				if (type == null) {
					type = redefined(name);
				}
				if (type == null) {
					throw e;
				} 
			}
			return type;
		}

		public Class<?> redefined(String name) {
			try {
				byte[] content = ClassLoaderHelper.getClassContent(ProjectLoader.this.javaProject, name);
				if (content != null) {
					int index = name.lastIndexOf('/');
					if (index != -1) {
						name = name.substring(index + 1);
					}
					return defineClass(name, content, 0, content.length);
				}
			} catch (Throwable e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected URL findResource(String name) {
			URL url = super.findResource(name);
			if (url == null) {
				url = ClassLoaderHelper.getResourceAsURL(ProjectLoader.this.javaProject, name);
			}
			return url;
		}
	}

	public ProjectLoader(IJavaProject javaProject) {
		this.javaProject = javaProject;
		resetLoader();
	}

	protected void resetLoader() {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		setClassLoader(new RuntimeLoader(classLoader));
	}
	
	protected void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
		Thread.currentThread().setContextClassLoader(classLoader);
	}

	protected ClassLoader getClassLoader() {
		return classLoader;
	}
	
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		return getClassLoader().loadClass(name);
	}
}
