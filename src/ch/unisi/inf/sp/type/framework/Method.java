package ch.unisi.inf.sp.type.framework;

import java.util.ArrayList;
import java.util.Collection;

import org.objectweb.asm.Opcodes;


/**
 * A method declared in a class or interface. May be abstract.
 * A method can contain several CallSites
 * (information about CallSites may or may not be available,
 * e.g. usually CallSites are added to Method objects only by the CallGraphBuilder).
 * 
 * @author Matthias.Hauswirth@usi.ch
 */
public final class Method {

	private final String declaringClassName;
	private final String name;
	private final String descriptor;
	private final int modifiers;
	private final ArrayList<CallSite> callSites;
	
	
	/**
	 * 
	 * @param name The name of the method, "<init>" for constructor or instance initializer, "<clinit>" for static initializer.
	 * @param descriptor The descriptor showing argument and return types
	 *        (e.g. "(IJ)V" means the method takes two arguments, an int and a long, and its return type is void)
	 * @param modifiers
	 */
	public Method(final String declaringClassName, final String name, final String descriptor, final int modifiers) {
		this.declaringClassName = declaringClassName;
		this.name = name;
		this.descriptor = descriptor;
		this.modifiers = modifiers;
		this.callSites = new ArrayList<CallSite>();
	}

	/**
	 * Get the internal name of the class declaring this method.
	 */
	public String getDeclaringClassName() {
		return declaringClassName;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDescriptor() {
		return descriptor;
	}
	
	/**
	 * Get the modifiers (access flags, ...) as an int, as taken from ASM.
	 */
	public int getModifiers() {
		return modifiers;
	}
	
	public boolean isStatic() {
		return (Opcodes.ACC_STATIC & modifiers) != 0;
	}
	
	public boolean isPublic() {
		return (Opcodes.ACC_PUBLIC & modifiers) != 0;
	}
	
	public boolean isProtected() {
		return (Opcodes.ACC_PROTECTED & modifiers) != 0;
	}
	
	public boolean isPrivate() {
		return (Opcodes.ACC_PRIVATE & modifiers) != 0;
	}
	
	public boolean isAbstract() {
		return (Opcodes.ACC_ABSTRACT & modifiers) !=0;
	}
	
	public boolean isFinal() {
		return (Opcodes.ACC_FINAL & modifiers) !=0;
	}
	
	/**
	 * Add a CallSite to this method.
	 * Usually done only when really needed (e.g. by CallGraphBuilder).
	 * @param callSite
	 */
	public void addCallSite(final CallSite callSite) {
		callSites.add(callSite);
	}
	
	/**
	 * Get all CallSites in this method
	 * (will return an empty collection if no CallSites were added).
	 */
	public Collection<CallSite> getCallSites() {
		return callSites;
	}
	
}
