package ch.unisi.inf.sp.type.assignment;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ch.unisi.inf.sp.type.framework.CallSite;
import ch.unisi.inf.sp.type.framework.ClassHierarchy;
import ch.unisi.inf.sp.type.framework.ClassType;
import ch.unisi.inf.sp.type.framework.Method;
import ch.unisi.inf.sp.type.framework.Type;
import ch.unisi.inf.sp.type.framework.TypeInconsistencyException;


/**
 * Dump out information about the given ClassHierarchy.
 * 
 * @author ?
 */
public final class Dumper {

	public void dumpDot(final ClassHierarchy hierarchy, final String fileName) throws IOException {
		final PrintWriter pw = new PrintWriter(new FileWriter(fileName));
		Set<String> edges = new HashSet<String>();
		pw.println("digraph types {");
		pw.println("  rankdir=\"BT\"");
		String dotEdges = "";

		for (final Type type : hierarchy.getTypes()) {
			if (type instanceof ClassType) {

				final ClassType classType = (ClassType)type;
				String className = "";
				className = classType.getInternalName().replaceAll("[\\[()/;<>\\[$-.]" , "");
				String classNameLabel = classType.getInternalName().replace("/", ".");
				className = replaceKeywords(className);
				classNameLabel = replaceKeywords(classNameLabel);



				Collection<Method> methods = classType.getMethods();
				int methodsCount = 0;
				int methodSize = methods.size();
				String record = "";
				if(classType.isInterface()){
					record = className+"[shape=\"record\", style = dotted, label=\""+"{"+classNameLabel.replace("_", "")+" | {";
				}else if(classType.isAbstract()){
					record = className+"[shape=\"record\", style = dashed, label=\""+"{"+classNameLabel.replace("_", "")+" | {";
				}else{
					if(methods.isEmpty()){
						record = className+"[ style = dotted, label=\""+classNameLabel.replace("_", "");
					}else{
						record = className+"[shape=\"record\", label=\"{"+classNameLabel.replace("_", "")+" | {";
					}
				}
				ClassType superType = classType.getSuperClass();
				if(superType != null) {
					String superTypeName = superType.getInternalName().replaceAll("[\\[();<>\\[$-]" , "");
					pw.println("\t" + classNameLabel.replace(".", "") + "-> " + superTypeName.replace("/", "")+ "[style= line, arrowhead= empty ]");
				}

				Collection<ClassType> interfaces = classType.getInterfaces();
				for(ClassType i: interfaces) {
					String interfaceName = i.getInternalName().replaceAll("[\\[();<>\\[$-]" , "");
					pw.println("\t" + classNameLabel.replace(".", "") + "->" + interfaceName.replace("/", "") + "[style= dashed, arrowhead= empty ]");
				}
				for (Method m : methods) {
					String methodName = m.getDeclaringClassName()+m.getName()+m.getDescriptor();
					String methodNameLabel = m.getName()+m.getDescriptor();

					methodName = methodName.replaceAll("[\\[();/<>\\[$-]" , "");
					methodNameLabel = methodNameLabel.replace("<","[").replace(">","]");
					if(methodsCount<methodSize-1){
						record += methodNameLabel + "\n";
					}else{
						record += methodNameLabel;
					}
					methodsCount++;
					methodName = m.getDeclaringClassName()+m.getName()+m.getDescriptor();
					methodNameLabel = m.getDeclaringClassName()+"\n"+m.getName()+m.getDescriptor();
					methodName = methodName.replaceAll("[\\[();/<>\\[$-]" , "");
					methodName = replaceKeywords(methodName);
					methodNameLabel = replaceKeywords(methodNameLabel);

					pw.println("\t" + methodName + "->" + classNameLabel.replace(".", "")+ "[ color = darkolivegreen1, arrowhead = none]");

					pw.println(methodName + "[shape=\"rectangle\" label=\""+methodNameLabel+"\" fillcolor = darkolivegreen1 style= filled]");

					Collection<CallSite> cs = m.getCallSites();
					for (CallSite callSite : cs) {
						Collection<ClassType> targets = callSite.getPossibleTargetClasses();
						for (ClassType target : targets) {
							if(target!=null){
								Method tm = target.getMethod(callSite.getTargetMethodName(), callSite.getTargetMethodDescriptor());
								if(tm!=null){
									boolean isConstructor = false;
									String targetName = tm.getDeclaringClassName()+tm.getName()+tm.getDescriptor();
									if (targetName.contains("init")) isConstructor = true;
									targetName = replaceKeywords(targetName);
									targetName  = targetName.replaceAll("[();/<>\\[$-]", "");
									if(isConstructor){
										pw.println(methodName + "->"  + targetName + "[color = red ]");
									}else{
										try {
											ClassType cutct =hierarchy.getOrCreateClass(tm.getDeclaringClassName());
											Collection<ClassType> curInterf = cutct.getInterfaces();
											if(!curInterf.isEmpty()){
												pw.println(methodName + "->"  + targetName + "[color = red style = dotted]");
											}else{
												pw.println(methodName + "->"  + targetName + "[color = red style = dashed]");
											}
										} catch (TypeInconsistencyException e) {
											e.printStackTrace();
										}
									}
								}
							}
						}
					}
				}
				if(methods.isEmpty()){
					record += "\"]";
				}else{
					record += "}}\"]";
				}

				pw.println(record);
			}
		}
		pw.println(dotEdges);
		pw.println("}");
		pw.close();
	}
	private String replaceKeywords(String s){
		if(s.contains("Node")){s = s.replace("Node", "_"); }
		if(s.contains("Edge")){s = s.replace("Edge", "_"); }
		if(s.contains("graph")){s = s.replace("graph", "_"); }
		if(s.contains("Digraph")){s = s.replace("Digraph", "_"); }
		if(s.contains("Digraph")){s = s.replace("Digraph", "_");}
		if(s.contains("Strict")){s = s.replace("Strict", "_"); }
		if(s.contains("strict")){s = s.replace("strict", "_");}
		return s;
	}

	public void dumpPrint(final ClassHierarchy hierarchy, String fileName) throws IOException {
		int virtualCounter = 0;
		int interfaceCounter = 0;
		double virtualTargets = 0;
		double interfaceTargets = 0;
		for (final Type type : hierarchy.getTypes()) {
			if (type instanceof ClassType) {
				final ClassType classType = (ClassType)type;
				for(final Method method : classType.getMethods()){
					for(final CallSite cs : method.getCallSites()){
						int opcode = cs.getOpcode();
						Collection<ClassType> possibleTargets = cs.getPossibleTargetClasses();
						int tempVirtual = 0;
						int tempInterface = 0;
						for(ClassType target: possibleTargets) {
							if(opcode == 182){
								tempVirtual ++;
							}
							if(opcode == 185){
								tempInterface ++;
							}
						}
						if(opcode == 182){
							virtualCounter ++;
							if(tempVirtual == 0)tempVirtual++;
							virtualTargets += tempVirtual;
						}
						if(opcode == 185){
							interfaceCounter++;
							if(tempInterface == 0)tempInterface++;
							interfaceTargets += tempInterface;
						}
					}
				}				
			}
		}
		//virtualTargets = 10044;
		System.out.println(fileName + ", INVOKE_VIRTUAL, "+virtualCounter+", VIRTUAL_TARGETS "+ virtualTargets + " MEAN "+(virtualTargets/virtualCounter));
		System.out.println(fileName + ", INVOKE_INTERFACE, "+interfaceCounter+", "+(interfaceTargets/interfaceCounter));
	}

}
