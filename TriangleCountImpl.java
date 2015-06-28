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

		ArrayList<HashSet<Integer>> adjacencyList = getAdjacencyList(input); //adjacency list
		ArrayList<Triangle> ret = new ArrayList<Triangle>();
		int numVertices = adjacencyList.size();
		for (int i = 0; i < numVertices; i++) {
			    HashSet<Integer> n1 = adjacencyList.get(i);
			    if(n1.size() >= 2){
			 	    for (int j: n1) {
			 	    	if(i<j){
							HashSet<Integer> n2 = adjacencyList.get(j);
							for (int k: n2) {
								if(j<k){
								    if(n1.contains(k)){
								    	ret.add(new Triangle(i, j, k));
								    }
								}
							}
						}
				    }
				}
		}

		return ret;
    }

    public ArrayList<HashSet<Integer>> getAdjacencyList(byte[] data) throws IOException {
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
 
	ArrayList<HashSet<Integer>> adjacencyList = new ArrayList<HashSet<Integer>>(numVertices);
	for (int i = 0; i < numVertices; i++) {
	    adjacencyList.add(i,new HashSet<Integer>());
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
