package dm.asg2.ds.cure;


import java.util.HashSet;
import java.util.Set;

public class Cluster implements Comparable<Cluster>{	
	int size;
	Point mean;
	Set<Point> allPoints;
	Set<Point> repPoints;
	public Cluster closest;
	public enum DistanceType {AVERAGE_DISTANCE,SHORTEST_DISTANCE,CENTROID_DISTANCE,LARGEST_DISTANCE};
	public static DistanceType distanceType=DistanceType.AVERAGE_DISTANCE;
	public Cluster() {
		
	}
	
	public Cluster(Point p) {
		this.allPoints = new HashSet<Point>();
		this.allPoints.add(p);
		
		this.repPoints = new HashSet<Point>();
		this.repPoints.add(p);
		
		this.mean=p;
		this.size =1;
		
	}
	public void setClosest(Cluster closest) {
		this.closest = closest;
	}
	
	public Set<Point> getAllPoints() {
		return allPoints;
	}
	
	public Set<Point> getRepPoints() {
		return repPoints;
	}
	
	public Point getMean() {
		return mean;
	}
	
	public static Cluster merge(Cluster u,Cluster v,int numRepresentative,double shrinkage){
		Cluster merged = new Cluster();
		double minDistance,maxDistance;
		//	union of two sets
		merged.allPoints = new HashSet<Point>();
		merged.allPoints.addAll(u.allPoints );
		merged.allPoints.addAll(v.allPoints );
		merged.size = merged.allPoints.size();
		
		//mean updation
		merged.mean=computeMean(u.mean, u.size, v.mean, v.size);
		
		//computing representative points
		Set<Point> tempSet = new HashSet<Point>();
		Point maxPoint = null;
		
		numRepresentative = Math.min(numRepresentative, merged.size);
		for(int index =0 ;index<numRepresentative;index++){
			maxDistance = 0.0;
			for(Point p : merged.allPoints){
				if (index==0){
					minDistance = p.distance(merged.mean);
				}else{
					minDistance = Double.MAX_VALUE;
					for(Point temp:tempSet){
						double tempDistance = p.distance(temp);
						if(minDistance>tempDistance){
							minDistance=tempDistance;
						}
					}
				}
				if(minDistance>=maxDistance){
					maxDistance=minDistance;
					maxPoint = p;
				}
			}
			tempSet.add(maxPoint);
		}
		
		//bringing the representative points closer
		merged.repPoints = new HashSet<Point>();
		for(Point p : tempSet){
			merged.repPoints.add(shrinkTowardsMean(p, merged.mean, shrinkage));
		}
	
		return merged;
	}
	
	public static Point shrinkTowardsMean(Point p, Point mean,double shrinkage){
		double []coordinates = p.getCoordinates();
		double []mCoordinates = mean.getCoordinates(); 
		double []nCoordinates = new double[p.getDimension()];
		for(int index = 0; index<p.getDimension();index++){
			nCoordinates[index] = coordinates[index]+shrinkage*(mCoordinates[index]-coordinates[index]);
		}
		return new Point(coordinates);
	}
	
	public static Point computeMean(Point u, int usize,Point v,int vsize){
		double mean[] = new double[u.getDimension()];
		double [] ucord = u.getCoordinates();
		double [] vcord = v.getCoordinates();
		for(int index =0; index<u.getDimension();index++){
			mean[index] = ucord[index]*usize + vcord[index]*vsize;
			mean[index] = mean[index]/(usize+vsize);
		}
		return new Point(mean);
	}
	public static double distance(Cluster u,Cluster v){
		double distance;
		switch(distanceType){
			case SHORTEST_DISTANCE :
				distance=Double.MAX_VALUE;
				for(Point p : u.repPoints){
					for(Point q : v.repPoints){
						double tempDistance = p.distance(q);
						if(distance>tempDistance){
							distance=tempDistance;
						}
					}
				}
				break;
			case AVERAGE_DISTANCE:
				distance=0.0;
				for(Point p : u.repPoints){
					for(Point q : v.repPoints){
						distance+= p.distance(q);
					}
				}
				distance=distance/(u.repPoints.size()+v.repPoints.size());
				break;
			case CENTROID_DISTANCE:	
				distance= u.mean.distance(v.mean);
				break;
			case LARGEST_DISTANCE:	
				distance=Double.MIN_VALUE;
				for(Point p : u.repPoints){
					for(Point q : v.repPoints){
						double tempDistance = p.distance(q);
						if(distance<tempDistance){
							distance=tempDistance;
						}
					}
				}
				break;
			default:
				distance =0.0;
		}
		
		return distance;
	}

	@Override
	public int compareTo(Cluster other) {
		double distance1 = Cluster.distance(this, this.closest);
		double distance2 = Cluster.distance(other, other.closest);
		if(distance1 < distance2){
			return -1;
		}else if(distance1 > distance2){
			return 1;
		}
		return 0;
	}
}
