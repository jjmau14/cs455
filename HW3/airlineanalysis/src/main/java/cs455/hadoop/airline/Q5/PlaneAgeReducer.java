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
                    year = Integer.toString(dataInt);
                } else {
                    dataOut += dataInt;
                    i++;
                }
            } catch (Exception e){}

        }

        if (!year.equals("")) {
            context.write(key, new Text(year + ": " + (dataOut/i)));
        }
    }

}
