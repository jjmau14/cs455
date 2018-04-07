package cs455.hadoop.airline.Q3;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Partitioner;

public class BusiestAirportPartitioner extends Partitioner<Text, Text>{

    @Override
    public int getPartition(Text key, Text value, int numReduceTasks) {
        return 2008 - Integer.parseInt(key.toString());
    }
}
