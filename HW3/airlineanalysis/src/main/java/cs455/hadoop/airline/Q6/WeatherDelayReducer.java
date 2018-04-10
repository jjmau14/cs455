package cs455.hadoop.airline.Q6;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.util.hash.Hash;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class WeatherDelayReducer extends Reducer<
        Text,   /* Input Key Type */
        Text,   /* Input Value Type */
        Text,   /* Output Key Type */
        Text    /* Output Value Type */
    >{

    // <String (Airport Code), (Integer) Weather Delay Time>
    private HashMap<String, Integer> delays = new HashMap<>();

    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context)
            throws IOException, InterruptedException {

        // Store all delays in hashmap of <yearOfFlight, delayTime>
        // count delays > 20yrs and < 20yrs.
        // count delays > 20 yrs and < 20 yrs
        // cleanup, context write delays >20 and <20

        String nameOfAirport = "";
        int delay = 0;

        for (Text t : values) {
            String data = t.toString();

            try {
                int x = Integer.parseInt(data);
                delay += x;
            } catch (Exception e) {
                nameOfAirport = data.replace("\"", "");
            }
            context.write(key, new Text(Integer.toString(delay)));

        }

        if (nameOfAirport != "" && delay > 0) {
            //delays.put(nameOfAirport, delay);
        }
        //context.write(key, new Text(new String(nameOfAirport + ": " + delay)));

    }

    /*@Override
    protected void cleanup(Context context) throws IOException, InterruptedException {
        ArrayList<String> airports = new ArrayList<>();

        for (String airport : delays.keySet()) {
            if (airports.size() < 10) {
                airports.add(airport);
            } else {
                airports.add(airport);
                Collections.sort(airports, new Comparator<String>() {
                    @Override
                    public int compare(String o1, String o2) {
                        return (-1)*(delays.get(o1).compareTo(delays.get(o2)));
                    }
                });
                airports.remove(10);
            }
        }
        for (int i = 0 ; i < 10 ; i++) {
            context.write(new Text(airports.get(i)), new Text(Integer.toString(delays.get(airports.get(i)))));
        }
    }*/

}
