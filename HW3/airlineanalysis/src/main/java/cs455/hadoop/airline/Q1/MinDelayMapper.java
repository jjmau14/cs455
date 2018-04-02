package cs455.hadoop.airline.Q1;

import cs455.hadoop.airline.util.IntArrayWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class MinDelayMapper extends Mapper<
        LongWritable,   /* Input Key */
        Text,           /* Input Value */
        Text,           /* Output Key */
        Text            /* Output Value Type */
    >{

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

        String time, day, month, delay;
        try {
            time = Integer.toString(Integer.parseInt(line[5]) / 100);
            day = line[3];
            month = line[1];
            delay = line[14];
        } catch (Exception e){
            return;
        }

        String TIME = time + "|" + delay;
        String DAY = day + "|" + delay;
        String MONTH = month + "|" + delay;

        context.write(new Text("Time"), new Text(TIME));
        context.write(new Text("Day"), new Text(DAY));
        context.write(new Text("Month"), new Text(MONTH));

    }

}
