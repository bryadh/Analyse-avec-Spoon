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

import spoon.Launcher;
import spoon.MavenLauncher;
import spoon.compiler.Environment;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.reflect.visitor.filter.TypeFilter;

public class SpoonMain {

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
		/*for(CtMethod method : methodList) {
			System.out.println("method: " + method.getSimpleName());
		}*/
		

		// number of classes 
		int cptClasses = 0;
		for(CtClass c : classList) {
			cptClasses++;
		}
		System.out.println("Number of classes in the projet : "+cptClasses);
		
		// number of lines of code
		int loc = 0;
		for(CtClass c : classList) {
			loc += c.toString().split("\n").length;
		}
		System.out.println("Number of lines of code in the project : "+loc);
		
		// number of methods 
		int cptMethods = 0;
		for(CtMethod m : methodList) {
			cptMethods++;
		}
		System.out.println("Number of methods in the project : "+cptMethods);
		
		// number of packages
		int cptPackages = 0;
		for(CtPackage p : packageList) {
			cptPackages++;
		}
		System.out.println("Number of packages in the project : "+cptPackages);
		
		// average methods per class
		System.out.println("Average number of methods per class : "+(float)cptMethods/cptClasses);
		
		// average loc per methods
		int cptLocInMethod = 0;
		for(CtMethod m : methodList) {
			cptLocInMethod += m.toString().split("\n").length;
		}
		System.out.println("Average lines of code per method : "+(float)cptLocInMethod/cptMethods);
		
		// average fields per class
		int cptFieldsInClass = 0;
		for(CtClass c : classList) {
			cptFieldsInClass += c.getFields().size();
		}
		System.out.println("Average number of fields per class : "+(float)cptFieldsInClass/cptClasses);
		
		
		//10% of classes(7) with the most methods
		System.out.println("=====================================");
		System.out.println("10% of classes with the most methods : ");
		
		List<CtClass> mostMethodsList = new ArrayList<CtClass>();
		
		classList.sort((c1, c2) -> c2.getMethods().size() - c1.getMethods().size()); // c2 c1 is Descending order
		
		for(int i = 0; i < classList.size() * 0.1; i++) {
			System.out.println(classList.get(i).getSimpleName()+" : " +classList.get(i).getMethods().size());
			mostMethodsList.add(classList.get(i));
		}
		
		//10% of classes(7) with the most fields
		System.out.println("=====================================");
		System.out.println("10% of classes with the most fields : ");
		
		List<CtClass> mostFieldsList = new ArrayList<CtClass>();
		
		classList.sort((c1, c2) -> c2.getFields().size() - c1.getFields().size()); // c2 c1 is Descending order
		
		for(int i = 0; i < classList.size() * 0.1; i++) {
			System.out.println(classList.get(i).getSimpleName()+" : " +classList.get(i).getFields().size());
			mostFieldsList.add(classList.get(i));
		}
		
		//Classes that are in both previous categories
		System.out.println("=====================================");
		System.out.println("Classes which are in both previous categories : ");
		
		for(CtClass c : classList) {
			if(mostFieldsList.contains(c) && mostMethodsList.contains(c)){
				System.out.println(c.getSimpleName());
			}
		}
		
		//Classes with more than x methods
		System.out.println("=====================================");
		Scanner sc = new Scanner(System.in);
		System.out.println("Please entrer the number of methods : ");
		int nbrMethods = sc.nextInt();
		
		System.out.println("Classes with more than "+nbrMethods+" methods : ");
		for(CtClass c : classList) {
			if(c.getMethods().size() > nbrMethods) {
				System.out.println(c.getSimpleName());
			}
		}
		
		//10% methods with most loc ( per class )
		System.out.println("=====================================");
		System.out.println("10% methods with most loc ( per class ) : ");
		
		for(CtClass c : classList) {
			
			System.out.println(c.getSimpleName()+" class");
			
			HashMap<String, Integer> mapMethods = new HashMap<String, Integer>();
			
			Set<CtMethod> methodSet = c.getMethods();
				
			for(CtMethod m : methodSet) {
				int linesOfCode = m.toString().split("\n").length;
				mapMethods.put(m.getSimpleName(), linesOfCode);
			}
			
			
			
			/* ******************************* */
			/* Sorting the HashMap "methods" */
			List<Map.Entry<String, Integer>> mList = new LinkedList<Map.Entry<String,Integer>>(mapMethods.entrySet());
			
			System.out.println(mList.size()+ " methods");
			System.out.println("10% is : "+ (int)(mList.size() * 0.1));
			
			Collections.sort(mList, new Comparator<Map.Entry<String, Integer>>() {

				@Override
				public int compare(Entry<String, Integer> arg0, Entry<String, Integer> arg1) {
					// TODO Auto-generated method stub
					return arg0.getValue().compareTo(arg1.getValue());
				}
			
			});
			
			/* ******************************* */
			
			for(int i = 0 ; i < (int)(mList.size() * 0.1) ; i++) {
					System.out.println("\t"+mList.get(i).getKey()+ " : " +mList.get(i).getValue() );
			}
			
			System.out.println("____________________________________");
					
		}
		
		//Max number of parameters for methods
		System.out.println("=====================================");
		
		int maxParams = 0;
		String methodName = "";
		
		for(CtMethod m : methodList) {
			
			if(m.getParameters().size() > maxParams) {
				maxParams = m.getParameters().size();
				methodName = m.getSimpleName();
			}	
			
		}
		
		System.out.println("Method with the most parameters :");
		System.out.println(methodName+" : "+maxParams);
	
		
	}
}
