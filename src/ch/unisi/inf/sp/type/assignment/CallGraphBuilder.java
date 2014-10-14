package ch.unisi.inf.sp.type.assignment;

import java.util.ArrayList;
import java.util.Collection;
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
		int counter = 0;
		try {
			final ClassType type = hierarchy.getOrCreateClass(classNode.name);
			final List<MethodNode> methodNodes = (List<MethodNode>)classNode.methods;
			for (final MethodNode methodNode : methodNodes) {
				final Method method = type.getMethod(methodNode.name, methodNode.desc);
				final InsnList instructions = methodNode.instructions;
				for (int i=0; i<instructions.size(); i++) {

					if (instructions.get(i).getType() != AbstractInsnNode.METHOD_INSN)
						continue;

					int opcode = instructions.get(i).getOpcode();
					MethodInsnNode insn = (MethodInsnNode) instructions.get(i);
					CallSite cs = new CallSite(opcode, insn.owner, insn.name, insn.desc);
					method.addCallSite(cs);

					ClassType superT = hierarchy.getOrCreateClass(insn.owner).getSuperClass();
					switch (opcode) {
					
					case 184: //INVOKE_STATIC
						cs.addPossibleTargetClass(hierarchy.getOrCreateClass(insn.owner));
					break;
					
					case 183: //INVOKE_SPECIAL
						if(method!=null && !method.isAbstract()){
							cs.addPossibleTargetClass(hierarchy.getOrCreateClass(insn.owner));
						}else{
							while (superT != null) {
								if(superT.getMethod(method.getName(), method.getDescriptor()) != null){	
									cs.addPossibleTargetClass(superT);
								}
								superT = superT.getSuperClass();
							}
						}

						break;
						
					case 182: //INVOKE VIRTUAL
						ClassType targetClass = hierarchy.getOrCreateClass(insn.owner);
						Method m = targetClass.getMethod(insn.name, insn.desc);
						superT = targetClass.getSuperClass();

						if(m!=null && !m.isAbstract()){
							cs.addPossibleTargetClass(targetClass);
						}else{
							while(m == null){
								targetClass = targetClass.getSuperClass();
								if(targetClass == null) {
									cs.addPossibleTargetClass(superT);
									break;
								}
								m = targetClass.getMethod(insn.name, insn.desc);
								if(m != null && m.isAbstract()) {
									m = null;
								}
							}
						}

						List <ClassType> sub = new ArrayList<ClassType>(hierarchy.getOrCreateClass(insn.owner).getSubTypes());
						while(!sub.isEmpty()){
							ClassType subType = sub.remove(0);
							Method methodz = subType.getMethod(insn.name, insn.desc);
							if( methodz!= null && !methodz.isAbstract()) {
								cs.addPossibleTargetClass(subType);
							}
							sub.addAll(subType.getSubTypes());						
						}
						break;
						
					case 185: //INVOKE INTERFACE
						targetClass = hierarchy.getOrCreateClass(insn.owner);

						List <ClassType> subc = new ArrayList<ClassType>(hierarchy.getOrCreateClass(insn.owner).getSubTypes());
						List<ClassType> subToCheck = new ArrayList<ClassType>();

						subToCheck.add(targetClass);

						while(!subc.isEmpty()) {
							ClassType curSub = subc.remove(0);

							if(!subToCheck.contains(curSub)) {
								m = curSub.getMethod(insn.name, insn.desc);
								if(m != null && !m.isAbstract()) {
									cs.addPossibleTargetClass(curSub);
								} 
								subc.addAll(curSub.getSubTypes());

								ClassType tempSuper = curSub.getSuperClass();

								if(tempSuper != null) {
									subc.add(tempSuper);
								}

								subToCheck.add(curSub);
							}
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
