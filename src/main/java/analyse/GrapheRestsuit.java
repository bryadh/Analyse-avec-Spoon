package analyse;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import guru.nidi.graphviz.attribute.Color;
import guru.nidi.graphviz.attribute.Font;
import guru.nidi.graphviz.attribute.Rank;
import guru.nidi.graphviz.attribute.Rank.RankDir;
import guru.nidi.graphviz.attribute.Style;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.Graph;
import guru.nidi.graphviz.model.MutableGraph;
import spoon.Launcher;
import spoon.MavenLauncher;
import spoon.compiler.Environment;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.reflect.reference.CtTypeReferenceImpl;

import static guru.nidi.graphviz.model.Factory.*;

public class GrapheRestsuit {
	
	public static void main(String[] args) {
		

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
		
		/*
		 * 
		 * 
		List<CtClass> classList = model.getElements(new TypeFilter<CtClass>(CtClass.class));
		
		for(CtClass c : classList) {
			
			System.out.println(c.getSimpleName());
			System.out.println("external references : ");
			
			List<String> listRefs = new ArrayList<String>();
			
			for(CtTypeReference ref : c.getElements(new TypeFilter<>(CtTypeReference.class))) {
				for(CtClass c1 : classList) {
					
					if(c1.getSimpleName().equals(ref.toString())) {
						if(!listRefs.contains(ref.toString())) {
							listRefs.add(ref.toString());
						}
					}
				}
				
			}
			
			System.out.println(listRefs);
			
			System.out.println("===========================================");
		}
		
		*/
		
		MutableGraph graph = mutGraph("example1").setDirected(true);
		
		List<CtClass> classList = model.getElements(new TypeFilter<CtClass>(CtClass.class));
		
		
		//Graph processing 
		/* ************************************************************************************** */
		for(CtClass c : classList) {
			
			List<String> listRefs = new ArrayList<String>();
			String className = c.getSimpleName();
			
			for(CtTypeReference ref : c.getElements(new TypeFilter<>(CtTypeReference.class))) {
				
				for(CtClass c1 : classList) {
					
					if(c1.getSimpleName().equals(ref.toString())) {
						if(!listRefs.contains(ref.toString())) {
							listRefs.add(ref.toString());
						}
					}
				}
				
			}
			
			graph.add(mutNode(className));
			
			for(String s : listRefs) {
				graph.add(mutNode(className).addLink(mutNode(s)));
				System.out.println(className+ " -> " +s);
			}
		
		}
		/* ************************************************************************************** */
		
		try {
			Graphviz.fromGraph(graph).width(2000).render(Format.PNG).toFile(new File("./restSuitGraph.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}

}
