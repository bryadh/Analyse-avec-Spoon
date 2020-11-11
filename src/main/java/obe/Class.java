package obe;

import java.util.List;

public class Class extends Obe implements IArtefact {
	
	private Package pack;
	private List<Method> methods = null;
	private List<Attribute> attributes = null;
	
	public Class(String name, Package pack) {
		this.setName(name);
		this.pack = pack;
	}
	
	public Package belongsToPackage() {
		return pack;
	}

	public void setMethods(List<Method> methods) {
		this.methods = methods;
	}

	public void setAttributes(List<Attribute> attributes) {
		this.attributes = attributes;
	}

	@Override
	public int getIdentifiant() {
		return this.getName().hashCode() ;
	}
	
	//ne respect pas le meta modele
	public List<Method> getMethods(){
		return this.methods;
	}
			
}
