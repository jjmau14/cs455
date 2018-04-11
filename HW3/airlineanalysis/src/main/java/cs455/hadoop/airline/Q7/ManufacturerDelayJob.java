package cs455.hadoop.airline.Q7;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.MultipleInputs;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

public class ManufacturerDelayJob {

    public static void main(String[] args) {
        try {
            Configuration conf = new Configuration();
            // Give the MapRed job a name. You'll see this name in the Yarn webapp.
            Job job = Job.getInstance(conf, "Q6 Weather Delay");
            // Current class.
            job.setJarByClass(ManufacturerDelayJob.class);

            // Reducer
            job.setReducerClass(ManufacturerDelayReducer.class);

            // Outputs from Reducer. It is sufficient to set only the following two properties
            // if the Mapper and Reducer has same key and value types. It is set separately for
            // elaboration.
            job.setOutputKeyClass(Text.class);
            job.setOutputValueClass(Text.class);

            // path to input in HDFS
            MultipleInputs.addInputPath(job, new Path(args[0]), TextInputFormat.class, ManufacturerDelayMapper.class);
            MultipleInputs.addInputPath(job, new Path(args[1]), TextInputFormat.class, ManufacturerMapper.class);


            // path to output in HDFS
            FileOutputFormat.setOutputPath(job, new Path(args[2]));


            // Block until the job is completed.
            job.waitForCompletion(true);

        } catch (IOException e) {
            System.err.println(e.getMessage());
        } catch (InterruptedException e) {
            System.err.println(e.getMessage());
        } catch (ClassNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }
}
