package cs455.hadoop.airline.Q1;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Set;

public class MinDelayReducer extends Reducer<Text, Text, Text, Text> {

    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        Hashtable<Integer, Integer> kv = new Hashtable<>();

        for(Text t : values){
            String s = "";
            for (int i = 0 ; i < t.getLength() ; i++) {
                s += t.toString();
            }
            String a = s.substring(0, s.indexOf("|"));
            String b = s.substring(s.indexOf("|")+1, s.length());
            context.write(key, new Text(a + ": " + b));
        }

    }

}
