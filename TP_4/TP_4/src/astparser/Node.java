package astparser;

public class Node implements Comparable<Node>{
	
	public String className;
	public String methodName;
	
	public Node(String className, String methodName) {
		
		this.className = className;
		
		this.methodName = methodName;
	}

	@Override
	public int compareTo(Node NodeToCompare) {
		return className.compareTo(NodeToCompare.className) + 
				methodName.compareTo(NodeToCompare.methodName);
	}
	
	

}
