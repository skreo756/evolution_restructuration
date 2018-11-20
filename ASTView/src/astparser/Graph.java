package astparser;

import com.mxgraph.examples.swing.*;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

import java.util.Map;
import java.util.Set;

import javax.swing.JFrame;

import com.mxgraph.*;

public class Graph  extends JFrame{
	
	public Graph(Tree tree) {
		
		super("MethodInvocation graph");
		
		mxGraph graph = new mxGraph();
		Object parent = graph.getDefaultParent();
		
		graph.getModel().beginUpdate();
		
		
		Object Root = graph.insertVertex(parent, null, tree.className, 20, 20, 100, 100);
		
		for (Map.Entry<String, Set<Node>> declarationInvocation : tree.declarationInvocations.entrySet()) {
			
			Object Node = graph.insertVertex(parent,  null,  declarationInvocation.getKey(), 50, 150, 100, 100);
			
			graph.insertEdge(parent,  null,  null,  Root, Node);
			
			for (Node treeNode : declarationInvocation.getValue()) {
				
				Object methodInvocationName = graph.insertVertex(parent,  null, treeNode.methodName, 40, 300, 50, 50);
				
				graph.insertEdge(parent,  null, null, Node, methodInvocationName);
				
			}
			
		}
		
		
		graph.getModel().endUpdate();
		
		mxGraphComponent graphComponent = new mxGraphComponent(graph);
		getContentPane().add(graphComponent);
				
				
	}

}
