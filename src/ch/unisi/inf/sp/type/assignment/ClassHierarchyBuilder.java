package ch.unisi.inf.sp.type.assignment;

import java.util.List;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import ch.unisi.inf.sp.type.framework.ClassAnalyzer;
import ch.unisi.inf.sp.type.framework.ClassHierarchy;
import ch.unisi.inf.sp.type.framework.ClassType;
import ch.unisi.inf.sp.type.framework.Method;
import ch.unisi.inf.sp.type.framework.TypeInconsistencyException;


/**
 * Build a class hierarchy (including methods).
 * 
 * @author ?
 */
public final class ClassHierarchyBuilder implements ClassAnalyzer {

	private final ClassHierarchy classHierarchy;
	
	
	public ClassHierarchyBuilder() {
		this.classHierarchy = new ClassHierarchy();
	}
	
	public ClassHierarchy getClassHierarchy() {
		return classHierarchy;
	}
	
	public void analyze(final String location, final ClassNode clazz) {
		try {
			final ClassType classType = classHierarchy.getOrCreateClass(clazz.name);
			if (classType.isResolved()) {
				System.err.println("WARNING: Class "+classType.getInternalName()+" defined multiple times");
				return;
			}
			classType.setLocation(location);
			
			
			// TODO extract modifiers, super class, interfaces, methods
			List<MethodNode> methods = clazz.methods;
			for(MethodNode mn : methods) {
				classType.addMethod(new Method(clazz.name, mn.name, mn.desc, clazz.access));
			}
			
			List<String> interfaces = clazz.interfaces;
			for(String interf : interfaces) {
				final ClassType i = classHierarchy.getOrCreateClass(interf);
				classType.addInterface(i);
			}
			
			final ClassType s = classHierarchy.getOrCreateClass(clazz.superName);
			classType.setSuperClass(s);
			
			classType.setModifiers(clazz.access);

			
			
			classType.setResolved();
		} catch (final TypeInconsistencyException ex) {
			System.err.println(ex);
		}
	}
	
}
