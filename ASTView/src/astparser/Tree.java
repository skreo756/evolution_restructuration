package astparser;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class Tree {

	public String className;
	
	public Map<String, Set<Node>> declarationInvocations;
	
	public Tree(String className) {
		this.className = className;
		
		declarationInvocations = new TreeMap<String, Set<Node>>();
	}
}
