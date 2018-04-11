Josh Mau
CS455 HW3-PC
#####################################################

Packages: (cs455.hadoop.airline):
	Delay: Contains files used to answer questions 1 and 2.
	Q3: Contains files used to answer question 3.
	Q4: Contains files used to answer question 4.
	Q5: Contains files used to answer question 5.
	Q6: Contains files used to answer question 6.
	Q7: Contains files used to answer question 7.

#####################################################

To Compile:
	
	Run `ant` command. The build.xml file will be used to generate
	a dist directory containing an "airline.jar" file that can be 
	used to run any of the above main functions in the packages.

#####################################################

Some of these programs output multiple files (per number of reducer) but generally
output one result per page. The following command can be used to output all the files
at once:

	
	$HADOOP_HOME/bin/hdfs -dfs -cat /(output_directory)/part*


This will `cat` all of the part-0-0000* outputs. Other programs will output
one part-0-0000* file containing multiple (or top 10) results.

#####################################################



