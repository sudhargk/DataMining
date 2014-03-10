package com.movierating;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

public class MovieRating {

	public static class RatingWritable implements Writable{
		long count;
		int rate;
		
		public void setCount(long count) {
			this.count = count;
		}
		
		public void setRate(int rate) {
			this.rate = rate;
		}
		public long getCount() {
			return count;
		}
		public int getRate() {
			return rate;
		}
		@Override
		public void readFields(DataInput in) throws IOException {
			count= in.readLong();
			rate = in.readInt();
		}

		@Override
		public void write(DataOutput out) throws IOException {
			out.writeLong(count);
			out.writeInt(rate);
		}
	}
		
	public static final Log log = LogFactory.getLog(MovieRating.class);	
	public static class AverageMovieRatingMapper extends Mapper<Object, Text, IntWritable, RatingWritable> {
		private IntWritable movieId = new IntWritable();
		private RatingWritable rating = new RatingWritable();
		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {
			try{
				String[] values = value.toString().split("\t");
				movieId.set(Integer.parseInt(values[1]));
				rating.setCount(1);
				rating.setRate(Integer.parseInt(values[2]));
			}catch(NumberFormatException e){
			
			}
			context.write(movieId, rating);
		}
	}
	public static class AverageMovieRatingCombiner extends Reducer<IntWritable, RatingWritable, IntWritable, RatingWritable> {
		private RatingWritable rating = new RatingWritable();
		public void reduce(IntWritable key, Iterable<RatingWritable> values, Context context) throws IOException, InterruptedException {
			Map<Integer,Long> ratingMap = new TreeMap<Integer,Long>();
			for(RatingWritable value : values){
				Long cnt = ratingMap.get(value.getRate());
				if(cnt==null){
					cnt = value.getCount();
				}else{
					cnt = cnt+value.getCount();
				}
				ratingMap.put(value.getRate(),cnt);
			}
			for(Integer rate : ratingMap.keySet()){
				rating.setRate(rate);
				rating.setCount(ratingMap.get(rate));
				context.write(key, rating);
			}
		}
	}
	public static class AverageMovieRatingReducer extends Reducer<IntWritable, RatingWritable, IntWritable, DoubleWritable> {
		private DoubleWritable result = new DoubleWritable();
		public void reduce(IntWritable key, Iterable<RatingWritable> values, Context context) throws IOException, InterruptedException {
			long sumrating=0;
			long count=0;
			for(RatingWritable value : values){
				sumrating+=value.getRate()*value.getCount();
				count+=value.getCount();
			}
			result.set((double)sumrating/count);
			context.write(key,result);
		}
	}
	
	public static void main(String[] args) {
		Configuration conf = new Configuration();
		String[] otherArgs;
		try {
			otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
			if (otherArgs.length != 2) {
				System.err.println("Usage: Please provide input and output path <in> <out>");
				System.exit(2);
			}
			Job job = new Job(conf, "Movie Rating");
			job.setJarByClass(MovieRating.class);
			job.setMapperClass(AverageMovieRatingMapper.class);
			job.setCombinerClass(AverageMovieRatingCombiner.class);
			job.setReducerClass(AverageMovieRatingReducer.class);
			job.setOutputKeyClass(IntWritable.class);
			job.setOutputValueClass(RatingWritable.class);
			FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
			FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));
			System.exit(job.waitForCompletion(true) ? 0 : 1);
		} catch (IOException e) {
			System.err.println("Erorr While Connecting :: "+e.getLocalizedMessage());
		} catch (ClassNotFoundException e) {
			System.err.println("Mapper and Reducer Class Not Found" + e.getLocalizedMessage());
		} catch (InterruptedException e) {
			System.err.println("Uknown error" + e.getLocalizedMessage());
		}
	}


}
