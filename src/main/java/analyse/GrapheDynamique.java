package analyse;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

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

public class GrapheDynamique {
	
	public static void main(String args[]) throws IOException{
		
		String file = "C:\\Users\\home\\Java_workspace\\restsuite\\grapheOut.txt";
		
		MutableGraph graph = mutGraph("grapheDynamique").setDirected(true);
		
		String node = null;
		
		DataInputStream dis = new DataInputStream(new FileInputStream(file));
		
		boolean eof = false;
		
		while(!eof) {
			
			try {
				
				node = dis.readLine();
				if(node == null) break;
				
			}catch(EOFException e){
				eof = true;
			}
			
			if(!eof) {
				
				String[] splitNodes = node.split(":");
				graph.add(mutNode(splitNodes[0]).addLink(mutNode(splitNodes[1])));
			}
		}
		
		dis.close();
		
		try {
			Graphviz.fromGraph(graph).width(2000).render(Format.PNG).toFile(new File("./restSuitGraph.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	
}
