package ch.unisi.inf.sp.type.framework;

import org.objectweb.asm.tree.ClassNode;


/**
 * Implement this interface if you want to be called by an ArchiveScanner.
 * 
 * @author Matthias.Hauswirth@usi.ch
 */
public interface ClassAnalyzer {

	public void analyze(String location, ClassNode clazz);
	
}
