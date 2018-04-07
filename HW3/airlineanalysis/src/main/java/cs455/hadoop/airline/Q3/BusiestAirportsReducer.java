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

    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context)
            throws IOException, InterruptedException {

        int count = 0;
        for (Text t : values) {
            count += 1;
        }
        context.write(key, new Text(Integer.toString(count)));
    }



}
