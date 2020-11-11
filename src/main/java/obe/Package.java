package obe;

import java.util.List;

public class Package extends Obe {
	
	private List<Class> classes;
	private List<Interface> interfaces;
	
	public Package(String name) {
		this.setName(name);
	}

	public void setClasses(List<Class> classes) {
		this.classes = classes;
	}

	public void setInterfaces(List<Interface> interfaces) {
		this.interfaces = interfaces;
	}
	
	public List<Class> getClasses() {
		return this.classes;
	}
	
	
}
