package cs455.hadoop.airline.Q4;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class CarrierDelayReducer extends Reducer<
        Text,   /* Input Key Type */
        IntWritable,   /* Input Value Type */
        Text,   /* Output Key Type */
        Text    /* Output Value Type */
    >{

    private HashMap<String, HashMap<String, Integer>> counts = new HashMap<>();

    @Override
    protected void reduce(Text key, Iterable<IntWritable> values, Context context)
            throws IOException, InterruptedException {

        int count = 0;
        int numFlights = 0;
        for (IntWritable i : values) {
            count += i.get();
            numFlights++;
        }
        context.write(key, new Text("Flights Delayed: " + numFlights + " for " + count + " minutes. Average delay of: " + (count/numFlights) + " minutes"));
    }

    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException {

    }

}
