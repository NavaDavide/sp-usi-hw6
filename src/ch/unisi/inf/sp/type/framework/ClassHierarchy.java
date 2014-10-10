package ch.unisi.inf.sp.type.framework;

import java.util.Collection;
import java.util.HashMap;


/**
 * The name space containing all known types.
 * 
 * @author Matthias.Hauswirth@usi.ch
 */
public final class ClassHierarchy {

	private HashMap<String, Type> typeByInternalName;
	
	
	public ClassHierarchy() {
		typeByInternalName = new HashMap<String, Type>();
		add(PrimitiveType.BYTE);
		add(PrimitiveType.SHORT);
		add(PrimitiveType.CHAR);
		add(PrimitiveType.INT);
		add(PrimitiveType.LONG);
		add(PrimitiveType.FLOAT);
		add(PrimitiveType.DOUBLE);
		add(PrimitiveType.BOOLEAN);
	}
	
	private final void add(final Type type) {
		typeByInternalName.put(type.getInternalName(), type);
	}
	
	public ClassType getOrCreateClass(final String internalName) throws TypeInconsistencyException {
		Type type = typeByInternalName.get(internalName);
		if (type==null) {
			type = new ClassType(internalName);
			typeByInternalName.put(internalName, type);
		} else if (!(type instanceof ClassType)) {
			throw new TypeInconsistencyException("Expected class, got "+type);
		}
		return (ClassType)type;
	}
	
	public ArrayType getOrCreateArrayType(final String internalName) throws TypeInconsistencyException {
		Type type = typeByInternalName.get(internalName);
		if (type==null) {
			final ArrayType arrayType = new ArrayType(internalName);
			typeByInternalName.put(internalName, arrayType);
			arrayType.resolve(this);
			type = arrayType;
		} else if (!(type instanceof ArrayType)) {
			throw new TypeInconsistencyException("Expected array type, got "+type);
		}
		return (ArrayType)type;
	}

	public PrimitiveType getPrimitiveType(final String internalName) throws TypeInconsistencyException {
		final Type type = typeByInternalName.get(internalName);
		if (!(type instanceof PrimitiveType)) {
			throw new TypeInconsistencyException("Expected primitive type, got "+type);
		}
		return (PrimitiveType)type;
	}
	
	public Collection<Type> getTypes() {
		return typeByInternalName.values();
	}
	
}
