package cs455.hadoop.airline.Q4;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class CarrierDelayMapper extends Mapper<
        LongWritable,   /* Input Key */
        Text,           /* Input Value */
        Text,           /* Output Key */
        IntWritable     /* Output Value Type */
    >{

    @Override
    protected void map(LongWritable key, Text value, Context context)
            throws IOException, InterruptedException {

        String[] line = value.toString().split(",");

        /**
         * Year: to determine change over 21 year period
         * origin: will output 1
         * dest: will output 1
         *
         * --> Subtracting 1 on all indexes since "line" is 0-based
         *
         * */

        String carrier = line[8];
        int delay = Integer.parseInt(line[24]);

        if (!carrier.equals("UniqueCarrier")) {
            context.write(new Text(carrier), new IntWritable(delay));
        }
    }

}
