package spoon;
import java.awt.Dimension;
import java.awt.List;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.rmi.server.ExportException;
import java.util.ArrayList;

import javax.swing.JApplet;
import javax.swing.JFrame;

import org.jgrapht.Graph;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.io.ComponentNameProvider;
import org.jgrapht.io.DOTExporter;
import org.jgrapht.io.GraphExporter;

import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.swing.mxGraphComponent;

import spoon.MyWeightedEdge;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtComment;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.TypeFilter;

public class main extends JApplet {
	
	private static final Dimension DEFAULT_SIZE = new Dimension(1000, 800);

    private JGraphXAdapter<String, DefaultEdge> jgxAdapter;
 
	private final  static int PERCENT = 10;
	
	
	public static int Metrique(CtClass<?>class1 , CtClass<?>class2) {

		
		
			int nbAppels1_2 = 0;
			int nbAppels2_1 = 0;
			int nbAppels = 0;

				
				System.out.println("CLASSE : " + class1.getSimpleName());
				
				int sizeClass1 = class1.filterChildren(new TypeFilter<CtInvocation>(CtInvocation.class)).list().size();
				System.out.println(sizeClass1);
				for ( int i = 0 ; i < sizeClass1 ; i++) {
					
					CtInvocation<?> displayInvocation = (CtInvocation)class1.filterChildren(new TypeFilter<CtInvocation>(CtInvocation.class)).list().get(i);
					CtExecutableReference<?> ctExecutableReference = displayInvocation.getExecutable();
					CtClass classs = (CtClass) ctExecutableReference.getExecutableDeclaration().getParent();
					System.out.println(classs.getSimpleName());
					if (classs.getSimpleName().equals(class2.getSimpleName())) {
						nbAppels1_2 ++;
					}
				}
				
				
				System.out.println("CLASSE : " + class1.getSimpleName());
				
				int sizeClass2 = class2.filterChildren(new TypeFilter<CtInvocation>(CtInvocation.class)).list().size();
				System.out.println(sizeClass2);
				for ( int i = 0 ; i < sizeClass2 ; i++) {
					
					CtInvocation<?> displayInvocation = (CtInvocation)class2.filterChildren(new TypeFilter<CtInvocation>(CtInvocation.class)).list().get(i);
					CtExecutableReference<?> ctExecutableReference = displayInvocation.getExecutable();
					CtClass classs = (CtClass) ctExecutableReference.getExecutableDeclaration().getParent();
					System.out.println(classs.getSimpleName());
					if (classs.getSimpleName().equals(class1.getSimpleName())) {
						nbAppels2_1 ++;
					}
				}
				
				nbAppels = nbAppels2_1 + nbAppels1_2;
				System.out.println("Metrique entre la classe : "+ class1.getSimpleName()+ " et la classe : "+class2.getSimpleName());
				System.out.println(nbAppels);
				return nbAppels;
	}
	
private static Graph createWeightedGraph(ArrayList<CtClass<?>> Classes) throws ExportException, org.jgrapht.io.ExportException, FileNotFoundException {
		
		String root = "root";
		
		Graph <String, MyWeightedEdge> g = new DefaultDirectedWeightedGraph(MyWeightedEdge.class);
		
		g.addVertex(root);
		
		for (CtClass classes : Classes) {
						
			
			g.addVertex(classes.getSimpleName());
			MyWeightedEdge e1 = (MyWeightedEdge) g.addEdge(root, classes.getSimpleName());
			//g.setEdgeWeight(e1,5);
			
			for( CtClass className2 : Classes) {
				if (classes.getSimpleName() != className2.getSimpleName()) {
								
					if (g.getEdge(classes.getSimpleName(), className2.getSimpleName()) == null) {
						g.addVertex(className2.getSimpleName());
						
						int m = Metrique (className2 , classes);
						if (m != 0) {
						  MyWeightedEdge e2 = (MyWeightedEdge) g.addEdge(classes.getSimpleName(), className2.getSimpleName());
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
	
	
public void init(ArrayList<CtClass<?>> classes) throws ExportException, FileNotFoundException, org.jgrapht.io.ExportException {
		
		
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

private static String renderStringGraph(Graph<String, MyWeightedEdge> stringGraph)
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
        GraphExporter<String, MyWeightedEdge> exporter =
        new DOTExporter<>(vertexIdProvider, vertexLabelProvider, null);
        Writer writer = new StringWriter();
        exporter.exportGraph(stringGraph, writer);
        System.out.println(writer.toString());
        
        return writer.toString();
    }

	public static void main(String[] args) throws ExportException, FileNotFoundException, org.jgrapht.io.ExportException {


		Launcher launcher = new Launcher();
		
		launcher.addInputResource("\\C:\\Users\\Baptiste\\eclipse-workspace\\Multiplication");
		
		launcher.buildModel();
		
		CtModel model = launcher.getModel();
		
		
	//	List<CtType<?>> classesList = launcher.getFactory().Class().getAll();		
		ArrayList<CtClass<?>> ListClass = new ArrayList<CtClass<?>>();
		
		for (CtType<?> type : model.getAllTypes()) {
			if (type instanceof CtClass<?>) {
				CtClass<?> class_ = (CtClass<?>)type;
				
				ListClass.add(class_);	
			}
		}
		
		main applet = new main();
        applet.init(ListClass);

		JFrame frame = new JFrame();
        frame.getContentPane().add(applet);
        frame.setTitle("TEST");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
		
		
		
		for (CtClass<?> _class_  : ListClass) {
			for (CtClass<?> _class : ListClass) {
				if (_class_ != _class) {
					Metrique(_class_, _class);
				}				
			}			
		}		
	}
}
		


