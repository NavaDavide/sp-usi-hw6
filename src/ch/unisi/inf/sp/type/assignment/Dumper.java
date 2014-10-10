package ch.unisi.inf.sp.type.assignment;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
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
		for (final Type type : hierarchy.getTypes()) {
			if (type instanceof ClassType) {
				final ClassType classType = (ClassType)type;

				// TODO implement this
				Collection<Method> methods = classType.getMethods();
				for (Method m : methods) {
					boolean isCaller = false;
					final String from = /*m.getDeclaringClassName()+*/m.getName().replace("<", "").
							replace(">", "").replace("$", "_");
					for (CallSite cs : m.getCallSites()) {
						isCaller = true;
						for(ClassType ct : cs.getPossibleTargetClasses()) {
							if (ct.isResolved()) {
								Method target = ct.getMethod(cs.getTargetMethodName(), cs.getTargetMethodDescriptor());
								final String to = /*target.getDeclaringClassName()+*/target.getName().replace("<", "")
										.replace(">", "").replace("$", "_");
								pw.println(to + " [label=\"" + target.getName() + "\"]");
								//pw.println(from + " -> " + to);
								edges.add(from + " -> " + to);
							}
						}
					}
					if (isCaller)
						pw.println(from + " [label=\"" + m.getName() + "\"]");
				}
			}
		}
		for (String edge : edges) {
			pw.println(edge);
		}
		pw.println("}");
		pw.close();
	}

}
