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
				// TODO implement this
				String className = "";
				className = classType.getSimpleName().replace("$", "").replace(".", "");
				if(className.contains("Node")){
					className += "_";
				}
				String record = "";
				if(classType.isInterface()){
					record = className+"[shape=\"record\", label=\"{"+"ABSTRACT_"+className.replace("_", "")+" | ";
				}else if(classType.isInterface()){
					record = className+"[shape=\"record\", label=\"{"+"INTERFACE_"+className.replace("_", "")+" | ";
				}else if(classType.isEnum()){
					record = className+"[shape=\"record\", label=\"{"+"ENUM_"+className.replace("_", "")+" | ";
				}else{
					record = className+"[shape=\"record\", label=\"{"+className.replace("_", "")+" | {";
				}

				List<Method> methods = new ArrayList<Method>(classType.getMethods());
				
				if(methods.isEmpty())
					continue;
				
				for (int i = 0; i < methods.size(); i++) {
					Method m = methods.get(i);
					final String methodname = /*m.getDeclaringClassName()+*/m.getName().replace("<", "")
																					   .replace(">", "")
																					   .replace("$", "_")
																					   .replace(".", "");
					String methodDesc = m.getDescriptor();

					if(i<methods.size()-1){
						record += methodname + methodDesc + "|";
					}else{
						record += methodname + methodDesc;
					}
				}
				record += "}\"]";
				pw.println(record);
				List <ClassType> sub = new ArrayList<ClassType>(classType.getSubTypes());
				
				for (int i = 0; i < sub.size(); i++) {
					ClassType subtype = sub.get(i);
					String subName = subtype.getSimpleName().replace("$", "").replace(".", "");
					if(subName.contains("Node")){
						subName += "_";
					}
					if(!edges.contains(subName)){
						//pw.println(className + " -> "+subName);
						dotEdges += className + " -> "+subName +"\n";
					}
					edges.add(subName);
				}
				
			}
		}
		pw.println(dotEdges);
		pw.println("}");
		pw.close();
	}
	public void dumpPrint(final ClassHierarchy hierarchy, String fileName) throws IOException {
		int virtualCounter = 0;
		int interfaceCounter = 0;
		double virtualTargets = 0;
		double interfaceTargets = 0;
		int totalCalls = 0;
		for (final Type type : hierarchy.getTypes()) {
			if (type instanceof ClassType) {
				final ClassType classType = (ClassType)type;
				for(final Method method : classType.getMethods()){
					totalCalls+=method.getCallSites().size();
					for(final CallSite cs : method.getCallSites()){
						int opcode = cs.getOpcode();
						if(opcode == 182){
							virtualCounter ++;
							virtualTargets += cs.getPossibleTargetClasses().size();
						}
						if(opcode == 185){
							interfaceCounter++;
							interfaceTargets += cs.getPossibleTargetClasses().size();
						}
					}
				}				
			}
		}
		//virtualTargets = 10044;
		System.out.println(fileName + ", INVOKE_VIRTUAL, "+virtualCounter+", VIRTUAL_TARGETS "+ virtualTargets +" "+(virtualTargets/virtualCounter));
		System.out.println(fileName + ", INVOKE_INTERFACE, "+interfaceCounter+", "+(interfaceTargets/interfaceCounter));
	}

}
