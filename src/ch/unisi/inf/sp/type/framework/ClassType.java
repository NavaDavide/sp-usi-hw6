package ch.unisi.inf.sp.type.framework;

import java.util.ArrayList;
import java.util.Collection;

import org.objectweb.asm.Opcodes;


/**
 * A ClassType represents a class or an interface.
 * 
 * @author Matthias.Hauswirth@usi.ch
 */
public final class ClassType implements Type {

	private final String internalName;
	private boolean resolved;
	private String location;
	private int modifiers;
	private ClassType superClass;
	private ArrayList<ClassType> interfaces;
	private ArrayList<Method> methods;
	private ArrayList<ClassType> subTypes;
	
	/**
	 * Create a ClassType given an internal name (without "L" prefix or ";" suffix).
	 * @param internalName the internal name of the class, 
	 *        e.g. "java/lang/Object" (class Object in package java.lang)
	 *        or "java/awt/geom/Point2D$Double" (class Double in class Point2D in package java.awt.geom)
	 *        or "TypeInDefaultPackage" (class TypeInDefaultPackage in the default package).
	 */
	public ClassType(final String internalName) {
		this.internalName = internalName;
		this.interfaces = new ArrayList<ClassType>();
		this.methods = new ArrayList<Method>();
		this.subTypes = new ArrayList<ClassType>();
	}
	
	public String getInternalName() {
		return internalName;
	}
	
	/**
	 * Returns the simple name of the underlying class as given in the source code. 
	 * Returns an empty string if the underlying class is anonymous.
     * The simple name of an array is the simple name of the component type with "[]" appended. 
     * In particular the simple name of an array whose component type is anonymous is "[]".
	 */
	public String getSimpleName() {
		final int dollar = internalName.lastIndexOf('$');
		if (dollar>-1) {
			final String n = internalName.substring(dollar);
			if (n.matches("[0-9]+")) {
				// anonymous inner classes have numbers as their names
				return "";
			} else {
				// inner/nested class
				return n;
			}
		} else {
			final int slash = internalName.lastIndexOf('/');
			if (slash>-1) {
				// class in package
				return internalName.substring(slash).replace('/', '.');
			} else {
				// class in default package
				return internalName;
			}
		}
	}

	/**
	 * Do this after you have completed reading this class
	 * (classes that were never read, but referenced by other classes, will appear as not resolved)
	 */
	public void setResolved() {
		resolved = true;
	}
	
	public boolean isResolved() {
		return resolved;
	}
	
	/**
	 * Set the location (e.g. the name of the JAR file) this class was loaded from
	 * when you read in the class.
	 * @param location
	 */
	public void setLocation(final String location) {
		this.location = location;
	}
	
	/**
	 * Get the location (e.g. the name of the JAR file) this class was loaded from.
	 * @return
	 */
	public String getLocation() {
		return location;
	}
	
	/**
	 * Set this class (or interface's) super class 
	 * when you read in the class.
	 * @param superClass
	 */
	public void setSuperClass(final ClassType superClass) {
		this.superClass = superClass;
		// automatically maintain subtypes
		superClass.subTypes.add(this);
	}
	
	/**
	 * Get this class (or interface's) super class.
	 */
	public ClassType getSuperClass() {
		return superClass;
	}
	
	/**
	 * Add interfa to the list of this class (or interface's) interfaces
	 * when you read in the class.
	 * @param interfa The interface implemented by this class, resp. extended by this interface.
	 */
	public void addInterface(final ClassType interfa) {
		interfaces.add(interfa);
		// automatically maintain subtypes
		interfa.subTypes.add(this);
	}
	
	/**
	 * Get all the interfaces implemented by this class resp. extended by this interface.
	 */
	public Collection<ClassType> getInterfaces() {
		return interfaces;
	}
	
	/**
	 * Get all the currently known subtypes (interfaces and/or classes) of this interface or class.
	 */
	public Collection<ClassType> getSubTypes() {
		return subTypes;
	}
	
	/**
	 * Add a method to this class
	 * when you read in the clas.
	 * The class should contain all the methods it explicitly declares
	 * (including abstract methods).
	 */
	public void addMethod(final Method method) {
		methods.add(method);
	}

	/**
	 * Get all the methods this class declares.
	 */
	public Collection<Method> getMethods() {
		return methods;
	}

	/**
	 * Get the method with the given name and descriptor, if such a method is declared in this class.
	 * @param name e.g. "<init>" or "main"
	 * @param descriptor E.g. "()V" or "([Ljava/lang/String;)V"
	 * @return the Method or null
	 */
	public Method getMethod(final String name, final String descriptor) {
		for (final Method method : methods) {
			if (method.getName().equals(name) && method.getDescriptor().equals(descriptor)) {
				return method;
			}
		}
		// no such method declared in this class
		// (one still could be declared in a superclass or interface!)
		return null;
	}
	
	/**
	 * Set the modifiers when you read in the class.
	 * @param modifiers Modifiers coming from ASM's ClassNode.access
	 */
	public void setModifiers(final int modifiers) {
		this.modifiers = modifiers;
	}
	
	public int getModifiers() {
		return modifiers;
	}
	
	public boolean isInterface() {
		return (modifiers & Opcodes.ACC_INTERFACE) != 0;
	}
	
	public boolean isAbstract() {
		return (modifiers & Opcodes.ACC_ABSTRACT) != 0;
	}
	
	public boolean isFinal() {
		return (modifiers & Opcodes.ACC_FINAL) != 0;
	}
	
	public boolean isEnum() {
		return (modifiers & Opcodes.ACC_ENUM) != 0;
	}
	
	public String toString() {
		return (isInterface()?"interface ":(isEnum()?"enum ":"class "))+getInternalName();
	}
	
}
