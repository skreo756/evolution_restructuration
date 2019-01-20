package spoon;


import spoon.Launcher;
import spoon.reflect.declaration.CtClass;

public class HelloWorldProcessorTest {
	
	public static void  main(String args[]) {
		
		CtClass l = Launcher.parseClass("class A { void m() { System.out.println(\"yeah\");} }");
		System.out.println("yo");
	}

}