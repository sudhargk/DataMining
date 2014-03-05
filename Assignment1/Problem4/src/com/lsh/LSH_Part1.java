	package com.lsh;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

public class LSH_Part1 {
	public static final Log log = LogFactory.getLog(LSH_Part1.class);
	public static final String PARAM_NUM_ROWS_PER_BLOCK="ROWS_PER_BLOCK";
	public static class HashBandIdWritable implements WritableComparable<HashBandIdWritable>{
		long hashValue;long bandId;
		
		public void setBandId(long bandId) {
			this.bandId = bandId;
		}
		
		public void setHashValue(long hashValue) {
			this.hashValue = hashValue;
		}
		
		@Override
		public void readFields(DataInput in) throws IOException {
			hashValue = in.readLong();
			bandId = in.readLong();
		}

		@Override
		public void write(DataOutput out) throws IOException {
			out.writeLong(hashValue);
			out.writeLong(bandId);
			
		}

		@Override
		public String toString() {
			return  bandId + "\t" + hashValue ;
		}

		@Override
		public int compareTo(HashBandIdWritable with) {
			if(this.hashValue>with.hashValue){
				return 1;
			}else if(this.hashValue==with.hashValue){
				if(this.bandId>with.bandId){
					return 1;
				}else if(this.bandId<with.bandId){
					return -1;
				}else{
					return 0;
				}
			}else{
				return -1;
			}
			
		}
	}
	
	public static class LSHMapper extends Mapper<LongWritable, Text, HashBandIdWritable,LongWritable> {
		private HashBandIdWritable outkey = new HashBandIdWritable();
		private LongWritable outvalue = new LongWritable();
		private long [] longarrvalues;
		private int numRows;
		
		@Override
		protected void setup(Context context) throws IOException,
				InterruptedException {
			super.setup(context);
			this.numRows = context.getConfiguration().getInt(PARAM_NUM_ROWS_PER_BLOCK, 5);
			this.longarrvalues = new long[numRows];
		}
		
		public void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {
			StringTokenizer tokenizer = new StringTokenizer(value.toString()," ");
			int hashId =0,bandId=0;
			if(tokenizer.hasMoreTokens()){
				String token = tokenizer.nextToken();
				try{
					outvalue.set(Long.parseLong(token));
				}catch(Exception e){
					outvalue.set(Long.MAX_VALUE);
				}
			}
			
			while(tokenizer.hasMoreTokens()){
				String token = tokenizer.nextToken();
				log.info(hashId);
				
				try{
					longarrvalues[hashId]=Long.parseLong(token);
				}catch(NumberFormatException e){
					longarrvalues[hashId]=0;
				}
				
				if(hashId==numRows-1){
					outkey.setBandId(bandId);
					outkey.setHashValue(Arrays.hashCode(longarrvalues));
					context.write(outkey, outvalue);
					hashId=0;
					bandId++;
				}else{
					hashId++;
				}
			}
		
		}
	}
	public static class LSHReducer extends Reducer<HashBandIdWritable,LongWritable,HashBandIdWritable,Text> {
		private Text text = new Text();
		public void reduce(HashBandIdWritable key, Iterable<LongWritable> values, Context context) throws IOException, InterruptedException {
			StringBuilder builder = new StringBuilder();
			for(LongWritable value : values){
				builder.append(value.get()).append("\t");
			}
			text.set(builder.toString());
			context.write(key,text);
		}
	}
}
