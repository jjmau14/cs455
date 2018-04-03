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
        Hashtable<Integer, Integer[]> kv = new Hashtable<>();

        for(Text t : values){
            String s = t.toString();
            String[] arr = s.split("\\|");

            try {
                Integer dataKey = Integer.parseInt(arr[0]);
                Integer dataValue = Integer.parseInt(arr[1]);

                if (kv.containsKey(dataKey)) {
                    Integer[] newArr = kv.get(dataKey);
                    newArr[0] += dataValue; // Increment Value
                    newArr[1] += 1;         // Increment Counter
                    kv.replace(dataKey, newArr);
                } else {
                    kv.put(dataKey, new Integer[] {dataValue, 0});
                }
            } catch (NumberFormatException nfe) {

            }

        }

        Set<Integer> keys = kv.keySet();
        Iterator<Integer> iter = keys.iterator();

        int minKey = 0;
        int minVal = Integer.MAX_VALUE;

        while (iter.hasNext()) {
            Integer i = iter.next();

            if (kv.get(i)[0]/kv.get(i)[1] < minVal) {
                minKey = i;
                minVal = kv.get(i)[0]/kv.get(i)[1];
            }
        }

        context.write(key, new Text(minKey + ": Delay: " + minVal));

    }

}
