package cs455.hadoop.airline.Q1;

import cs455.hadoop.airline.util.IntArrayWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class MinDelayMapper extends Mapper<LongWritable, Text, Text, IntArrayWritable> {

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

        /**
         * Split the current working line on commas into and array.
         * */
        String[] line = value.toString().split(",");

        /**
         * For Time: [6] CRSDepTime Scheduled Departure Time (local time, hhmm)
         * For Day: [4] DayOfWeek 1 (Monday) – 7 (Sunday)
         * For Month: [2] Month Between 1 - 12
         * For Delay: [15] ArrDelay Arrival delay (in minutes)
         *
         * --> Subtracting 1 on all indexes since "line" is 0-based
         *
         * */

        int time, day, month, delay;
        try {
            time = Integer.parseInt(line[5]) / 100;
            day = Integer.parseInt(line[3]);
            month = Integer.parseInt(line[1]);
            delay = Integer.parseInt(line[14]);
        } catch (Exception e){
            return;
        }

        context.write(new Text("Time"), new IntArrayWritable(
                new IntWritable[] {
                        new IntWritable(time),
                        new IntWritable(delay)
                }
        ));
        context.write(new Text("Day"), new IntArrayWritable(
                new IntWritable[] {
                        new IntWritable(day),
                        new IntWritable(delay)
                }
        ));
        context.write(new Text("Month"), new IntArrayWritable(
                new IntWritable[] {
                        new IntWritable(month),
                        new IntWritable(delay)
                }
        ));

    }

}
