package com.minhash;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Random;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

public class MinHash {
	public static final Log log = LogFactory.getLog(MinHash.class);
	public static final int NUM_HASH_FUNCTION=5;
	public static final int MAX_INT = 100;
	static int [][]hashConstants;
	static{
		Random random = new Random();
		hashConstants = new int[NUM_HASH_FUNCTION][2];
		for(int k=0;k<NUM_HASH_FUNCTION;k++){
			hashConstants[k][0]=Math.abs(random.nextInt()%MAX_INT);
			hashConstants[k][1]=Math.abs(random.nextInt()%MAX_INT);
			//log.info(hashConstants[k][0] + "-- " +hashConstants[k][1]);
		}
		
	}
	
	public static class HashWritable implements WritableComparable<HashWritable>{
		int documentId;
		int hashId;
		
		public void setDocumentId(int documentId) {
			this.documentId = documentId;
		}
		
		public void setHashId(int hashId) {
			this.hashId = hashId;
		}
		
		@Override
		public void readFields(DataInput in) throws IOException {
			this.documentId = in.readInt();
			this.hashId = in.readInt();
		}
		@Override
		public String toString() {
			return documentId+"\t"+hashId;
		}
		@Override
		public void write(DataOutput out) throws IOException {
			out.writeInt(this.documentId);
			out.writeInt(this.hashId);
		}

		@Override
		public int compareTo(HashWritable with) {
			if(this.documentId>with.documentId){
				return 1;
			}else if(this.documentId==with.documentId){
				if(this.hashId>with.hashId){
					return 1;
				}else if(this.hashId<with.hashId){
					return -1;
				}else{
					return 0;
				}
			}else{
				return -1;
			}
		}
		
	}
	
	public static long[] computeHashValues(long value){
		long hashvalues[] = new long[NUM_HASH_FUNCTION];
		for(int k=0;k<NUM_HASH_FUNCTION;k++){
			hashvalues [k] = (hashConstants[k][0]*value + hashConstants[k][1])%MAX_INT;
		}
		return hashvalues;
	}
		
	public static class MinHashMapper extends Mapper<LongWritable, Text, HashWritable, LongWritable> {
		private HashWritable hashKey = new HashWritable();
		private LongWritable longvalue = new LongWritable();
		public void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {
			StringTokenizer tokenizer = new StringTokenizer(value.toString()," ");
			int documentId = 0;
			try{
				while(tokenizer.hasMoreTokens()){
					String token = tokenizer.nextToken();
					if(Integer.parseInt(token)==1){
						long hashvalues[] = computeHashValues(key.get());
						
						for(int hashID=0;hashID<NUM_HASH_FUNCTION;hashID++){
							hashKey.setDocumentId(documentId);
							hashKey.setHashId(hashID);
							longvalue.set(hashvalues[hashID]);
							context.write(hashKey, longvalue);
						}
					}
					documentId++;
				}
			}catch(NumberFormatException e){
				
			}
		}
	}
	public static class MinHashReducer extends Reducer<HashWritable, LongWritable, HashWritable, LongWritable> {
		private LongWritable longvalue = new LongWritable();
		public void reduce(HashWritable key, Iterable<LongWritable> values, Context context) throws IOException, InterruptedException {
			Long minvalue = Long.MAX_VALUE;
			for(LongWritable value : values){
				if(value.get()<minvalue){
					minvalue=value.get();
				}
			}
			longvalue.set(minvalue);
			context.write(key, longvalue);
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
			Job job = new Job(conf, "Min Hashing");
			job.setJarByClass(MinHash.class);
			job.setMapperClass(MinHashMapper.class);
			job.setCombinerClass(MinHashReducer.class);
			job.setReducerClass(MinHashReducer.class);
			job.setOutputKeyClass(HashWritable.class);
			job.setOutputValueClass(LongWritable.class);
			FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
			FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));
			System.exit(job.waitForCompletion(true) ? 0 : 1);
		} catch (IOException e) {
			System.err.println("Erorr While Connecting :: "+e.getLocalizedMessage());
		} catch (ClassNotFoundException e) {
			System.err.println("Mapper and Reducer Class Not Found" + e.getLocalizedMessage());
		} catch (InterruptedException e) {
			System.err.println("Unknown error" + e.getLocalizedMessage());
		}
	}
}
