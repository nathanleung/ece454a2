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
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TriangleCountImplMult {
    private byte[] input;
    private int numCores;

    public TriangleCountImplMult(byte[] input, int numCores) {
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

		ConcurrentMap<Integer,HashSet<Integer>> adjacencyList = getAdjacencyList(input); //adjacency list
		ArrayList<Triangle> ret = new ArrayList<Triangle>();
		if(numCores == 1){
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
		}
		else{
			ConcurrentMap<String,Triangle> results = new ConcurrentHashMap<String,Triangle>();
			TriangleCountParallel tcp = new TriangleCountParallel(adjacencyList,results,-1,numCores);
			ret = tcp.getParallel();
		}

		return ret;
    }

    public ConcurrentMap<Integer,HashSet<Integer>> getAdjacencyList(byte[] data) throws IOException {
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
 
	ConcurrentMap<Integer,HashSet<Integer>> adjacencyList = new ConcurrentHashMap<Integer,HashSet<Integer>>(numVertices);
	for (int i = 0; i < numVertices; i++) {
	    adjacencyList.putIfAbsent(i,new HashSet<Integer>());
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
    public class TriangleCountParallel implements Runnable{
    	private final ConcurrentMap<Integer, HashSet<Integer>> adjacencyList;
    	private final ConcurrentMap<String, Triangle> results;
    	private final int threadNum;
    	private final int numOfThreads;
    	public TriangleCountParallel(ConcurrentMap<Integer, HashSet<Integer>> adjacencyList,
    		ConcurrentMap<String, Triangle> results, int threadNum, int numOfThreads){
    		this.adjacencyList = adjacencyList;
    		this.results =results;
    		this.threadNum = threadNum;
    		this.numOfThreads = numOfThreads;
    	} 
    	public ArrayList<Triangle> getParallel(){
    		ArrayList<Triangle> ret = new ArrayList<Triangle>();
    		//this.adjacencyList = getAdjacencyList()
    		ExecutorService pool = Executors.newFixedThreadPool(numOfThreads);
    		for(int i = 0; i< numOfThreads; i++){
    			pool.submit(new TriangleCountParallel(adjacencyList,results,i,numOfThreads));
    		}
    		pool.shutdown();
    		try {
	            pool.awaitTermination(1,TimeUnit.DAYS);
	        } catch (InterruptedException e) {
	            System.out.println("Pool interrupted!");
	            System.exit(1);
	        }
	        //System.out.println(results.size());
	        //ConcurrentMap<String, Triangle> syncResults = getResults();
	        for (ConcurrentMap.Entry<String, Triangle> e: results.entrySet()){
		    	ret.add(e.getValue());
		    }
    		return ret;
    	}
    	public ConcurrentMap<String, Triangle> getResults(){
    		return results;
    	}
    	public void run(){
    		if(threadNum >= 0){
	    		enumerateTrianglesMult();
	    	}
	    	else{
	    		getParallel();
	    	}
    	}
    	public void enumerateTrianglesMult(){
			int numVertices = adjacencyList.size();
			int blockSize = numVertices/numOfThreads;
			//System.out.println("start: "+threadNum*blockSize);
			//System.out.println("end: "+(threadNum*blockSize+blockSize));
    		for (int i = threadNum*blockSize; i < threadNum*blockSize+blockSize; i++) {
				    HashSet<Integer> n1 = adjacencyList.get(i);
				    if(n1.size() >= 2){
				 	    for (int j: n1) {
				 	    	if(i<j){
								HashSet<Integer> n2 = adjacencyList.get(j);
								for (int k: n2) {
									if(j<k){
									    if(n1.contains(k)){
									    	String name = Integer.toString(i)+"-"+
									    		Integer.toString(j)+"-"+Integer.toString(k);
									    	results.putIfAbsent(name,new Triangle(i, j, k));
									    	//ret.add(new Triangle(i, j, k));
									    }
									}
								}
							}
					    }
					}
			}
    	}
    }
}
