package ch.unisi.inf.sp.type.assignment;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import ch.unisi.inf.sp.type.framework.CallSite;
import ch.unisi.inf.sp.type.framework.ClassAnalyzer;
import ch.unisi.inf.sp.type.framework.ClassHierarchy;
import ch.unisi.inf.sp.type.framework.ClassType;
import ch.unisi.inf.sp.type.framework.Method;
import ch.unisi.inf.sp.type.framework.TypeInconsistencyException;


/**
 * Build a call graph (as part of the class hierarchy)
 * consisting of CallSite nodes pointing to Method nodes.
 * 
 * @author ?
 */
public final class CallGraphBuilder implements ClassAnalyzer {

	private final ClassHierarchy hierarchy;
	
	
	public CallGraphBuilder(final ClassHierarchy hierarchy) {
		this.hierarchy = hierarchy;
	}
	
	public void analyze(final String location, final ClassNode classNode) {
		try {
			final ClassType type = hierarchy.getOrCreateClass(classNode.name);
			final List<MethodNode> methodNodes = (List<MethodNode>)classNode.methods;
			for (final MethodNode methodNode : methodNodes) {
				final Method method = type.getMethod(methodNode.name, methodNode.desc);
				final InsnList instructions = methodNode.instructions;
				for (int i=0; i<instructions.size(); i++) {
					// TODO implement this
					if (instructions.get(i).getType() != AbstractInsnNode.METHOD_INSN)
						continue;
					MethodInsnNode insn = (MethodInsnNode) instructions.get(i);
					int opcode = instructions.get(i).getOpcode();
					CallSite cs = new CallSite(opcode, insn.owner, insn.name, insn.desc);
					method.addCallSite(cs);
					if (instructions.get(i).getType() != AbstractInsnNode.METHOD_INSN)
						continue;
					ClassType targetClass = hierarchy.getOrCreateClass(((MethodInsnNode)insn).owner);
					Method targetMethod = targetClass.getMethod(((MethodInsnNode)insn).name, ((MethodInsnNode)insn).desc);
					if(targetMethod == null){
						continue;
					}
					
					ClassType superT = hierarchy.getOrCreateClass(insn.owner).getSuperClass();
					switch (opcode) {
					case 184: //INVOKE_STATIC
						cs.addPossibleTargetClass(hierarchy.getOrCreateClass(insn.owner));
						break;
					case 183: //INVOKE_SPECIAL
						while (superT != null) {
							if(superT.getMethod(method.getName(), method.getDescriptor())!=null){	
								cs.addPossibleTargetClass(superT);
							}
							superT = superT.getSuperClass();
						}
						break;
					case 182: //INVOKE VIRTUAL
						while (superT != null) {
							if(superT.getSuperClass() == null){	
								final Method supMethod = superT.getMethod(insn.name, insn.desc);
								if(supMethod!=null){
									if(supMethod.isAbstract())
										cs.addPossibleTargetClass(superT);
								}
							}
							superT = superT.getSuperClass();
						}
						if(method!=null){
							if(method.isAbstract())
								cs.addPossibleTargetClass(targetClass);
						}
						List <ClassType> sub = new ArrayList<ClassType>(hierarchy.getOrCreateClass(insn.owner).getSubTypes());
						while(!sub.isEmpty()){
							ClassType subType = sub.get(0);
							//System.out.println(subType.getInternalName());
							sub.remove(0);
								List<Method> methods = new ArrayList<Method>(subType.getMethods());
								for (Method methodz : methods) {
									if(methodz.getName().equals(method.getName())){
										if(!methodz.isAbstract() && methodz!= null) {
											cs.addPossibleTargetClass(subType);
										}
									}
								}
							sub.addAll(subType.getSubTypes());						
						}
						break;
					default:
						break;
					}
				}
			}
		} catch (final TypeInconsistencyException ex) {
			System.err.println(ex);
		}
	}

}
