package astparser;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

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


 

public class Main {
 
	private final  static int PERCENT = 10;
	
	private final  static int X = 2;
	
	private static  int classCounter = 0;
	private static  int lineCounter = 0;
	private static  int methodCounter = 0;
//	private static int packageCounter = 0;
	
	
	
	// private  int methodAverage;
	// private  int codeLineMethodAverage = 0;
	// private  int attributeAverage = 0;
	
	private static List<String> percentClassWithManyMethods = new ArrayList<String>();
	private static  List<String> percentClassWithManyAttributes = new ArrayList<String>();

	private static  Collection<String> classWithManyMethodsAndAttributes = new ArrayList<String>();
	
	private static  Collection<String> classWithMoreThanXMethods = new ArrayList<String>();
	private static  Collection<String> percentMethodsWithLargestCode = new ArrayList<String>();
	
	private static List<String> ClassWithManyAttributesName = new ArrayList<String>();
	private static List<String> ClassWithManyMethodsName = new ArrayList<String>();
	
	
	private static  List<String> packages = new ArrayList<String>();
	
	private static int maximumMethodParameter = 0;

	
	private  Map<String, Collection<String>> classMethods = new TreeMap<String, Collection<String>>();
	private  Map<String, Collection<String>> methodMethods = new TreeMap<String, Collection<String>>();
	
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
 
			Set names = new HashSet();
		
			public boolean visit (PackageDeclaration node ) {
				if (packages.contains(node.getName().toString())) {
					return true;
				}
				else {
					packages.add(node.getName().toString());
					//	packageCounter++;
				}
				
				return true;
			}
			
			
			
			public boolean visit(TypeDeclaration node) {
				
				SimpleName className = node.getName();
				
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
						
						System.out.println(tree);
						
						if(tree.get(node.getName().toString()).declarationInvocations
								.get(m.getName().toString()) == null) {
							System.out.println(tree.get(node.getName().toString()).declarationInvocations
								.get(m.getName().toString()));
							
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
							.get(methodDeclaration.getName().toString()) == null)
							tree.get(typeDeclaration.getName().toString()).declarationInvocations
									.put(methodDeclaration.getName().toString(), new TreeSet<Node>());
					
					tree.get(typeDeclaration.getName().toString()).declarationInvocations
							.get(methodDeclaration.getName().toString())
							.add(new Node ("", methodInvocation.getName().toString()));
							
					
					Expression expression = methodInvocation.getExpression();
					
					if (expression != null) {
						
						ITypeBinding typeBinding = expression.resolveTypeBinding();
						
						if (typeBinding != null) {
							System.out.println(typeBinding.toString());
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
	
	
	
	private static void percentOfClassWithManMethods() {
		
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
 
	public static void main(String[] args) throws IOException {
		
		
		 ParseFilesInDir("C:\\\\Users\\\\Baptiste\\\\eclipse-workspace\\\\Multiplication");
		System.out.println("Nombre de classe : " + classCounter);
		
		System.out.println("Nombre de lignes de code : "+ lineCounter);
		
		System.out.println("Nombre de méthodes : " + methodCounter);
		
		System.out.println("Nombre de packages : " + packages.size());
		
		System.out.println("Liste des packages : " + packages);
		
		System.out.println("Nombre moyen de lignes par méthode : "+ methodLineCounter/methodCounter);
		
		System.out.println("Nombre moyen de méthodes par classe : "+ methodCounter/classCounter);
		
		
		
		System.out.println("Nombre moyen d'attributs  par classe : " +attributeCounter/classCounter);
		
		
		
		
		System.out.println("Classes possédants le plus d'attributs :");		
		percentOfClassWithManyAttributs();		
		
		System.out.println("Classes possédants le plus de méthodes :");		
		percentOfClassWithManMethods();
			
		System.out.println("Classe possédants plus que " + X + " méthodes : " + classWithMoreThanXMethods);
		
		System.out.println("Methodes possédants le plus de code : ");  		
		percentOfMethodsWithLargestCode();
		
		
		System.out.println("Classe possédants le plus de méthodes ET d'attributs : ");	
		classWithManyAttributesAndMethods();
		
		 System.out.println("Nombre maximum de paramètre : " + maximumMethodParameter);
		 
		 String className = "addition";
		
		 for (Map.Entry<String, Tree> entry : tree.entrySet()) {
			 if (entry.getKey().equals(className)) {
				 Graph frame = new Graph(entry.getValue());
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setSize(800, 740);
				frame.setVisible(true);
				 
			 }
		 }
			 
		
		
		
		
		
		
		
	}
}
