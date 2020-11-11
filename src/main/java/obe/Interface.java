package obe;

public class Interface extends Obe {
	
	private Package pack;
	
	public Interface(String name, Package pack) {
		this.setName(name);
		this.pack = pack;
	}

	public Package belongsToPackage() {
		return pack;
	}
	
	

}
