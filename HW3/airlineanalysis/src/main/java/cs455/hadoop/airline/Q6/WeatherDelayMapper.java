package cs455.hadoop.airline.Q6;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import javax.swing.text.EditorKit;
import java.io.IOException;

public class WeatherDelayMapper extends Mapper<
        LongWritable,   /* Input Key */
        Text,           /* Input Value */
        Text,           /* Output Key */
        Text            /* Output Value Type */
    >{

    @Override
    protected void map(LongWritable key, Text value, Context context)
            throws IOException, InterruptedException {

        String[] line = value.toString().split(",");

        // <origin iata, weatherDelay>
        try {
            if (Integer.parseInt(line[25]) > 0) {
                context.write(new Text(line[16]), new Text(line[25]));
            }
        } catch (Exception e) {
            ;
        }
    }

}
