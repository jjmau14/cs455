package cs455.hadoop.airline.Q3;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.util.hash.Hash;

import java.io.IOException;
import java.util.*;

public class BusiestAirportsReducer extends Reducer<
        Text,   /* Input Key Type */
        Text,   /* Input Value Type */
        Text,   /* Output Key Type */
        Text    /* Output Value Type */
    >{

    private HashMap<String, HashMap<String, Integer>> counts = new HashMap<>();

    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context)
            throws IOException, InterruptedException {

        HashMap<String, Integer> temp = new HashMap<>();

        for (Text t : values) {
            String dataKey = t.toString();
            if (temp.containsKey(dataKey)) {
                temp.replace(dataKey, temp.get(dataKey) + 1);
            } else {
                temp.put(dataKey, 1);
            }
        }

        counts.put(key.toString(), temp);
    }

    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException {

        ArrayList<String> years = new ArrayList<>();
        for (String year : counts.keySet()) {
            years.add(year);
        }
        Collections.sort(years);
        for (String year : years) {
            ArrayList<String> airports = new ArrayList<>();

            for (String airport : counts.get(year).keySet()) {
                if (airports.size() < 10)
                    airports.add(airport);
                else {
                    airports.add(airport);
                    Collections.sort(airports, new Comparator<String>() {
                        @Override
                        public int compare(String o1, String o2) {
                            return (-1)*(counts.get(year).get(o1).compareTo(counts.get(year).get(o2)));
                        }
                    });
                    airports.remove(10);
                }
            }
            String s = "";
            for (String airport : airports) {
                s += airport + ": " + counts.get(year).get(airport) + " ";
            }
            context.write(new Text(year), new Text(s));
        }
    }

}
