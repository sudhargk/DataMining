package org.dbscan;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.dbscan.ds.Point;

import weka.clusterers.DBSCAN;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.ListOptions;

public class Dbscan {
	Instances instances;
	List<Point> inputPoints;
	DBSCAN dbscan;
	public void loadFile(String filename) throws FileNotFoundException, IOException{
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
		FastVector attributes = new FastVector();
		Point point = inputPoints.get(0);
		for(int index =0 ;index<point.getDimension();index++){
			attributes.addElement(new Attribute("Index "+index));
		}
		
		instances = new Instances("MyRelation",attributes,0);
		for(Point p : inputPoints){
			instances.add(new Instance(1.0,p.getCoordinates()));
		}
		
	}
	public void showCluster() throws Exception{
		//System.out.println(dbscan.toString());
		for(int index=0;index<instances.numInstances();index++){
			Instance instance = instances.instance(index);
			StringBuilder builder = new StringBuilder();
			for(int pos =0;pos<instance.numValues();pos++){
				builder.append(instance.value(pos)).append(" ");
			}
			int clusterIndex=0;
			try{
				clusterIndex = dbscan.clusterInstance(instance)+1;
			}catch(Exception e){
			}
			System.out.println(builder.toString()+" "+clusterIndex);
		}
	}
	public void start(double epsilon,int minPoints) throws Exception{
		dbscan = new DBSCAN();
		dbscan.setEpsilon(epsilon);
		dbscan.setMinPoints(minPoints);
		dbscan.buildClusterer(instances);
	}
	public static void main(String[] args) {
		Dbscan dbscanalgo = new Dbscan();
		
		int neigbors = 5;
		double radius = 0.035;
		String input = "data_type1.txt";

		
//		int neigbors = 10;
//		double radius = 0.1;
//		String input = "data_type2.txt";
		
		try {
			dbscanalgo.loadFile("data\\" + input);
			dbscanalgo.start(radius,neigbors);
			System.setOut(new PrintStream("data\\data.out"));
			dbscanalgo.showCluster();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
