package com.lsh;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

public class DriverClass {

	public static void main(String[] args) {
		Configuration conf = new Configuration();
		String[] otherArgs;
		try {
			otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
			if (otherArgs.length != 2) {
				System.err.println("Usage: Please provide input and output path <in> <out>");
				System.exit(2);
			}
			Job job = new Job(conf, "LSH Bagging");
			job.setJarByClass(LSH_Part1.class);
			job.setMapperClass(LSH_Part1.LSHMapper.class);
			job.setReducerClass(LSH_Part1.LSHReducer.class);
			job.setOutputKeyClass(LSH_Part1.HashBandIdWritable.class);
			job.setOutputValueClass(LongWritable.class);
			FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
			FileOutputFormat.setOutputPath(job, new Path("mid"));
			boolean success = job.waitForCompletion(true);
			
			if(success){
				job = new Job(conf, "LSH Bagging Across Bands");
				job.setJarByClass(LSH_Part2.class);
				job.setMapperClass(LSH_Part2.LSHMapper.class);
				job.setReducerClass(LSH_Part2.LSHReducer.class);
				job.setOutputKeyClass(LSH_Part2.CandidatePairs.class);
				job.setOutputValueClass(NullWritable.class);
				FileInputFormat.addInputPath(job, new Path("mid"));
				FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));
				System.exit(job.waitForCompletion(true) ? 0 : 1);
			}
			
			
			
		} catch (IOException e) {
			System.err.println("Erorr While Connecting :: "+e.getLocalizedMessage());
		} catch (ClassNotFoundException e) {
			System.err.println("Mapper and Reducer Class Not Found" + e.getLocalizedMessage());
		} catch (InterruptedException e) {
			System.err.println("Unknown error" + e.getLocalizedMessage());
		}
	}

}
