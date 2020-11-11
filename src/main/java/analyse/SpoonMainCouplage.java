package analyse;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
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

import spoon.Launcher;
import spoon.MavenLauncher;
import spoon.compiler.Environment;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.filter.TypeFilter;

public class SpoonMainCouplage {

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
		
		List<CtClass> classList = model.getElements(new TypeFilter<>(CtClass.class));
		List<CtMethod> methodList = model.getElements(new TypeFilter<>(CtMethod.class));
		
		List<String> classNameList = new ArrayList<>();
		
		PrintWriter writer = new PrintWriter(new FileWriter("outCouplage/demo.txt"));
		
		for(CtClass c : classList) {
			
			for(CtMethod mCaller : c.getElements(new TypeFilter<>(CtMethod.class))) {
				
				String caller = c.getSimpleName()+":"+mCaller.getSimpleName();
				
				System.out.println("________________ "+ caller+ " ________________");
				
				for(CtInvocation inv : mCaller.getElements(new TypeFilter<>(CtInvocation.class))) {
					
					CtMethod mCallee = null;
					
					try {
						mCallee = (CtMethod) inv.getExecutable().getDeclaration();
					} catch (Exception e) {
						System.out.println("\t\tProblem with "+mCallee);
						continue;
					}
					
					CtClass cCallee = null; 
					
					try {
						cCallee = mCallee.getParent(CtClass.class);
					} catch (NullPointerException e) {
						System.out.println("\t\tInvocation without parent -> "+mCallee);
						continue;
					}
					
					
					if(cCallee.getSimpleName().equals(c.getSimpleName())) {
						
						//do nothing;
						
					} else {
						String callee = cCallee.getSimpleName()+":"+mCallee.getSimpleName();
						
						System.out.println("CALLER ("+caller+") -> CALLEE ("+callee+")");
						
						writer.println(c.getSimpleName()+":"+cCallee.getSimpleName());
					}
				}
				
				System.out.println("\n\n");
			}
			
		}
		
		writer.close();
		
		
	}
}
