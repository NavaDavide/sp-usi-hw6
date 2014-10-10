package ch.unisi.inf.sp.type.assignment;

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
					switch (opcode) {
					case 184: //INVOKE_STATIC
						cs.addPossibleTargetClass(hierarchy.getOrCreateClass(insn.owner));
						method.addCallSite(cs);
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
