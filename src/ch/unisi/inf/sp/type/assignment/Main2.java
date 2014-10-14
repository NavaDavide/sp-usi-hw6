package ch.unisi.inf.sp.type.assignment;

import java.io.IOException;

import ch.unisi.inf.sp.type.framework.ArchiveScanner;


/**
 * Main class.
 * 
 * @author ?
 */
public final class Main2 {

	public static void main(final String[] args) throws IOException {
		for (final String arg : args) {
			System.out.println(arg);

			final ArchiveScanner scanner = new ArchiveScanner();
			
			// phase 1: build inheritance hierarchy
			final ClassHierarchyBuilder classHierarchyBuilder = new ClassHierarchyBuilder();
			scanner.addAnalyzer(classHierarchyBuilder);
			scanner.scan(arg);
			scanner.removeAnalyzer(classHierarchyBuilder);
			
			// phase 2: add call sites and edges
			final CallGraphBuilder callGraphBuilder = new CallGraphBuilder(classHierarchyBuilder.getClassHierarchy());
			scanner.addAnalyzer(callGraphBuilder);
			scanner.scan(arg);
			
			// dump info about structure (e.g. inheritance hierarchy, call graph, statistics, ...)
			// TODO probably change this
			String[] temp = arg.split("/");
			String[] fileN = temp[temp.length-1].split("\\.");
			String fileName = fileN[0];
	
			Dumper dumper = new Dumper();
			dumper.dumpDot(classHierarchyBuilder.getClassHierarchy(), "graph.dot");
			dumper.dumpPrint(classHierarchyBuilder.getClassHierarchy(), fileName);
		}
	}
}
