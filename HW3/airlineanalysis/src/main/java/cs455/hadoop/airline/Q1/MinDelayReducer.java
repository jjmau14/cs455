package cs455.hadoop.airline.Q1;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.Hashtable;

public class MinDelayReducer extends Reducer<Text, Text, Text, Text> {

    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        Hashtable<Integer, Integer> key_values = new Hashtable<>();
        context.write(key, new Text());
        for(Text t : values){

            try {
                String[] data = t.toString().split("|");
                int data_key = Integer.parseInt(data[0]);
                int data_value = Integer.parseInt(data[1]);

                if (key_values.containsKey(data_key)) {
                    key_values.put(data_key, (key_values.get(data_key) + data_value));
                } else {
                    key_values.put(data_key, data_value);
                }

            } catch (NumberFormatException nfe) {
                // pass
            }

        }

        int min_key = Integer.MAX_VALUE;
        int min_value = Integer.MAX_VALUE;

        for (Integer i : key_values.keySet()) {
            context.write(key, new Text(i + ": " + key_values.get(i)));
            if (key_values.get(i) < min_value) {
                min_key = i;
                min_value = key_values.get(i);
            }
        }


        context.write(key, new Text(min_key + ": " + min_value + "\n"));
    }

}
