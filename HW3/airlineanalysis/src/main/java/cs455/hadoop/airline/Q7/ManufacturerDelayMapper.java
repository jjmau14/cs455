package cs455.hadoop.airline.Q7;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class ManufacturerDelayMapper extends Mapper<
        LongWritable,   /* Input Key */
        Text,           /* Input Value */
        Text,           /* Output Key */
        Text            /* Output Value Type */
    >{

    @Override
    protected void map(LongWritable key, Text value, Context context)
            throws IOException, InterruptedException {

        String[] line = value.toString().split(",");

        // <tailnum, delay>
        context.write(new Text(line[10]), new Text(line[14]));
    }

}
