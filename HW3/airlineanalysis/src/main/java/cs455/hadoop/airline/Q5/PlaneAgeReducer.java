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

    private int[] oldVsNew = new int[] {0,0};
    private static final int OLD = 0;
    private static final int NEW = 1;

    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context)
            throws IOException, InterruptedException {

        HashMap<Integer, Integer> delays = new HashMap<>();

        // Store all delays in hashmap of <yearOfFlight, delayTime>
        // count delays > 20yrs and < 20yrs.
        // count delays > 20 yrs and < 20 yrs
        // cleanup, context write delays >20 and <20

        int yearOfPlane = 0;
        for (Text t : values) {
            String data = t.toString();

            int arrDelay = 0;
            int yearOfFlight = 0;

            String[] splitData = data.split("\\|");
            if (splitData.length == 2) {
                try {
                    arrDelay = Integer.parseInt(splitData[0]);
                    yearOfFlight = Integer.parseInt(splitData[1]);
                } catch (Exception e) {
                    continue;
                }

                if (delays.containsKey(yearOfFlight)) {
                    delays.replace(yearOfFlight, delays.get(yearOfFlight) + arrDelay);
                } else {
                    delays.put(yearOfFlight, arrDelay);
                }
            } else {
                yearOfPlane = Integer.parseInt(splitData[0]);
            }
        }

        for (Integer yearOfFlight : delays.keySet()) {
            // New Planes
            if ((yearOfFlight-20) < yearOfPlane) {
                oldVsNew[NEW] += delays.get(yearOfFlight);
            // Old Planes
            } else {
                oldVsNew[OLD] += delays.get(yearOfFlight);
            }
        }

    }

    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException {
        ArrayList<String> totals = new ArrayList<>();

        if (oldVsNew[OLD] > oldVsNew[NEW]) {
            context.write(
                    new Text("Old Planes performed worse: "),
                    new Text(oldVsNew[OLD] + " vs " + oldVsNew[NEW])
            );
        } else {
            context.write(
                    new Text("New Planes performed worse: "),
                    new Text(oldVsNew[NEW] + " vs " + oldVsNew[OLD])
            );
        }
    }

}
