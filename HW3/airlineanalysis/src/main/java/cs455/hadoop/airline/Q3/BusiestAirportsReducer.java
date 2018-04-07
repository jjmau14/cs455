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

        HashMap<String, Integer> totalAirports = new HashMap<>();

        for (String year : counts.keySet()) {

            ArrayList<String> top10AirportsByYear = new ArrayList<>();

            for (String airport : counts.get(year).keySet()) {

                if (top10AirportsByYear.size() < 10) {
                    top10AirportsByYear.add(airport);
                } else {
                    top10AirportsByYear.add(airport);
                    Collections.sort(top10AirportsByYear, new Comparator<String>() {
                        @Override
                        public int compare(String o1, String o2) {
                            return (-1)*(counts.get(year).get(o1).compareTo(counts.get(year).get(o2)));
                        }
                    });
                    top10AirportsByYear.remove(10);
                }

                if (totalAirports.containsKey(airport)) {
                    totalAirports.replace(airport, totalAirports.get(airport) + counts.get(year).get(airport));
                } else {
                    totalAirports.put(airport, counts.get(year).get(airport));
                }
            }

            String s = "";
            for (int i = 0 ; i < 10 ; i++) {
                s += top10AirportsByYear.get(i) + ": " + counts.get(year).get(top10AirportsByYear.get(i)) + "\n";
            }
            context.write(new Text(year), new Text(s));
        }

        ArrayList<String> sortedTotals = new ArrayList<>();
        for (String airport : totalAirports.keySet()) {
            if (sortedTotals.size() < 10)
                sortedTotals.add(airport);
            else {
                sortedTotals.add(airport);
                Collections.sort(sortedTotals, new Comparator<String>() {
                    @Override
                    public int compare(String o1, String o2) {
                        return (-1)*(totalAirports.get(o1).compareTo(totalAirports.get(o2)));
                    }
                });
                sortedTotals.remove(10);
            }
        }

        for (int i = 0 ; i < 10 ; i++) {
            context.write(new Text(sortedTotals.get(i)), new Text(Integer.toString(totalAirports.get(sortedTotals.get(i)))));
        }
    }

}
