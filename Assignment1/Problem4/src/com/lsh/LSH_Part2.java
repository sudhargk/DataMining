package com.lsh;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

public class LSH_Part2 {
	public static final Log log = LogFactory.getLog(LSH_Part2.class);
	public static final String PARAM_NUM_ROWS_PER_BLOCK="ROWS_PER_BLOCK";
	public static class CandidatePairs implements WritableComparable<CandidatePairs>{
		long docId1;
		long docId2;
		
		public void setDocId1(long docId) {
			this.docId1 = docId;
		}
		
		public void setDocId2(long docId) {
			this.docId2 = docId;
		}
		
		@Override
		public void readFields(DataInput in) throws IOException {
			this.docId1 = in.readLong();
			this.docId2 = in.readLong();
		}
		@Override
		public void write(DataOutput out) throws IOException {
			out.writeLong(docId1);
			out.writeLong(docId2);
		}
		@Override
		public String toString() {
			return  "("+docId1 + ","+docId2+")";
		}
		@Override
		public int compareTo(CandidatePairs with) {
			if(this.docId1>with.docId1){
				return 1;
			}else if(this.docId1==with.docId1){
				if(this.docId2>with.docId2){
					return 1;
				}else if(this.docId2<with.docId2){
					return -1;
				}else{
					return 0;
				}
			}else{
				return -1;
			}
			
		}
	}
	
	public static class LSHMapper extends Mapper<LongWritable, Text, CandidatePairs,NullWritable> {
		private CandidatePairs outkey = new CandidatePairs();
		
		public void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {
			StringTokenizer tokenizer = new StringTokenizer(value.toString(),"\t");
			List<Long> docKeys = new ArrayList<Long>();
			//skipping first two columns
			if(tokenizer.hasMoreTokens()){
				log.info(tokenizer.nextToken());
			}
			
			if(tokenizer.hasMoreTokens()){
				log.info(tokenizer.nextToken());
			}
			
			while(tokenizer.hasMoreTokens()){
				try{
					long currKey = Long.parseLong(tokenizer.nextToken());
					for(Long docKey:docKeys){
						if(currKey>docKey){
							outkey.setDocId1(docKey);
							outkey.setDocId2(currKey);
						}else{
							outkey.setDocId1(currKey);
							outkey.setDocId2(docKey);
						}
						context.write(outkey, NullWritable.get());
					}
					docKeys.add(currKey);
				}catch(NumberFormatException e){
					
				}
			}
		
		}
	}
	public static class LSHReducer extends Reducer<CandidatePairs,NullWritable,CandidatePairs,NullWritable> {
		public void reduce(CandidatePairs key, Iterable<NullWritable> values, Context context) throws IOException, InterruptedException {
			log.info("Part 2 reducer");
			log.info(key);
			context.write(key,NullWritable.get());
		}
	}
}
