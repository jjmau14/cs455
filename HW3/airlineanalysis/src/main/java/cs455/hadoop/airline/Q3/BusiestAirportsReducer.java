package cs455.hadoop.airline.Q3;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BusiestAirportsReducer extends Reducer<
        Text,   /* Input Key Type */
        Text,   /* Input Value Type */
        Text,   /* Output Key Type */
        Text    /* Output Value Type */
    >{

    // map of years to
    private Map<String, HashMap<String, Integer>> counts = new HashMap<>();

    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context)
            throws IOException, InterruptedException {

        /**
         * Receives: <airport, year>
         * */

        for (Text t : values) {
            String year = t.toString();
            String airport = key.toString();
            if (counts.containsKey(year)) {
                if (counts.get(year).containsKey(airport)) {
                    counts.get(year).replace(airport, counts.get(year).get(airport).intValue() + 1);
                } else {
                    counts.get(year).put(airport, 1);
                }
            } else {
                counts.put(year, new HashMap<>());
                counts.get(year).put(airport, 1);
            }
        }

        String s = "";
        Map<String, Integer> m = counts.get("2000");
        for (String k : m.keySet()) {
            s += ", " + Integer.toString(m.get(k));
        }
        context.write(new Text("2000"), new Text(s));
    }

    /*@Override
    protected void cleanup(Context context) {
        Set<String> years = counts.keySet();



        for (String year : years) {
            ArrayList<String> airport = new ArrayList<>();
            ArrayList<Integer> count = new ArrayList<>();


        }

    }*/


}
