package astparser;
import java.awt.Dimension;
import java.io.BufferedReader;

import org.jgrapht.*;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.*;
import org.jgrapht.io.ComponentNameProvider;
import org.jgrapht.io.DOTExporter;
import org.jgrapht.io.GraphExporter;
// import org.jgrapht.io.*;
import org.jgrapht.traverse.*;

import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.swing.mxGraphComponent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.rmi.server.ExportException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.JApplet;
import javax.swing.JFrame;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.TypeDeclaration;


 

public class Main extends JApplet{
	
	private static final Dimension DEFAULT_SIZE = new Dimension(1000, 800);

    private JGraphXAdapter<String, DefaultEdge> jgxAdapter;
 
	private final  static int PERCENT = 10;
	
	private final  static int X = 2;
	

	
	
	private static List<String> packages = new ArrayList<String>();	
	private static List<String> Classes = new ArrayList<String>();
	
	private static int maximumMethodParameter = 0;
	
	private static Map<String, Tree> tree = new TreeMap<String, Tree>();
	
	private static  TreeSet<CustomType> classWithManyMethods = new TreeSet<CustomType>();
	private static  TreeSet<CustomType> classWithManyAttributes = new TreeSet<CustomType>();
	private static  TreeSet<CustomType> methodsWithLargestCode = new TreeSet<CustomType>();

	
	
	
	public static  void parse(String str) {
		
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setSource(str.toCharArray());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
 
		final CompilationUnit cu = (CompilationUnit) parser.createAST(null);
 
		cu.accept(new ASTVisitor() {
 

		
			public boolean visit (PackageDeclaration node ) {
				
			
				if (packages.contains(node.getName().toString())) {
					return true;
				}
				else {
					packages.add(node.getName().toString());
		
				}
				
				return true;
							
			}
			 		
			public boolean visit(TypeDeclaration node) {
				
				SimpleName className = node.getName();
				Classes.add(className.toString());
			
				if (tree.get(node.getName().toString()) == null) {
					tree.put(node.getName().toString(), new Tree(node.getName().toString()));
				}
				
				
				int localLineCounter = node.toString().length() - node.toString().replaceAll(System.getProperty("line.separator"), "").length();
			
				
				if (localLineCounter == 0) {
					localLineCounter += node.toString().length() - node.toString().replace("\n", "").length();
				}
	
				classWithManyAttributes.add(new CustomType (className.toString(), node.getFields().length));
				
				if (node.getMethods().length > X) {
				}
				
				classWithManyMethods.add(new CustomType(className.toString(), node.getMethods().length));

				for (MethodDeclaration m : node.getMethods()) {
					
						if (m.parameters().size() > maximumMethodParameter)
							maximumMethodParameter = m.parameters().size();
					
						localLineCounter = m.getBody().toString().length() - m.getBody().toString().replace(System.getProperty("line.separator"), "").length();
					
						if(localLineCounter == 0)
						localLineCounter += m.getBody().toString().length() - m.getBody().toString().replace("\n", "").length();
					
					
				
					//	methodLineCounter += localLineCounter;
						
						
						
						methodsWithLargestCode.add(new CustomType((m.getName() + " - " +m.getReturnType2() + " - " +
						m.parameters()), localLineCounter, m.getName().toString()));
						
						
						
						if(tree.get(node.getName().toString()).declarationInvocations
								.get(m.getName().toString()) == null) {						
							tree.get(node.getName().toString()).declarationInvocations
							.put(m.getName().toString(), new TreeSet<Node>()); 							
						}														 					
				}		
				return true;				
			}									
			public boolean visit (MethodInvocation methodInvocation) {
				
				try {
					
					ASTNode parent = methodInvocation.getParent();							
							
					if(parent == null) {
						return true;
					}
					
					while(parent.getNodeType() != 31) {
						parent = parent.getParent();
						
						if (parent == null) {
							return true;
						}
					}
					
					MethodDeclaration methodDeclaration = (MethodDeclaration) parent;
					
					
					parent = methodInvocation.getParent();
					
					if (parent == null) {
						return true;
					}
					
					while (parent.getNodeType() != 55) {
						parent = parent.getParent();
					}
					
					
					TypeDeclaration typeDeclaration = (TypeDeclaration) parent;
					
					if (tree.get(typeDeclaration.getName().toString()).declarationInvocations
							.get(methodDeclaration.getName().toString()) == null) {
							tree.get(typeDeclaration.getName().toString()).declarationInvocations
									.put(methodDeclaration.getName().toString(), new TreeSet<Node>());
					}
					
					tree.get(typeDeclaration.getName().toString()).declarationInvocations
							.get(methodDeclaration.getName().toString())
							.add(new Node ("", methodInvocation.getName().toString()));
							
					
					Expression expression = methodInvocation.getExpression();
					
					if (expression != null) {
						
						ITypeBinding typeBinding = expression.resolveTypeBinding();
						
						if (typeBinding != null) {
							
						}
						
						
					}
				}
				catch(NullPointerException nullPointerException) {
					nullPointerException.printStackTrace();
				}
				return true;
					
			}
		});
 
	}

	private static String renderStringGraph(Graph<String, MyWeightedEdge> stringGraph)
	        throws ExportException, org.jgrapht.io.ExportException
	    {
	       
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
	        GraphExporter<String, MyWeightedEdge> exporter =
	        new DOTExporter<>(vertexIdProvider, vertexLabelProvider, null);
	        Writer writer = new StringWriter();
	        exporter.exportGraph(stringGraph, writer);
	        System.out.println(writer.toString());
	        
	        return writer.toString();
	    }
	
	
	
	static int Metrique (String class1, String class2) {
		
	ArrayList<String> a_b = new ArrayList<String>();
	ArrayList<String> b_a = new ArrayList<String>();	
	
		
		for (Map.Entry<String, Tree> entry : tree.entrySet()) {
	 			 
			 if (entry.getKey().equals(class1)) {					 								 
				for (Map.Entry<String, Set<Node>> declarationInvocation :entry.getValue().declarationInvocations.entrySet()) {
					
					String Node = declarationInvocation.getKey().toString();
					//System.out.println(Node);
					
					for (Node treeNode : declarationInvocation.getValue()) {
						String MethodCalled  = treeNode.methodName;
																
						for (Map.Entry<String,Tree> entry2 : tree.entrySet()) {
							if (entry2.getKey().equals(class2)) {
								for (Map.Entry<String, Set<Node>> declarationInvocation2 :entry2.getValue().declarationInvocations.entrySet()) {
									String Node2 = declarationInvocation2.getKey().toString();
									
									
									if (Node2.equals(MethodCalled)) {							
										a_b.add(Node2);
									}									
								}								
							}
						}
					}
				}
				
			}	
		}
		
		for (Map.Entry<String, Tree> entry : tree.entrySet()) {
			 
			 if (entry.getKey().equals(class2)) {					 								 
				for (Map.Entry<String, Set<Node>> declarationInvocation :entry.getValue().declarationInvocations.entrySet()) {
					
					String Node = declarationInvocation.getKey().toString();
					
					
					for (Node treeNode : declarationInvocation.getValue()) {
						String MethodCalled  = treeNode.methodName;
																
						for (Map.Entry<String,Tree> entry2 : tree.entrySet()) {
							if (entry2.getKey().equals(class1)) {
								for (Map.Entry<String, Set<Node>> declarationInvocation2 :entry2.getValue().declarationInvocations.entrySet()) {
									String Node2 = declarationInvocation2.getKey().toString();
									
									
									if (Node2.equals(MethodCalled)) {							
										b_a.add(Node2);
									}
									
								}
								
							}
						}
					}
				}
				
			}	
		}
		System.out.println("Méthodes de la classe " + class2  + " appelées par la classe " + class1 +" "+  a_b);
		System.out.println("Méthodes de la classe " + class1  + " appelées par la classe " + class2 +" "+ b_a);
		
		int nbAppels = a_b.size() + b_a.size();
		System.out.println("Nombre d'appels de méthodes entres les 2 classes : " +nbAppels);
		return nbAppels;
	}
	
	
	private static Graph createWeightedGraph(List<String> Classes) throws ExportException, org.jgrapht.io.ExportException, FileNotFoundException {
		
		String root = "root";
		
		Graph <String, MyWeightedEdge> g = new DefaultDirectedWeightedGraph(MyWeightedEdge.class);
		
		g.addVertex(root);
		
		for (String classes : Classes) {
						
			
			g.addVertex(classes);
			MyWeightedEdge e1 = (MyWeightedEdge) g.addEdge(root, classes);
			//g.setEdgeWeight(e1,5);
			
			for( String className2 : Classes) {
				if (classes != className2) {
								
					if (g.getEdge(classes, className2) == null) {
						g.addVertex(className2);
						
						int m = Metrique (className2 , classes);
						if (m != 0) {
						  MyWeightedEdge e2 = (MyWeightedEdge) g.addEdge(classes, className2);
						  g.setEdgeWeight(e2, Metrique(className2, classes));
						}				
					}										
				}				
			} 											 		
		}
				
		System.out.println(g.toString());			
		PrintWriter writer = new PrintWriter("graph.dot");
		writer.print(renderStringGraph(g));
		writer.close();
		return g;
	}
	
	
public void init(List<String> classes) throws ExportException, FileNotFoundException, org.jgrapht.io.ExportException {
		
		
		Graph g = createWeightedGraph(classes);
		
		 jgxAdapter = new JGraphXAdapter<>(g);

	        setPreferredSize(DEFAULT_SIZE);
	        mxGraphComponent component = new mxGraphComponent(jgxAdapter);
	        component.setConnectable(false);
	        component.getGraph().setAllowDanglingEdges(false);
	        getContentPane().add(component);
	        mxCircleLayout layout = new mxCircleLayout(jgxAdapter);
	        
	        int radius = 100;
	        layout.setX0((DEFAULT_SIZE.width / 2.0) - radius);
	        layout.setY0((DEFAULT_SIZE.height / 2.0) - radius);
	        layout.setRadius(radius);
	        layout.setMoveCircle(true);
	        
	        layout.execute(jgxAdapter.getDefaultParent());
		
	}
	
		
	
	//read file content into a string
	public static  String readFileToString(String filePath) throws IOException {
		StringBuilder fileData = new StringBuilder(1000);
		BufferedReader reader = new BufferedReader(new FileReader(filePath));
 
		char[] buf = new char[10];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) {
		//	System.out.println(numRead);
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			buf = new char[1024];
		}
 
		reader.close();
 
		return  fileData.toString();	
	}
 
	//loop directory to get file list
	public static void ParseFilesInDir(String path) throws IOException{
		
		File dirs = new File(".");
		
	//	File dirs = new File("/auto_home/bvolle/workspace/Resolution");
	//	File dirs = new File("C:\\Users\\Baptiste\\Desktop\\M2\\METHODES_FORMELLES\\Resolution");
	//	File dirs = new File("C:\\Users\\Baptiste\\eclipse-workspace\\Multiplication");	
	//  File dirs = new File("C:\\Users\\Baptiste\\eclipse-workspace\\Rename_refactored");
			        
		String dirPath = path;
	
		File root = new File(dirPath);

		File[] files = root.listFiles ( );
		String filePath = null;
 
		 for (File f : files ) {
			 filePath = f.getAbsolutePath();
			 if(f.isFile()){
				 parse(readFileToString(filePath));
			 }
			 else {
				 ParseFilesInDir(f.getCanonicalPath());
			 }
		 }
	}
 
	public static void main(String[] args) throws IOException, org.jgrapht.io.ExportException {
		
		
		 ParseFilesInDir("C:\\\\Users\\\\Baptiste\\\\eclipse-workspace\\\\Multiplication");
		 

		 Main applet = new Main();
	        applet.init(Classes);

			JFrame frame = new JFrame();
	        frame.getContentPane().add(applet);
	        frame.setTitle("TEST");
	        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        frame.pack();
	        frame.setVisible(true);
	}
	
}
					 		 		
	

