package cs455.hadoop.airline.util;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.ArrayWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.DataOutput;
import java.io.IOException;

public class IntArrayWritable extends ArrayWritable {
    public IntArrayWritable(IntWritable[] intWritables) {
        super(IntWritable.class);
    }

    @Override
    public IntWritable[] get() {
        return (IntWritable[]) super.get();
    }

    @Override
    public void write(DataOutput arg0) throws IOException {
        for(IntWritable data : get()){
            data.write(arg0);
        }
    }
}
