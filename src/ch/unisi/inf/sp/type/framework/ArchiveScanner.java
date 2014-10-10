package ch.unisi.inf.sp.type.framework;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;


/**
 * Scans a JAR archive containing Java class files, uses ASM to load each class,
 * and for each class invokes Analyzer.analyze() on each registered Analyzer.
 *
 * @author Matthias.Hauswirth@usi.ch
 */
public final class ArchiveScanner {

	private final ArrayList<ClassAnalyzer> analyzers;
	
	
	public ArchiveScanner() {
		analyzers = new ArrayList<ClassAnalyzer>();
	}
	
	public void addAnalyzer(final ClassAnalyzer analyzer) {
		analyzers.add(analyzer);
	}
	
	public void removeAnalyzer(final ClassAnalyzer analyzer) {
		analyzers.remove(analyzer);
	}
	
	public void scan(final String archiveName) throws IOException {
		final ZipFile zipFile = new ZipFile(archiveName);
		final Enumeration<? extends ZipEntry> entries = zipFile.entries();
		while (entries.hasMoreElements()) {
			final ZipEntry zipEntry = entries.nextElement();
			if (zipEntry.getName().toLowerCase().endsWith(".class"))
				analyzeClass(zipFile, zipEntry);
		}
	}
	
	private void analyzeClass(final ZipFile zipFile, final ZipEntry zipEntry) throws IOException {
		final String location = zipFile.getName();
		final ClassReader classReader = new ClassReader(zipFile.getInputStream(zipEntry));
		// create an empty ClassNode (in-memory representation of a class)
		final ClassNode classNode = new ClassNode();
		// have the ClassReader read the class file and populate the ClassNode with the corresponding information
		classReader.accept(classNode, 0);
		for (final ClassAnalyzer analyzer : analyzers) {
			analyzer.analyze(location, classNode);
		}
	}

}
