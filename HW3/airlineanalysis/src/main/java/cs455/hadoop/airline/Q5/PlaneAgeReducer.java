package cs455.hadoop.airline.Q5;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.*;

public class PlaneAgeReducer extends Reducer<
        Text,   /* Input Key Type */
        Text,   /* Input Value Type */
        Text,   /* Output Key Type */
        Text    /* Output Value Type */
    >{

    private HashMap<String, Integer> delays = new HashMap<>();

    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context)
            throws IOException, InterruptedException {

        int i = 0;
        String year = "";
        int dataOut = 0;
        for (Text t : values) {
            String data = t.toString();

            if (data.equals("NA"))
                continue;

            try {
                int dataInt = Integer.parseInt(data);
                if (dataInt > 1900) {
                    delays.put(Integer.toString(dataInt), dataOut);
                    year = Integer.toString(dataInt);
                } else {
                    if (!year.equals("")) {
                        delays.replace(year, dataOut);
                    }
                    dataOut += dataInt;
                    i++;
                }
            } catch (Exception e){}

        }

        if (!year.equals("")) {
            context.write(key, new Text(year + ": " + (i == 0 ? dataOut : (dataOut/i))));
        }
    }

    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException {
        ArrayList<String> totals = new ArrayList<>();

        for (String key : delays.keySet()) {
            if (totals.size() == 0) {
                totals.add(key);
            } else {
                totals.add(key);
                Collections.sort(totals, new Comparator<String>() {
                    @Override
                    public int compare(String o1, String o2) {
                        return (-1)*(delays.get(o1).compareTo(delays.get(o2)));
                    }
                });
                totals.remove(1);
            }
        }

        context.write(new Text("Worst manufactured year: "), new Text(totals.get(0)));
    }

}
