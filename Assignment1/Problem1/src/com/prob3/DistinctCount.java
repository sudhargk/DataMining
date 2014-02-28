package com.prob3;

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
import org.apache.hadoop.mapreduce.lib.output.NullOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;


public class DistinctCount {
	public static final String COUNTER_GROUP = "group";
	public static final String COUNTER_DISTINCT = "distinct";
	public static class DistinctCountMapper extends Mapper<Object, Text, IntWritable, NullWritable> {
		//private final static IntWritable one = new IntWritable(1);
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
	public static class DistinctCountCombiner extends Reducer<IntWritable, NullWritable, IntWritable, NullWritable> {
		public void reduce(IntWritable key, Iterable<NullWritable> values, Context context) throws IOException, InterruptedException {
			context.write(key,NullWritable.get());
		}
	}
	
	public static class DistinctCountReducer extends Reducer<IntWritable, NullWritable, IntWritable, NullWritable> {
		public void reduce(IntWritable key, Iterable<NullWritable> values, Context context) throws IOException, InterruptedException {
			context.getCounter(COUNTER_GROUP, COUNTER_DISTINCT).increment(1);
			context.write(key, NullWritable.get());
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
			job.setJarByClass(DistinctCount.class);
			job.setMapperClass(DistinctCountMapper.class);
			job.setCombinerClass(DistinctCountCombiner.class);
			job.setReducerClass(DistinctCountReducer.class);
			job.setOutputKeyClass(IntWritable.class);
			job.setOutputValueClass(NullWritable.class);
			job.setOutputFormatClass(NullOutputFormat.class);
			FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
			FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));
			boolean status=job.waitForCompletion(true);
			if(status){
			 Counter distinctCounter = job.getCounters().getGroup(COUNTER_GROUP).findCounter(COUNTER_DISTINCT);
			 System.out.println("Unique Elements :: " + distinctCounter.getValue());
			}
			System.exit(status?0:1);
		} catch (IOException e) {
			System.err.println("Erorr While Connecting :: "+e.getLocalizedMessage());
		}  catch (ClassNotFoundException e) {
			System.err.println("Mapper and Reducer Class Not Found" + e.getLocalizedMessage());
		} catch (InterruptedException e) {
			System.err.println("Uknown error" + e.getLocalizedMessage());
		}
	}

}
