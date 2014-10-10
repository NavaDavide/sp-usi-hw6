package ch.unisi.inf.sp.type.framework;


/**
 * Represents the type of an array.
 * 
 * @author Matthias.Hauswirth@usi.ch
 */
public final class ArrayType implements Type {

	private final String internalName;
	private boolean resolved;
	private Type componentType;
	
	
	public ArrayType(final String internalName) {
		this.internalName = internalName;
	}
	
	public String getInternalName() {
		return internalName;
	}
	
	public boolean isResolved() {
		return resolved;
	}

	public String getSimpleName() {
		if (!resolved) {
			throw new IllegalStateException("Array type "+internalName+" not resolved yet");
		}
		return componentType.getSimpleName()+"[]";
	}
	
	public void resolve(final ClassHierarchy nameSpace) throws TypeInconsistencyException {
		if (internalName.charAt(1)=='[') {
			componentType = nameSpace.getOrCreateArrayType(internalName.substring(1));
		} else if (internalName.charAt(1)=='L') {
			componentType = nameSpace.getOrCreateClass(internalName.substring(2, internalName.length()-1));
		} else {
			componentType = nameSpace.getPrimitiveType(internalName.substring(1));
		}
		resolved = true;
	}
	
}
