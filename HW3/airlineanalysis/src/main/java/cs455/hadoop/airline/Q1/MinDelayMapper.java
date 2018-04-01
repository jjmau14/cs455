package cs455.hadoop.airline.Q1;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class MinDelayMapper extends Mapper<LongWritable, Text, Text, IntWritable> {

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
        String time = line[5];
        String day = getDay(line[3]);
        String month = getMonth(line[1]);
        int delay = Integer.parseInt(line[14]);

        context.write(new Text(time), new IntWritable(delay));
        context.write(new Text(day), new IntWritable(delay));
        context.write(new Text(month), new IntWritable(delay));

    }

    private String getDay(String dayNumber) {
        switch (dayNumber) {
            case "1": return "mon";
            case "2": return "tue";
            case "3": return "wed";
            case "4": return "thu";
            case "5": return "fri";
            case "6": return "sat";
            case "7": return "sun";
            default: return null;
        }
    }

    private String getMonth(String monthNumber) {
        switch (monthNumber) {
            case "1": return "jan";
            case "2": return "feb";
            case "3": return "mar";
            case "4": return "apr";
            case "5": return "may";
            case "6": return "jun";
            case "7": return "jul";
            case "8": return "aug";
            case "9": return "sep";
            case "10": return "oct";
            case "11": return "nov";
            case "12": return "dec";
            default: return null;
        }
    }

}
