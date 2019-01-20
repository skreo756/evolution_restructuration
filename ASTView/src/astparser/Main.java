package astparser;
import java.awt.Dimension;
import java.io.BufferedReader;

import org.jgrapht.*;
import org.jgrapht.demo.JGraphXAdapterDemo;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.*;
import org.jgrapht.io.ComponentNameProvider;
import org.jgrapht.io.DOTExporter;
import org.jgrapht.io.GraphExporter;
// import org.jgrapht.io.*;
import org.jgrapht.traverse.*;

import com.mxgraph.swing.mxGraphComponent;

import javax.swing.JApplet;
import javax.swing.JFrame;

import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.view.mxGraph;

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


 

public class Main extends JApplet {
	
	private static JGraphXAdapter<String, DefaultEdge> jgxAdapter;
	
	private static final Dimension DEFAULT_SIZE = new Dimension(1000, 800);
 
	private final  static int PERCENT = 10;
	
	private final  static int X = 2;
	
	private static  int classCounter = 0;
	private static  int lineCounter = 0;
	private static  int methodCounter = 0;
	
	

	
	private static List<String> percentClassWithManyMethods = new ArrayList<String>();
	private static List<String> percentClassWithManyAttributes = new ArrayList<String>();

	private static Collection<String> classWithManyMethodsAndAttributes = new ArrayList<String>();	
	private static Collection<String> classWithMoreThanXMethods = new ArrayList<String>();
	private static Collection<String> percentMethodsWithLargestCode = new ArrayList<String>();
	
	private static List<String> ClassWithManyAttributesName = new ArrayList<String>();
	private static List<String> ClassWithManyMethodsName = new ArrayList<String>();
	
	
	private static List<String> packages = new ArrayList<String>();	
	private static List<String> Classes = new ArrayList<String>();
	
	private static int maximumMethodParameter = 0;
	
	private static Map<String, Tree> tree = new TreeMap<String, Tree>();
	
	private static  TreeSet<CustomType> classWithManyMethods = new TreeSet<CustomType>();
	private static  TreeSet<CustomType> classWithManyAttributes = new TreeSet<CustomType>();
	private static  TreeSet<CustomType> methodsWithLargestCode = new TreeSet<CustomType>();
	
	private static  int attributeCounter = 0;
	private static  int methodLineCounter = 0;
	
	
	
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
				
				lineCounter += localLineCounter;
				classCounter++;
				
				attributeCounter += node.getFields().length;
				
				classWithManyAttributes.add(new CustomType (className.toString(), node.getFields().length));
				
				if (node.getMethods().length > X) {
					classWithMoreThanXMethods.add(className.toString());
				}
				
				classWithManyMethods.add(new CustomType(className.toString(), node.getMethods().length));
				
				methodCounter += node.getMethods().length;
				
				
				for (MethodDeclaration m : node.getMethods()) {
					
						if (m.parameters().size() > maximumMethodParameter)
							maximumMethodParameter = m.parameters().size();
					
						localLineCounter = m.getBody().toString().length() - m.getBody().toString().replace(System.getProperty("line.separator"), "").length();
					
						if(localLineCounter == 0)
						localLineCounter += m.getBody().toString().length() - m.getBody().toString().replace("\n", "").length();
					
					
				
						methodLineCounter += localLineCounter;
						
						
						
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
	
	
	private static void percentOfClassWithManyAttributs()
	{
		int numberToSelect = (classCounter * 50) / 100;
		int counter = 0;
		

		for(CustomType customType : classWithManyAttributes)
			if(counter != numberToSelect)
			{
				percentClassWithManyAttributes.add(customType.toString());
				counter++;
				ClassWithManyAttributesName.add(customType.getName());
			}
			else {
				System.out.println(percentClassWithManyAttributes);
				return;
				
			}
				
	}
	
	
	
	private static void percentOfClassWithManyMethods() {
		
		int numberToSelect = (classCounter * 50) /100;
		int counter = 0;
		
		for (CustomType customType : classWithManyMethods)
			if (counter != numberToSelect)
			{
				percentClassWithManyMethods.add(customType.toString());
				counter++;
				ClassWithManyMethodsName.add(customType.getName());
			}
			else {
				System.out.println(percentClassWithManyMethods);
				return;
			}
		
	}
	
	private static void percentOfMethodsWithLargestCode()
	{
		int numberToSelect = (methodCounter * PERCENT) / 100;

		int counter = 0;
		
		for(CustomType customType : methodsWithLargestCode)
			if(counter != numberToSelect)
			{
				percentMethodsWithLargestCode.add(customType.toString());
				counter++;
			}
			else {
				System.out.println(percentMethodsWithLargestCode);
				return;
			}			
	}
	
	
	private static void classWithManyAttributesAndMethods() {
		for (String methods : ClassWithManyMethodsName) {
			for (String attributes : ClassWithManyAttributesName) {
				if (methods.equals(attributes)) {
					classWithManyMethodsAndAttributes.add(methods.toString());				
				}
			}
					
		}
		System.out.println(classWithManyMethodsAndAttributes);		
	}
	
	private String renderStringGraph(Graph<String, DefaultEdge> stringGraph)
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
	        
	        return writer.toString();
	    }
	
	
	private Graph createGraph(List<String> Classes) throws ExportException, org.jgrapht.io.ExportException, FileNotFoundException {
		
		String root = "root";
		
		ListenableGraph <String, DefaultEdge> g = new DefaultListenableGraph<>(new DefaultDirectedGraph<>(DefaultEdge.class));
		
		g.addVertex(root);
		
		for (String classes : Classes) {
		
					
			String className = classes;
			
			g.addVertex(className);
			g.addEdge(root, className);
			
			 for (Map.Entry<String, Tree> entry : tree.entrySet()) {
				 				 			 			 
				 if (entry.getKey().equals(className)) {					 								 
					for (Map.Entry<String, Set<Node>> declarationInvocation :entry.getValue().declarationInvocations.entrySet()) {
								
						String Node = declarationInvocation.getKey().toString();		
						
						g.addVertex(Node);
						g.addEdge(className, Node);
						
						
						for (Node treeNode : declarationInvocation.getValue()) {
							
							g.addVertex(treeNode.methodName);
							
							g.addEdge(Node, treeNode.methodName);
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
		
		
		Graph g = createGraph(classes);
		
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
		 
		//1
		System.out.println("Nombre de classe : " + classCounter);
		System.out.println("Liste des classes : " + Classes);
		
		//2
		System.out.println("Nombre de lignes de code : "+ lineCounter);
		
		//3
		System.out.println("Nombre de méthodes : " + methodCounter);
				
		//4
		System.out.println("Nombre de packages : " + packages.size());
		
		System.out.println("Liste des packages : " + packages);
		
		//5
		System.out.println("Nombre moyen de méthodes par classe : "+ methodCounter/classCounter);
		
		//6
		System.out.println("Nombre moyen de lignes de code par méthode : "+ methodLineCounter/methodCounter);
			
		//7
		System.out.println("Nombre moyen d'attributs  par classe : " +attributeCounter/classCounter);
		
		//8
		System.out.println("10% de slasses possédant le plus de méthodes :");		
		percentOfClassWithManyMethods();
				
		//9
		System.out.println("10% de classes possédant le plus d'attributs :");		
		percentOfClassWithManyAttributs();		
				
		//10	
		System.out.println("Classe possédants le plus de méthodes ET d'attributs : ");	
		classWithManyAttributesAndMethods();
		
		//11
		System.out.println("Classe possédants plus que " + X + " méthodes : " + classWithMoreThanXMethods);
			
		//12
		System.out.println("Methodes possédants le plus de code : ");  		
		percentOfMethodsWithLargestCode();
		
		
		//13
		System.out.println("Nombre maximum de paramètre : " + maximumMethodParameter);
		

		
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
					 		 		
	

