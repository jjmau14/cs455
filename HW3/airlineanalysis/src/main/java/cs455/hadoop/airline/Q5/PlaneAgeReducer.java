package cs455.hadoop.airline.Q5;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.HashMap;

public class PlaneAgeReducer extends Reducer<
        Text,   /* Input Key Type */
        Text,   /* Input Value Type */
        Text,   /* Output Key Type */
        Text    /* Output Value Type */
    >{

    private HashMap<String, HashMap<String, Integer>> counts = new HashMap<>();

    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context)
            throws IOException, InterruptedException {

        String s = "";
        for (Text t : values) {
            s += " " + t.toString();
        }
        context.write(key, new Text(s));
    }

    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException {

    }

}
