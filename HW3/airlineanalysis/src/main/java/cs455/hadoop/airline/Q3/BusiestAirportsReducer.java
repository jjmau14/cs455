package cs455.hadoop.airline.Q3;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.*;

public class BusiestAirportsReducer extends Reducer<
        Text,   /* Input Key Type */
        Text,   /* Input Value Type */
        Text,   /* Output Key Type */
        Text    /* Output Value Type */
    >{

    private HashMap<String, Integer> counts = new HashMap<>();

    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context)
            throws IOException, InterruptedException {

        int count = 0;
        for (Text t : values) {
            count += 1;
        }

        counts.put(key.toString(), count);
    }

    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException {
        ArrayList<String> top10Airports = new ArrayList<>();
        for (String key : counts.keySet()) {
            if (top10Airports.size() < 10) {
                top10Airports.add(key);
            } else {
                top10Airports.add(key);
                Collections.sort(top10Airports);
                top10Airports.remove(10);
            }
        }
        for (int i = 0 ; i < 10 ; i++) {
            context.write(new Text(top10Airports.get(i)), new Text(Integer.toString(counts.get(top10Airports.get(i)))));
        }
    }

}
