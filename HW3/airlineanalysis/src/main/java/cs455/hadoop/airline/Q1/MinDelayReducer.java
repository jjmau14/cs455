package cs455.hadoop.airline.Q1;

import org.apache.hadoop.io.ArrayWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.HashMap;

public class MinDelayReducer extends Reducer<Text, ArrayWritable, Text, Text> {

    @Override
    protected void reduce(Text key, Iterable<ArrayWritable> values, Context context) throws IOException, InterruptedException {
        HashMap<Integer, Integer> minimizer = new HashMap<>();

        for(ArrayWritable t : values){
            Integer[] arr = (Integer[]) t.toArray();

            if (!minimizer.containsKey(arr)){
                minimizer.put(arr[0], arr[1]);
            } else {
                minimizer.replace(arr[0], minimizer.get(arr[0]) + arr[1]);
            }
        }

        int key_name = 0;
        int value = 0;
        for (Integer i : minimizer.keySet()) {
            if (minimizer.get(i) > value) {
                value = minimizer.get(i);
                key_name = i;
            }
        }

        context.write(key, new Text(key_name + ": " + value));
    }

}
