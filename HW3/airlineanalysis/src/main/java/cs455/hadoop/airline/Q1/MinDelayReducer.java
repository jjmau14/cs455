package cs455.hadoop.airline.Q1;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

public class MinDelayReducer extends Reducer<Text, Text, Text, Text> {

    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        Hashtable<Integer, Integer> kv = new Hashtable<>();

        for(Text t : values){
            String s = t.toString();
            String[] arr = s.split("\\|");

            Integer dataKey = 0;
            Integer dataValue = 0;

            try {
                dataKey = Integer.parseInt(arr[0]);
                dataValue = Integer.parseInt(arr[1]);

            } catch (Exception e) {

            }

            if (kv.containsKey(dataKey)) {
                kv.replace(dataKey, kv.get(dataKey) + dataValue);
            } else {
                kv.put(dataKey, dataValue);
            }
            
        }

        Set<Integer> keys = kv.keySet();
        Iterator<Integer> iter = keys.iterator();
        boolean setInit = true;
        int minKey = 0;
        int minVal = Integer.MAX_VALUE;

        while (iter.hasNext()) {
            Integer i = iter.next();

            if (kv.get(i) < minVal) {
                minKey = i;
                minVal = kv.get(i);
            }
        }

        context.write(key, new Text(minKey + ": Delay: " + minVal));

    }

}
