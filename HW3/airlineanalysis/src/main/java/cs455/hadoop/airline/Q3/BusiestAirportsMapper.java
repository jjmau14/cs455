package cs455.hadoop.airline.Q3;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class BusiestAirportsMapper extends Mapper<
        LongWritable,   /* Input Key */
        Text,           /* Input Value */
        Text,           /* Output Key */
        Text            /* Output Value Type */
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

        String year = line[0];
        String origin = line[16];
        String dest = line[17];

        context.write(new Text(origin), new Text(year));
        context.write(new Text(dest), new Text(year));

    }

}
