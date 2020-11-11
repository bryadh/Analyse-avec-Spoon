package obe;

public class Inheritance {
	
	private Class superClass;
	private Class subClass;
	
	public Inheritance(Class sup, Class sub) {
		this.subClass = sub;
		this.superClass = sup;
	}
	
	public Class hasSuperClass() {
		return superClass;
	}
	public Class hasSubClass() {
		return subClass;
	}
	
	

}
