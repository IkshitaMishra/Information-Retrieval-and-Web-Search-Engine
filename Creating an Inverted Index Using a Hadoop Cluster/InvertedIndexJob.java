import java.io.IOException;
import java.util.HashMap;
import java.util.ArrayList;

import java.util.Arrays; 
import java.util.StringTokenizer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import java.util.Iterator;
import java.util.Map;
import java.util.List;
import java.lang.StringBuilder;
public class InvertedIndexJob {

 public static class Map1 extends Mapper < LongWritable, Text, Text, Text > {
	
  	Text documentID= new Text();
	private Text word = new Text();
	public void map(LongWritable key, Text value, Context context) throws IOException,InterruptedException {
		List<String> aList= new ArrayList<>(Arrays.asList(value.toString().split("\t", 2)));
                documentID.set(String.valueOf(aList.get(0)));
		String lin = aList.get(1).replaceAll("[^a-zA-Z0-9\\s+]", " ").replaceAll("[0-9\\s+]", " ").toLowerCase();
		StringTokenizer tokens = new StringTokenizer(lin);
   		while (tokens.hasMoreTokens()) {
        		word.set(tokens.nextToken());
        		context.write(word,documentID);
      		}
  	}
 }


 public static class Red1 extends Reducer <Text,Text,Text,Text> {
 	public void reduce(Text key, Iterable <Text> values, Context context) throws IOException,InterruptedException {
		String value;
                HashMap<String, Integer> hmap = new HashMap();
                Iterator <Text> tok = values.iterator();
   		while (tok.hasNext()) {
    			value = tok.next().toString();
    			if (hmap.containsKey(value)) {
     				int count = (hmap.get(value));
     				count = count + 1;
     				hmap.put(value, new Integer(count));

    			} else {
     				hmap.put(value, new Integer(1));
    			}
		}
   		StringBuilder sbuilder = new StringBuilder();
		// Using a for-each
        	for (Map.Entry<String, Integer> e: hmap.entrySet()) {
            		sbuilder.append(e.getKey() + " : " + e.getValue() + " ");
		}
		Text op = new Text(sbuilder.toString());
                context.write(key, op);
	}
 }

 public static void main(String[] args) throws Exception {
 			Configuration con = new Configuration();
                        Job job_mr = Job.getInstance(con, "Hadoop Indexing");
                        job_mr.setJarByClass(InvertedIndexJob.class);
                        job_mr.setMapperClass(Map1.class);
                        job_mr.setReducerClass(Red1.class);
                        job_mr.setMapOutputKeyClass(Text.class);
                        job_mr.setMapOutputValueClass(Text.class);
                        job_mr.setOutputKeyClass(Text.class);
                        job_mr.setOutputValueClass(Text.class);
                        FileInputFormat.addInputPath(job_mr, new Path(args[0]));
                        FileOutputFormat.setOutputPath(job_mr, new Path(args[1]));
                        System.exit(job_mr.waitForCompletion(true)? 0 : 1);
 }
}