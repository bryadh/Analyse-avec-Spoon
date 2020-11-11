package obe;

import java.util.List;

public class Method extends Obe implements IArtefact {
	
	private Signature signature;
	private List<Invocation> invocations;
	private List<LocalVariable> variables;
	private List<Access> accesses;
	
	public Method(String name, Signature s) {
		this.setName(name);
		this.signature = s;
	}
	
	public List<Invocation> invokedBy() {
		return invocations;
	}

	public List<Access> hasAccess() {
		return accesses;
	}

	public void setInvocations(List<Invocation> invocations) {
		this.invocations = invocations;
	}

	public void setVariables(List<LocalVariable> variables) {
		this.variables = variables;
	}
	
	
	public List<LocalVariable> getVariables() {
		return variables;
	}

	@Override
	public int getIdentifiant() {
		// TODO Auto-generated method stub
		return this.getName().hashCode();
	}
	
	
	
	
	
	
}
