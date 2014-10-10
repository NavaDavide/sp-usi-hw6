package ch.unisi.inf.sp.type.framework;

import java.util.Collection;
import java.util.HashSet;


/**
 * A call site represents a call instruction in the body of a method.
 * 
 * @author Matthias.Hauswirth@usi.ch
 */
public final class CallSite {

	private final int opcode;
	private final String declaredTargetClassName;
	private final String targetMethodName;
	private final String targetMethodDescriptor;
	private final HashSet<ClassType> possibleTargetClasses;
	
	
	/**
	 * Create a CallSite given the info taken from an ASM MethodInsnNode.
	 * 
	 * @param opcode from MethodInsnNode.getOpcode()
	 * @param declaredTargetClassName from MethodInsnNode.owner
	 * @param targetMethodName from MethodInsnNode.name
	 * @param targetMethodDescriptor from MethodInsnNode.desc
	 */
	public CallSite(final int opcode, final String declaredTargetClassName, final String targetMethodName, final String targetMethodDescriptor) {
		this.opcode = opcode;
		this.declaredTargetClassName = declaredTargetClassName;
		this.targetMethodName = targetMethodName;
		this.targetMethodDescriptor = targetMethodDescriptor;
		possibleTargetClasses = new HashSet<ClassType>();
	}
	
	public int getOpcode() {
		return opcode;
	}
	
	public String getDeclaredTargetClassName() {
		return declaredTargetClassName;
	}
	
	public String getTargetMethodName() {
		return targetMethodName;
	}
	
	public String getTargetMethodDescriptor() {
		return targetMethodDescriptor;
	}
	
	/**
	 * Use this method to add a possible target during Class Hierarchy Analysis.
	 * @param targetClass
	 */
	public void addPossibleTargetClass(final ClassType targetClass) {
		possibleTargetClasses.add(targetClass);
	}
	
	public Collection<ClassType> getPossibleTargetClasses() {
		return possibleTargetClasses;
	}
	
}
