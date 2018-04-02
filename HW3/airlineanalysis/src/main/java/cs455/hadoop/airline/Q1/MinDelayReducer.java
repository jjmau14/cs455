package cs455.hadoop.airline.Q1;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

public class MinDelayReducer extends Reducer<Text, Text, Text, Text> {

    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        HashMap<Integer, Integer> minimizer = new HashMap<>();
        String m = "test";
        context.write(key, new Text(m));
        for(Text t : values){
            try {
                String[] arr = t.toString().split("|");
                context.write(key, new Text(Arrays.toString(arr)));
                if (!minimizer.containsKey(arr[0])) {
                    minimizer.put(Integer.parseInt(arr[0]), Integer.parseInt(arr[1]));
                } else {
                    minimizer.put(Integer.parseInt(arr[0]), minimizer.get(Integer.parseInt(arr[0])) + Integer.parseInt(arr[1]));
                }
            } catch (NumberFormatException nfe) {
                // pass
            }
        }

        int key_name = 0;
        int value = 0;
        for (Integer i : minimizer.keySet()) {
            String s = i + ": " + minimizer.get(i);
            context.write(key, new Text(s));
            if (minimizer.get(i) > value) {
                value = minimizer.get(i);
                key_name = i;
            }
        }

        context.write(key, new Text(key_name + ": " + value + "\n"));
    }

}
