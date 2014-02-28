package com.prob2;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;


public class Distinct {
	public static final String COUNTER_DISTINCT = "distinct";
	public static class DistinctMapper extends Mapper<Object, Text, IntWritable, NullWritable> {
		private IntWritable intValue = new IntWritable();
		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {
			try{
				intValue.set(Integer.parseInt(value.toString()));
			}catch(NumberFormatException e){
			
			}
			context.write(intValue,NullWritable.get());
		}
	}
	
	public static class DistinctReducer extends Reducer<IntWritable, NullWritable, IntWritable, NullWritable> {
		private IntWritable intValue = new IntWritable();
		public void reduce(IntWritable key, Iterable<NullWritable> values, Context context) throws IOException, InterruptedException {
			intValue.set(key.get());
			context.write(intValue, NullWritable.get());
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
			Job job = new Job(conf, "Distinct Values");
			job.setJarByClass(Distinct.class);
			job.setMapperClass(DistinctMapper.class);
			job.setCombinerClass(DistinctReducer.class);
			job.setReducerClass(DistinctReducer.class);
			job.setOutputKeyClass(IntWritable.class);
			job.setOutputValueClass(NullWritable.class);
			FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
			FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));
			boolean status=job.waitForCompletion(true);
			System.exit(status?0:1);
		} catch (IOException e) {
			System.err.println("Erorr While Connecting :: "+e.getLocalizedMessage());
		} catch (ClassNotFoundException e) {
			System.err.println("Mapper and Reducer Class Not Found" + e.getLocalizedMessage());
		} catch (InterruptedException e) {
			System.err.println("Uknown error" + e.getLocalizedMessage());
		}
	}

}
