/**
 * ECE 454/750: Distributed Computing
 *
 * Code written by Wojciech Golab, University of Waterloo, 2015
 *
 * IMPLEMENT YOUR SOLUTION IN THIS FILE
 *
 */

package ece454750s15a2;
import java.io.*;
import java.util.*;

public class TriangleCountImpl {
    private byte[] input;
    private int numCores;

    public TriangleCountImpl(byte[] input, int numCores) {
	this.input = input;
	this.numCores = numCores;
    }

    public List<String> getGroupMembers() {
	ArrayList<String> ret = new ArrayList<String>();
	ret.add("nhleung");
	ret.add("swmaung");
	return ret;
    }

    public List<Triangle> enumerateTriangles() throws IOException {
		// this code is single-threaded and ignores numCores

		Map<Integer,ArrayList<Integer>> adjacencyList = getAdjacencyList(input); //adjacency list
		ArrayList<Triangle> ret = new ArrayList<Triangle>();
		int numVertices = adjacencyList.size();
		// Map<Integer,Integer> edgesList = getEdgesList(input);
		// for(Map.Entry<Integer,Integer> edge : edgesList.entrySet()){
		// 	System.out.println(edge.getKey() +":: "+ edge.getValue());
		// }
		Map<Integer,ArrayList<Integer>> forward = createEmptyAdjList(numVertices);
		//ennumerating over vertices
		long startTime = System.currentTimeMillis();
		for (int i = 0; i < numVertices; i++) {
			    ArrayList<Integer> n1 = adjacencyList.get(i);
			    //if >=2 edges (can be triangle)
			    //if(n1.size() >= 2){
			    	//for all adjacent nodes
			 	    for (int j: n1) {
			 	    	//if(i<j){
							/*ArrayList<Integer> n2 = adjacencyList.get(j);
							if(n2.size() >= 2){
								for (int k: n2) {
									if(j<k){
									    if(n1.contains(k)){
									    	ret.add(new Triangle(i, j, k));
									    }
									}
								}
							}*/
							for (int adjToI : (ArrayList<Integer>)forward.get(i)) {
					            if(((ArrayList<Integer>)forward.get(j)).contains(adjToI)) {
					                ret.add(new Triangle(adjToI,i, j));
					            }
					        }
					        forward.get(j).add(i);
						}
				    //}
				//}
				//adjacencyList.remove(i);
		}
		long endTime = System.currentTimeMillis();
		long diffTime = endTime - startTime;
		System.out.println("Done enumerating in " + diffTime + "ms");

		return ret;
    }

    public Map<Integer,ArrayList<Integer>> createEmptyAdjList(int sizeOfList){
    	HashMap<Integer,ArrayList<Integer>> emptyAdjList = new HashMap<Integer,ArrayList<Integer>>();
    	for(int i = 0; i< sizeOfList; i++){
    		emptyAdjList.put(i, new ArrayList<Integer>());
    	}
    	return emptyAdjList;
    }

    /*public Map<Integer,Integer> getEdgesList(byte[] data) throws IOException {
	InputStream istream = new ByteArrayInputStream(data);
	BufferedReader br = new BufferedReader(new InputStreamReader(istream));
	String strLine = br.readLine();
	if (!strLine.contains("vertices") || !strLine.contains("edges")) {
	    System.err.println("Invalid graph file format. Offending line: " + strLine);
	    System.exit(-1);	    
	}
	String parts[] = strLine.split(", ");
	int numVertices = Integer.parseInt(parts[0].split(" ")[0]);
	int numEdges = Integer.parseInt(parts[1].split(" ")[0]);
	System.out.println("Found graph with " + numVertices + " vertices and " + numEdges + " edges");
 
	Map<Integer,Integer> edgesList = new HashMap<Integer,Integer>();
	//for (int i = 0; i < numVertices; i++) {
	//    adjacencyList.put(i,new ArrayList<Integer>());
	//}
	while ((strLine = br.readLine()) != null && !strLine.equals(""))   {
	    parts = strLine.split(": ");
	    int vertex = Integer.parseInt(parts[0]);
	    if (parts.length > 1) {
		parts = parts[1].split(" +");
		for (String part: parts) {
			if(vertex < Integer.parseInt(part)){
			    edgesList.put(vertex, Integer.parseInt(part));
			}
		}
	    }
	}
	br.close();
	return edgesList;
    }*/

    public Map<Integer,ArrayList<Integer>> getAdjacencyList(byte[] data) throws IOException {
	InputStream istream = new ByteArrayInputStream(data);
	BufferedReader br = new BufferedReader(new InputStreamReader(istream));
	String strLine = br.readLine();
	if (!strLine.contains("vertices") || !strLine.contains("edges")) {
	    System.err.println("Invalid graph file format. Offending line: " + strLine);
	    System.exit(-1);	    
	}
	String parts[] = strLine.split(", ");
	int numVertices = Integer.parseInt(parts[0].split(" ")[0]);
	int numEdges = Integer.parseInt(parts[1].split(" ")[0]);
	System.out.println("Found graph with " + numVertices + " vertices and " + numEdges + " edges");
 
	Map<Integer,ArrayList<Integer>> adjacencyList = new HashMap<Integer,ArrayList<Integer>>(numVertices);
	for (int i = 0; i < numVertices; i++) {
	    adjacencyList.put(i,new ArrayList<Integer>());
	}
	while ((strLine = br.readLine()) != null && !strLine.equals(""))   {
	    parts = strLine.split(": ");
	    int vertex = Integer.parseInt(parts[0]);
	    if (parts.length > 1) {
		parts = parts[1].split(" +");
		for (String part: parts) {
			if(vertex < Integer.parseInt(part)){
			    adjacencyList.get(vertex).add(Integer.parseInt(part));
			}
		}
	    }
	}
	br.close();
	return adjacencyList;
    }
}
