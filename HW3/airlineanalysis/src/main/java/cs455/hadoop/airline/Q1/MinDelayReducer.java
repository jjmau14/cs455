package cs455.hadoop.airline.Q1;

import cs455.hadoop.airline.util.IntArrayWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.HashMap;

public class MinDelayReducer extends Reducer<Text, IntArrayWritable, Text, Text> {

    @Override
    protected void reduce(Text key, Iterable<IntArrayWritable> values, Context context) throws IOException, InterruptedException {
        HashMap<Integer, Integer> minimizer = new HashMap<>();

        for(IntArrayWritable t : values){
            String[] arr = (String[]) t.toArray();

            if (!minimizer.containsKey(arr)){
                minimizer.put(Integer.parseInt(arr[0]), Integer.parseInt(arr[1]));
            } else {
                minimizer.replace(Integer.parseInt(arr[0]), minimizer.get(Integer.parseInt(arr[0])) + Integer.parseInt(arr[1]));
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
