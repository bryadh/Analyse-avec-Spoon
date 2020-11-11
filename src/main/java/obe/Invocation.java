package obe;

import java.util.List;

public class Invocation {
	
	private Method method;
	private List<Method> methList = null;

	public Invocation(Method method) {
		this.method = method;
	}
	
	public void setMethList(List<Method> methList) {
		this.methList = methList;
	}
	
	public Method getMethod() {
		return this.method;
	}
	
	
		
	

}
