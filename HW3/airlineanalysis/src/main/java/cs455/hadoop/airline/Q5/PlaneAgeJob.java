package cs455.hadoop.airline.Q5;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.MultipleInputs;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

public class PlaneAgeJob {

    public static void main(String[] args) {
        try {
            Configuration conf = new Configuration();
            // Give the MapRed job a name. You'll see this name in the Yarn webapp.
            Job job = Job.getInstance(conf, "Q5 Plane Age Delay");
            // Current class.
            job.setJarByClass(PlaneAgeJob.class);

            // Reducer
            job.setReducerClass(PlaneAgeReducer.class);

            // Outputs from Reducer. It is sufficient to set only the following two properties
            // if the Mapper and Reducer has same key and value types. It is set separately for
            // elaboration.
            job.setOutputKeyClass(Text.class);
            job.setOutputValueClass(Text.class);

            job.setNumReduceTasks(10);
            // path to input in HDFS
            MultipleInputs.addInputPath(job, new Path(args[0]), TextInputFormat.class, PlaneAgeMapper.class);
            MultipleInputs.addInputPath(job, new Path(args[1]), TextInputFormat.class, SupplPlaneAgeMapper.class);


            // path to output in HDFS
            FileOutputFormat.setOutputPath(job, new Path(args[2]));


            // Block until the job is completed.
            job.waitForCompletion(true);
            FileSystem fs = FileSystem.get(new Configuration());
            FileStatus[] status = fs.listStatus(new Path("hdfs://denver:4601/" + args[2]));
            for (int i=0;i<status.length;i++){
                fs.open(status[i].getPath());
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } catch (InterruptedException e) {
            System.err.println(e.getMessage());
        } catch (ClassNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }
}
