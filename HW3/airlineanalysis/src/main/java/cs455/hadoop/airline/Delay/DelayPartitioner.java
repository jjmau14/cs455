package cs455.hadoop.airline.Delay;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Partitioner;

public class DelayPartitioner extends Partitioner<Text, Text>{

    @Override
    public int getPartition(Text key, Text value, int numReduceTasks) {
        switch (key.toString()) {
            case "time": return 0;
            case "day": return 1;
            default: return 2;
        }
    }
}
