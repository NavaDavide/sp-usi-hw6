package ch.unisi.inf.sp.type.framework;


/**
 * Represents a primitive type.

 * @author Matthias.Hauswirth@usi.ch
 */
public enum PrimitiveType implements Type {

	BYTE("B", "byte"), 
	SHORT("S", "short"), 
	CHAR("C", "char"), 
	INT("I", "int"), 
	LONG("J", "long"), 
	FLOAT("F", "float"), 
	DOUBLE("D", "double"), 
	BOOLEAN("Z", "boolean"), 
	VOID("V", "void");
	
	private final String internalName;
	private final String simpleName;
	
	
	private PrimitiveType(final String internalName, final String simpleName) {
		this.internalName = internalName;
		this.simpleName = simpleName;
	}
	
	public String getInternalName() {
		return internalName;
	}
	
	public boolean isResolved() {
		return true;
	}
	
	public String getSimpleName() {
		return simpleName;
	}
	
}
