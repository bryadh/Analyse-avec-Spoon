package analyse;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;

import spoon.Launcher;
import spoon.MavenLauncher;
import spoon.compiler.Environment;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.reflect.visitor.filter.TypeFilter;

public class SpoonMainDynamique {

	public static void main(String[] args) {
		
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
			launcher.addInputResource(experiment_source_code + "/src");
		}
		
		// Setting the environment for Spoon
		Environment environment = launcher.getEnvironment();
		environment.setCommentEnabled(true); // represent the comments from the source code in the AST
		environment.setAutoImports(true); // add the imports dynamically based on the typeReferences inside the AST nodes.
//		environment.setComplianceLevel(0); // sets the java compliance level.
		
		System.out.println("Run Launcher and fetch model.");
		launcher.run(); // creates model of project
		CtModel model = launcher.getModel(); // returns the model of the project
		
		
		// basic type filter to retrive all methods in your model
		List<CtMethod> methodList = model.getElements(new TypeFilter<CtMethod>(CtMethod.class));
		List<CtClass> classList = model.getElements(new TypeFilter<CtClass>(CtClass.class));
		List<CtPackage> packageList = model.getElements(new TypeFilter<CtPackage>(CtPackage.class));
		
		List<String> listTypes = new ArrayList<String>();
		
		for(CtClass cls : classList) {
			listTypes.add(cls.getSimpleName());
		}
		
		System.out.println(listTypes);
		System.out.println("====================================");
		
		for(CtClass cls : classList) {
			
			Set<CtMethod> methodeSet = cls.getMethods();
			for(CtMethod md : methodeSet) {
				
				List<CtInvocation> refs = md.getElements(new TypeFilter < CtInvocation > (CtInvocation.class));
				
				if(refs.isEmpty()) {
					
					System.out.println(cls.getSimpleName()+":"+md.getSimpleName()+" has no refs");
					System.out.println("_____________________________________________________________________");
					
				} else {
					
					for(CtInvocation ref : refs) {
						
						
						try {
							
							if(listTypes.contains(ref.getTarget().getType().toString())) {
								System.out.println(cls.getSimpleName()+":"+md.getSimpleName()+" has ref target ->"+ref.getTarget().getType().toString());
								System.out.println("_____________________________________________________________________");
								
								CtBlock mdBlock = md.getBody();
								
								CtStatement NewInstruction = launcher.getFactory().Code().createCodeSnippetStatement("analyse.Analyse.printAnalysis(\""+ref.getTarget().getType().toString()+"\",\""+cls.getSimpleName()+"\")");
								mdBlock.addStatement(0,NewInstruction);
								md.setBody(mdBlock);
								
								
							}else {
								System.out.println("NO TARGET");
								System.out.println("_____________________________________________________________________");
								
							}
							
						} catch (NullPointerException e) {
							System.out.println("NO TARGET");
							System.out.println("_____________________________________________________________________");
						}
						
					}
				}
					
			}
			
		}
		
		
		try {
			launcher.setSourceOutputDirectory("C:\\Users\\home\\Java_workspace\\AnalyseAvecSpoon\\analyseDynamiqueOUT");
			launcher.prettyprint();
			
		} catch(Exception e) {
			System.out.println("ERROR CREATING OUTPUT FILE");
			e.printStackTrace();
		}
		
	}
}
