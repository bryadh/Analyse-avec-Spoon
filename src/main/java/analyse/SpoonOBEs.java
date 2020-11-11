package analyse;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;

import obe.IArtefact;
import obe.Invocation;
import obe.Obe;
import obe.Package;
import spoon.Launcher;
import spoon.MavenLauncher;
import spoon.compiler.Environment;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtComment;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypedElement;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.reflect.code.CtInvocationImpl;

import java.io.*;


public class SpoonOBEs {

	public static void main(String[] args) throws IOException {
		
		System.out.println("Begin Analysis");

		// Parsing arguments using JCommander
		Arguments arguments = new Arguments();
		boolean isParsed = arguments.parseArguments(args);

		// if there was a problem parsing the arguments then the program is terminated.
		if(!isParsed)
			return;
		
		// Parsed Arguments
		String experiment_source_code = arguments.getSource();
		String experiment_output_filepath = arguments.getTarget();
		
		// Load project (APP_SOURCE only, no TEST_SOURCE for now)
		Launcher launcher = null;
		if(arguments.isMavenProject() ) {
			launcher = new MavenLauncher(experiment_source_code, MavenLauncher.SOURCE_TYPE.APP_SOURCE); // requires M2_HOME environment variable
		}else {
			launcher = new Launcher();
			launcher.addInputResource(experiment_source_code + "/sources");
			System.out.println("launcher set on " + experiment_source_code.toString());
		}
		
		// Setting the environment for Spoon
		Environment environment = launcher.getEnvironment();
		environment.setCommentEnabled(true); // represent the comments from the source code in the AST
		environment.setAutoImports(true); // add the imports dynamically based on the typeReferences inside the AST nodes.
        //environment.setComplianceLevel(0); // sets the java compliance level.
		
		System.out.println("Run Launcher and fetch model.");
		launcher.run(); // creates model of project
		CtModel model = launcher.getModel(); // returns the model of the project
		System.out.println("model :"+model.toString());
		
		
		// basic type filter to retrive all methods in your model
		//List<CtMethod> methodList = model.getElements(new TypeFilter<CtMethod>(CtMethod.class));
		//List<CtClass> classList = model.getElements(new TypeFilter<CtClass>(CtClass.class));
		List<CtPackage> packageList = model.getElements(new TypeFilter<CtPackage>(CtPackage.class));
		
		
		ArrayList<obe.Obe> artefacts = new ArrayList<>();
		
		HashMap<String, obe.Method> mapMethods = new HashMap<>();
		
		System.out.println("======================= BEGIN OBE =======================");
		
		for(CtPackage p : packageList) {
			
			//OBE
			obe.Package pack = new Package(p.getSimpleName());
			System.out.println(pack.getClass().getSimpleName()+":"+pack.getName());
			ArrayList<obe.Class> obeClassList = new ArrayList<>();
			ArrayList<obe.Interface> obeInterfaceList = new ArrayList<>();
				
			List<CtClass> classList = p.getElements(new TypeFilter<>(CtClass.class));
			List<String> classSimpleNamesList = new ArrayList();
			
			for(CtClass cl : classList) {
				classSimpleNamesList.add(cl.getSimpleName());
			}
					
			for(CtClass c : classList) {
						
					//OBE
					obe.Class cls = new obe.Class(c.getSimpleName(), pack);
					System.out.println(cls.getClass().getSimpleName()+":"+cls.getName());
					obeClassList.add(cls);
					ArrayList<obe.Method> obeMethodList = new ArrayList<>();
					ArrayList<obe.Attribute> obeAttributList = new ArrayList<>();
										
					for(CtFieldReference fr : c.getDeclaredFields() ) {
						//OBE
						obe.Access access = new obe.Access(fr.getSimpleName());
						obe.Attribute attr = new obe.Attribute(fr.getSimpleName(), new obe.Class(c.getSimpleName(), pack) , access);
						System.out.println(attr.getClass().getSimpleName()+":"+attr.getName());
						obeAttributList.add(attr);
						
						artefacts.add(attr);
						
						
					}
					
					//OBE
					cls.setAttributes(obeAttributList);
					
					List<CtMethod> methodList = c.getElements(new TypeFilter<>(CtMethod.class));
					
					for(CtMethod m : methodList) {
						
						//OBE
						obe.Signature sign = new obe.Signature(m.getSignature().toString());
						obe.Method meth = new obe.Method(m.getSimpleName(), sign);
						System.out.print(meth.getClass().getSimpleName()+":"+meth.getName());
						obeMethodList.add(meth);
						
						List<CtVariable> localVariables = m.getElements(new TypeFilter<>(CtVariable.class));
						ArrayList<obe.LocalVariable> obeLocalVariableList = new ArrayList<>();
						
						System.out.print("(");
						for(CtVariable v : localVariables) {
							obe.LocalVariable localV = new obe.LocalVariable(v.getSimpleName(), v.getType().toString()); 
							obeLocalVariableList.add(localV);
							
							System.out.print(v.getSimpleName()+":"+v.getType()+", ");
						
							artefacts.add(localV);
						}
						System.out.println(")");
						
						
						meth.setVariables(obeLocalVariableList);
						
						//obe
						//List<CtInvocation> invocations = m.getElements(new TypeFilter<>(CtInvocation.class));
						
						//ArrayList<obe.Invocation> obeInvocationList = new ArrayList<>();
						
						
						/*
						System.out.println("\t========== INVOCATIONS ========== ");
						
						for(CtInvocation inv : invocations) {
							obeInvocationList.add(new obe.Invocation(meth));
								
							if(inv instanceof CtInvocationImpl) {
								System.out.println("\t"+inv.getTarget()+"."+inv.getExecutable().getSimpleName());
							}					
						}
						*/
						//meth.setInvocations(obeInvocationList);
						
						artefacts.add(meth);
						
						System.out.println("\t========== DEPENDENCIES ========== ");
					
						List<CtTypeReference<?>> listDependencies = new ArrayList<>();
						List<CtInvocation> invList = m.getElements(new TypeFilter<>(CtInvocation.class));
						
						for(CtInvocation i : invList) {
						
							listDependencies.add(i.getExecutable().getDeclaringType());
										
						}
						
						for(CtTypeReference tRef : listDependencies) {
							System.out.println("\t"+tRef);
						}
						
						if(!mapMethods.containsKey(m.getSimpleName())) {
							mapMethods.put(m.getSimpleName(), meth);
						}
							
					}
					
					cls.setMethods(obeMethodList);
					
					artefacts.add(cls);
						
			}
			
			
			artefacts.add(pack);
			
			
			System.out.println("\t========== INVOCATIONS ========== ");
			// set invocations in obe methods
			for(CtClass c : classList) {
				
				System.out.println("\tCLASS :"+c.getSimpleName());
				List<CtMethod> methList = c.getElements(new TypeFilter<>(CtMethod.class));
				
				for(CtMethod m : methList) {
					
					System.out.println("\tMETHOD :"+m.getSimpleName());
					
					
					// CtInovcations
					List<CtInvocation> invocations = m.getElements(new TypeFilter<>(CtInvocation.class));
					
					// obe invocation list 
					List<obe.Invocation> obeInvocations = new ArrayList<obe.Invocation>();
					
					
					System.out.println("\tINVOCATIONS :");
					for(CtInvocation inv : invocations) {
						
						String key = inv.getExecutable().getSimpleName();
						
						if(mapMethods.containsKey(key)) {
							
							obeInvocations.add(new Invocation(mapMethods.get(key)));
							
							System.out.println("\t"+mapMethods.get(key).getName());
						
						} 
						
						
					}
					
					System.out.println("\t__________________________");
					
					mapMethods.get(m.getSimpleName()).setInvocations(obeInvocations);
					
				}
				
					
			}
				
		}
		
		System.out.println("======================= END OBE =======================");
		
		System.out.println("Size of the artefacts list : "+artefacts.size());
		
		System.out.println("======================= BEGIN IARTEFACTS =======================");
		
		//FileWriter fw = new FileWriter("outIartefacts/product5.txt");
			
		for(obe.Obe art : artefacts) {
			
			if(art instanceof obe.IArtefact) {
				System.out.println(art.getClass().getSimpleName()+":"+art.getName());
				//fw.write(art.getClass().getSimpleName()+":"+art.getName()+"\n");
				
			}
		}
		
		//fw.close();
		
		System.out.println("======================= END IARTEFACTS =======================");
		
		
			
	}
	
}
