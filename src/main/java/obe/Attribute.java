package obe;

public class Attribute extends Obe implements IArtefact{
	
	private Access access;
	private Class cls;
	
	public Attribute(String name, Class cls, Access access ) {
		this.setName(name);
		this.cls = cls;
		this.access = access;
	}

	public Access isAccessedIn() {
		return access;
	}
	
	public Class belongsToClass() {
		return cls;
	}

	@Override
	public int getIdentifiant() {
		// TODO Auto-generated method stub
		return this.getName().hashCode();
	}
	
	
	
	
}
