package cs455.hadoop.airline.Q1;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.*;

public class MinDelayReducer extends Reducer<Text, IntWritable, Text, Text> {

    private Map<String, Integer[]> kv = new HashMap<>();

    @Override
    protected void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {

        for(IntWritable t : values){

            try {
                String dataKey = key.toString();
                Integer dataValue = t.get();


                /**
                 *  If map contains the certain day, or month... etc.
                 * */
                if (kv.containsKey(dataKey)) {
                    Integer[] newArr = kv.get(dataKey);
                    newArr[0] += dataValue; // Increment Value
                    newArr[1] += 1;         // Increment Counter
                    kv.replace(dataKey, newArr);
                } else {
                    kv.put(dataKey, new Integer[] {dataValue, 0});
                }
            } catch (NumberFormatException nfe) {

            }

        }

    }

    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException{
        Set<String> keys = kv.keySet();

        String topTime = "";
        int topTimeVal = Integer.MAX_VALUE;

        String topDay = "";
        int topDayVal = Integer.MAX_VALUE;

        String topMonth = "";
        int topMonthVal = Integer.MAX_VALUE;

        for (String key : keys) {
            if (isDay(key)) {
                if (kv.get(key)[0] < topDayVal) {
                    topDayVal = kv.get(key)[0];
                    topDay = key;
                }
            }
            else if (isMonth(key)) {
                if (kv.get(key)[0] < topMonthVal) {
                    topMonthVal = kv.get(key)[0];
                    topMonth = key;
                }
            }
            else {
                if (kv.get(key)[0] < topTimeVal) {
                    topTimeVal = kv.get(key)[0];
                    topTime = key;
                }
            }
        }

        context.write(new Text("Time"), new Text(topTime + Integer.toString(topTimeVal)));
        context.write(new Text("Day"), new Text(topDay + Integer.toString(topDayVal)));
        context.write(new Text("Month"), new Text(topMonth + Integer.toString(topMonthVal)));
    }

    private boolean isDay(String d) {
        switch (d) {
            case "mon": return true;
            case "tue": return true;
            case "wed": return true;
            case "thu": return true;
            case "fri": return true;
            case "sat": return true;
            case "sun": return true;
            default: return false;
        }
    }

    private boolean isMonth(String m) {
        switch (m) {
            case "jan": return true;
            case "feb": return true;
            case "mar": return true;
            case "apr": return true;
            case "may": return true;
            case "jun": return true;
            case "jul": return true;
            case "aug": return true;
            case "sep": return true;
            case "oct": return true;
            case "nov": return true;
            case "dec": return true;
            default: return false;
        }
    }

}
