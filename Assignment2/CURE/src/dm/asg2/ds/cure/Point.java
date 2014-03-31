package dm.asg2.ds.cure;

public class Point {
	public enum Metric {EUCLIDEAN,MANHATTAN};
	double coordinates[];
	public static Metric metric = Metric.EUCLIDEAN;
	int dimension;
	public Point(double coordinates[]) {
		this.coordinates = coordinates;
		this.dimension = coordinates.length;
	}
	public double[] getCoordinates() {
		return coordinates;
	}
	public int getDimension() {
		return dimension;
	}
	
	public double distance(Point p){
		double sum=0;
		switch(metric){
			case EUCLIDEAN:
				for(int index= 0;index<dimension;index++){
					sum+=Math.pow(coordinates[index]-p.coordinates[index], 2);
				}
				break;
			case MANHATTAN:
				for(int index= 0;index<dimension;index++){
					sum+=Math.abs(coordinates[index]-p.coordinates[index]);
				}
				sum = Math.sqrt(sum);
				break;
		}
		return sum;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for(double value : coordinates){
			builder.append(value).append(" ");
		}
		return builder.toString().trim();
	}
}
