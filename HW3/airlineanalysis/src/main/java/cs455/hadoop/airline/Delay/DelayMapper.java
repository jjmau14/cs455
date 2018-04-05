package cs455.hadoop.airline.Delay;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class DelayMapper extends Mapper<
        LongWritable,   /* Input Key */
        Text,           /* Input Value */
        Text,           /* Output Key */
        IntWritable     /* Output Value Type */
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

        String time, day, month;
        int delay;
        try {
            time = Integer.toString(Integer.parseInt(line[5]) / 100);
            day = getDay(Integer.parseInt(line[3]));
            month = getMonth(Integer.parseInt(line[1]));
            delay = Integer.parseInt(line[14]);
        } catch (Exception e){
            return;
        }

        context.write(new Text(time), new IntWritable(delay));
        context.write(new Text(day), new IntWritable(delay));
        context.write(new Text(month), new IntWritable(delay));

    }

    private String getDay(int i) throws Exception {
        switch(i) {
            case 1: return "mon";
            case 2: return "tue";
            case 3: return "wed";
            case 4: return "thu";
            case 5: return "fri";
            case 6: return "sat";
            case 7: return "sun";
            default: throw new Exception("Could not parse " + i + " into a day of week.");
        }
    }

    private String getMonth(int i) throws Exception {
        switch(i) {
            case 1: return "jan";
            case 2: return "feb";
            case 3: return "mar";
            case 4: return "apr";
            case 5: return "may";
            case 6: return "jun";
            case 7: return "jul";
            case 8: return "aug";
            case 9: return "sep";
            case 10: return "oct";
            case 11: return "nov";
            case 12: return "dec";
            default: throw new Exception("Could not parse " + i + " into a month.");
        }
    }

}
