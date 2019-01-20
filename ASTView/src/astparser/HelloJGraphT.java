 package astparser;
/*
import com.mxgraph.examples.swing.*;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph; */

import java.util.Map;

import org.jgrapht.*;
import org.jgrapht.graph.*;
import org.jgrapht.io.*;
import org.jgrapht.traverse.*;

import java.io.*;
import java.net.*;
import java.rmi.server.ExportException;
import java.util.*;
import java.util.Set;

import javax.swing.JFrame;

// import com.mxgraph.*;

public final class HelloJGraphT {
	
	
	private HelloJGraphT()
    {
    }
	
	/*
	public Graph( Tree tree ) {
	
		
	}
	*/
	
	public static void main(String[] args)
	        throws URISyntaxException,
	        ExportException, org.jgrapht.io.ExportException
	    {
		
			
	        Graph<String, DefaultEdge> stringGraph = createStringGraph();

	        // note undirected edges are printed as: {<v1>,<v2>}
	        System.out.println("-- toString output");
	        System.out.println(stringGraph.toString());
	        System.out.println();
	        renderStringGraph(stringGraph);
	    }
	    
	    private static Graph<String, DefaultEdge> createStringGraph()
	    {
	        Graph<String, DefaultEdge> g = new SimpleGraph<>(DefaultEdge.class);

	        String v1 = "v1";
	        String v2 = "v2";
	        String v3 = "v3";
	        String v4 = "v4";

	        // add the vertices
	        g.addVertex(v1);
	        g.addVertex(v2);
	        g.addVertex(v3);
	        g.addVertex(v4);

	        // add edges to create a circuit
	        g.addEdge(v1, v2);
	        g.addEdge(v2, v3);
	        g.addEdge(v3, v4);
	        g.addEdge(v4, v1);

	        return g;
	    }
	    
	    
	    private static void renderStringGraph(Graph<String, DefaultEdge> stringGraph)
		        throws ExportException, org.jgrapht.io.ExportException
		    {

		        // use helper classes to define how vertices should be rendered,
		        // adhering to the DOT language restrictions
		        ComponentNameProvider<String> vertexIdProvider = new ComponentNameProvider<String>()
		        {
		           

					@Override
					public String getName(String st) {
						return st.toString();
					}
		        };
		        ComponentNameProvider<String> vertexLabelProvider = new ComponentNameProvider<String>()
		        {
		            public String getName(String st)
		            {
		                return st.toString();
		            }
		        };
		        GraphExporter<String, DefaultEdge> exporter =
		        new DOTExporter<>(vertexIdProvider, vertexLabelProvider, null);
		        Writer writer = new StringWriter();
		        exporter.exportGraph(stringGraph, writer);
		        System.out.println(writer.toString());
		    } 
	    
	    
	    
	}
	
		/*
		
		super("MethodInvocation graph");
		
		
		int x1 = 40;
		int x = 40;
		int y = 300;
		
		mxGraph graph = new mxGraph();
		Object parent = graph.getDefaultParent();
		
		graph.getModel().beginUpdate();
		
		
		Object Root = graph.insertVertex(parent, null, tree.className, 300, 20, 100, 100);

	 for (Map.Entry<String, Set<Node>> declarationInvocation : tree.declarationInvocations.entrySet()) {
			
			
			Object Node = graph.insertVertex(parent,  null,  declarationInvocation.getKey(), x1, 150, 100, 100);
					
			graph.insertEdge(parent,  null,  null,  Root, Node);
			
			x1+= 105;
			
			x = x1;
			
			
			for (Node treeNode : declarationInvocation.getValue()) {
				
							
				Object methodInvocationName = graph.insertVertex(parent,  null, treeNode.methodName, x, y, 50, 50);
				
				graph.insertEdge(parent,  null, null, Node, methodInvocationName);
				
				x += 55;
				//y += 55;
				
				
			}
			
		}
		
	
		graph.getModel().endUpdate();
		
		mxGraphComponent graphComponent = new mxGraphComponent(graph);
		getContentPane().add(graphComponent);
				
			*/	
	


