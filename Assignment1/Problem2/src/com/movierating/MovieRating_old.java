package com.movierating;
import java.io.IOException;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.SortedMapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

public class MovieRating_old {
	public static final Log log = LogFactory.getLog(MovieRating_old.class);	
	public static class AverageMovieRatingMapper extends Mapper<Object, Text, IntWritable, SortedMapWritable> {
		private IntWritable movieId = new IntWritable();
		private static final LongWritable one = new LongWritable(1);
		private IntWritable rate = new IntWritable(); 
		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {
			SortedMapWritable rating = new SortedMapWritable();
			try{
				String[] values = value.toString().split("\t");
			//	if(values.length==4){
					movieId.set(Integer.parseInt(values[1]));
					rate.set(Integer.parseInt(values[2]));
					rating.put(rate,one);
			//	}
			}catch(NumberFormatException e){
			
			}
			context.write(movieId, rating);
		}
		
		
	
	}
	public static class AverageMovieRatingCombiner extends Reducer<IntWritable, SortedMapWritable, IntWritable, SortedMapWritable> {
		public void reduce(IntWritable key, Iterable<SortedMapWritable> values, Context context) throws IOException, InterruptedException {
			SortedMapWritable outValue = new SortedMapWritable();
			for(SortedMapWritable v : values){
				for (Entry<WritableComparable, Writable> entry : v.entrySet()) {
					log.info(key.get()+" :: overatll rating : "+entry.getKey() + "count : "+entry.getValue());
					LongWritable count = (LongWritable) outValue.get(entry.getKey());
					if (count != null) {
						count.set(count.get()+ ((LongWritable) entry.getValue()).get());
					} else {
						outValue.put(entry.getKey(), new LongWritable(((LongWritable) entry.getValue()).get()));
					}
				}
			}
			//for (Entry<WritableComparable, Writable> entry : outValue.entrySet()) {
			//	log.info(key.get()+" :: overatll rating : "+entry.getKey() + "count : "+entry.getValue());
			//}
			context.write(key, outValue);
		}
	}
	public static class AverageMovieRatingReducer extends Reducer<IntWritable, SortedMapWritable, IntWritable, DoubleWritable> {
		private DoubleWritable result = new DoubleWritable();
		public void reduce(IntWritable key, Iterable<SortedMapWritable> values, Context context) throws IOException, InterruptedException {
			long sumrating=0;
			long count=0;
			for(SortedMapWritable v : values){
				for (Entry<WritableComparable, Writable> entry : v.entrySet()) {
					log.info(key.get()+" :: overatll rating : "+entry.getKey() + "count : "+entry.getValue());
					sumrating = sumrating+ ((IntWritable)entry.getKey()).get()*((LongWritable)entry.getValue()).get();
					count =count+((LongWritable)entry.getValue()).get();
				}
			}
			result.set(count);
			//log.info(key.get()+" :: overatll rating : "+sumrating + "count : "+count);
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
			job.setJarByClass(MovieRating_old.class);
			job.setMapperClass(AverageMovieRatingMapper.class);
			job.setCombinerClass(AverageMovieRatingCombiner.class);
			job.setReducerClass(AverageMovieRatingReducer.class);
			job.setOutputKeyClass(IntWritable.class);
			job.setOutputValueClass(SortedMapWritable.class);
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
