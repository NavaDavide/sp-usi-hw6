package ch.unisi.inf.sp.type.framework;


/**
 * Thrown in cases of type inconsistencies while building the ClassHierarchy
 * 
 * @author Matthias.Hauswirth@usi.ch
 */
public class TypeInconsistencyException extends Exception {

	public TypeInconsistencyException(final String message) {
		super(message);
	}
	
}
