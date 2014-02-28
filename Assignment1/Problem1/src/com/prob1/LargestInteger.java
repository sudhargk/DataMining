package com.prob1;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;


public class LargestInteger {

	public static class LargestIntegerMapper extends Mapper<Object, Text, IntWritable, IntWritable> {
		private final static IntWritable one = new IntWritable(1);
		private IntWritable intValue = new IntWritable();
		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {
			try{
				intValue.set(Integer.parseInt(value.toString()));
			}catch(NumberFormatException e){
			
			}
			context.write(one, intValue);
		}
		
		
	
	}
	public static class LargestIntegerCombiner extends Reducer<IntWritable, IntWritable, IntWritable, IntWritable> {
		private IntWritable result = new IntWritable();
		public void reduce(IntWritable key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
			int max=Integer.MIN_VALUE;
			for(IntWritable value : values){
				if(value.get()>max){
					max =value.get();
				}
			}
			result.set(max);
			context.write(key, result);
		}
	}
	public static class LargestIntegerReducer extends Reducer<IntWritable, IntWritable, IntWritable, NullWritable> {
		private IntWritable result = new IntWritable();
		public void reduce(IntWritable key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
			int max=Integer.MIN_VALUE;
			for(IntWritable value : values){
				if(value.get()>max){
					max =value.get();
				}
			}
			result.set(max);
			context.write(result,NullWritable.get());
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
			Job job = new Job(conf, "Largest Integer");
			job.setJarByClass(LargestInteger.class);
			job.setMapperClass(LargestIntegerMapper.class);
			job.setCombinerClass(LargestIntegerCombiner.class);
			job.setReducerClass(LargestIntegerReducer.class);
			job.setOutputKeyClass(IntWritable.class);
			job.setOutputValueClass(IntWritable.class);
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
