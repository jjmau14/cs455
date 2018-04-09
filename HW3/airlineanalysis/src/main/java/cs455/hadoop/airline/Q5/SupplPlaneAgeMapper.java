package cs455.hadoop.airline.Q5;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class SupplPlaneAgeMapper extends Mapper<
        LongWritable,   /* Input Key */
        Text,           /* Input Value */
        Text,           /* Output Key */
        Text            /* Output Value Type */
    >{

    @Override
    protected void map(LongWritable key, Text value, Context context)
            throws IOException, InterruptedException {

        String[] line = value.toString().split(",");

        try {
            String year = line[8];
            String tailNum = line[0];
            context.write(new Text(tailNum), new Text(year));
        } catch (Exception e) {
            // skip bad record
        }
    }

}
