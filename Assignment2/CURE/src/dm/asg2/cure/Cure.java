package dm.asg2.cure;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

import dm.asg2.ds.cure.Cluster;
import dm.asg2.ds.cure.Cluster.DistanceType;
import dm.asg2.ds.cure.Point;
import dm.asg2.ds.cure.Point.Metric;

public class Cure {
	int K;
	int numRepresentative ;
	double shrinkage;
	List<Point> inputPoints;
	Queue<Cluster> clusterQueue;
	
	public Cure(int numRepresentative,int K,double shrinkage) {
		this.numRepresentative = numRepresentative;
		this.K =K;
		this.shrinkage = shrinkage;
	}
	
	public void loadFile(String filename) throws IOException{
		inputPoints = new ArrayList<Point>();
		BufferedReader reader = new BufferedReader(new FileReader(filename));
		String line = null;
		while((line=reader.readLine())!=null){
			String strvalues[] = line.split(" ");
			double []coordinates = new double [strvalues.length];
			for(int index =0;index<strvalues.length;index++){
				coordinates[index] = Double.parseDouble(strvalues[index]);
			}
			inputPoints.add(new Point(coordinates));
		}
		reader.close();
	}
	
	public void init(){
		clusterQueue = new PriorityQueue<Cluster>();
		Map<Point,Cluster> pointClusterMap = new HashMap<Point,Cluster>();

		for(Point p : inputPoints){
			pointClusterMap.put(p, new Cluster(p));
		}
		
		double tempDistance,minDistance;
		Point minPoint;
		Cluster curCluster;
		for(Point p : inputPoints){
			minDistance = Double.MAX_VALUE;
			minPoint = null;
			curCluster = pointClusterMap.get(p);
			for(Point q : inputPoints){
				if(!p.equals(q)){
					tempDistance = p.distance(q);
					if(minDistance>tempDistance){
						minDistance = tempDistance;
						minPoint=q;
					}
				}
			}
			curCluster.setClosest(pointClusterMap.get(minPoint));
			
		}
		for(Point p : inputPoints){
			clusterQueue.add(pointClusterMap.get(p));
		}
	}
	
	public void start(){
		Cluster u,v,w;
		Set<Cluster> updatedClusters = new HashSet<Cluster>();
		
		init();
		while(clusterQueue.size()>K){
			u = clusterQueue.remove();
			v = u.closest;
			clusterQueue.remove(v);
			w = Cluster.merge(u, v, numRepresentative,shrinkage);
			
			//arbitrarily assigning the closest 
			w.closest=clusterQueue.peek();
			
			// updating the other cluster closest points if needed;
			updatedClusters.clear();
			for(Cluster x : clusterQueue){
				if(Cluster.distance(w,x)<Cluster.distance(w, w.closest))
					w.closest=x;
				if(x.closest==u ||x.closest==v){
					if(Cluster.distance(x,x.closest) < Cluster.distance(x, w)){
						x.closest = closest_cluster(clusterQueue,x,w);  
					}else{
						x.closest = w;
					}
					updatedClusters.add(x);
				}else if(Cluster.distance(x, x.closest)>Cluster.distance(x, w)){
					x.closest = w;
					updatedClusters.add(x);
				}
			}
			clusterQueue.removeAll(updatedClusters);
			clusterQueue.add(w);
			clusterQueue.addAll(updatedClusters);
		}
	}
	
	public void showClusters(){
		int index=1;
		for(Cluster x : clusterQueue){
			for(Point p : x.getAllPoints()){
				System.out.println(p + " " + index);
			}
			index++;
		}
	}
	public void meanClusters(){
		int index=1;
		for(Cluster x : clusterQueue){
			System.out.println(x.getMean() + " " + index);
			index++;
		}
	}
	
	public Cluster closest_cluster(Queue<Cluster> currentQueue,Cluster x,Cluster w){
		double tempDistance,minDistance = Cluster.distance(x, w);
		Cluster closeCluster = w;
		for(Cluster s : currentQueue){
			if(!s.equals(x)){
				tempDistance = Cluster.distance(s, x);
				if(minDistance>tempDistance){
					minDistance=tempDistance;
					closeCluster = s;
				}
			}
		}
		return closeCluster;
	}
	
	public static void main(String[] args) {
		Cluster.distanceType = DistanceType.SHORTEST_DISTANCE;
		Point.metric = Metric.EUCLIDEAN;
		int numCluster = 2,numRepresentative = 3;
		double shrinkage = 1;
		String input = "data_type2.txt";
		

		Cure cureAlgorithm = new Cure(numRepresentative,numCluster,shrinkage);
		try {
			cureAlgorithm.loadFile("data\\" + input);
			cureAlgorithm.start();
			System.setOut(new PrintStream("data\\data.out"));
			cureAlgorithm.showClusters();
			System.setOut(new PrintStream("data\\mean.out"));
			cureAlgorithm.meanClusters();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
