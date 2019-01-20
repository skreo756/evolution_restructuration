package spoon;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtComment;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;

public class main {
	
	public static void main(String[] args) {


		Launcher launcher = new Launcher();
		
		launcher.addInputResource("\\C:\\Users\\Baptiste\\eclipse-workspace\\Multiplication");
		
		launcher.buildModel();
		
		CtModel model = launcher.getModel();
			
		//Liste des packages 
		for (CtPackage p : model.getAllPackages()) {
			System.out.println("Liste des packages : "+p.getQualifiedName());
		}
		
		//Liste des classes
		for (CtType<?> s : model.getAllTypes()) {
			
			System.out.println("class "+s.getQualifiedName());
										
			// CtExecutable<?> ex=i.getExecutable().getExecutableDeclaration();//get the declaration of the called method
			// CtInvocation i=...; avant
			
			
			
		//	System.out.println(s.getPosition());
			//On récupère toutes les méthodes de chaque classe
			for (CtMethod<?> m :s.getMethods()) {
				
				//On affiche le nom
				System.out.print("   Method : "+m.getSimpleName()+ "  ");
				//On affiche les paramètres
				System.out.println("Arguments : "+m.getParameters());	
				System.out.println(m.getReferencedTypes());
			//	System.out.println(m.get);
			
			}
			
				
		}			
	}	
}

