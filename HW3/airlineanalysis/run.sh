$HADOOP_HOME/bin/hadoop jar target/airline-analysis-1.0-SNAPSHOT.jar cs455.hadoop.airline.Delay.DelayJob /data/main /out/Q1_2 &

$HADOOP_HOME/bin/hadoop jar target/airline-analysis-1.0-SNAPSHOT.jar cs455.hadoop.airline.Q3.BusiestAirportsJob /data/main /out/Q3 &

$HADOOP_HOME/bin/hadoop jar target/airline-analysis-1.0-SNAPSHOT.jar cs455.hadoop.airline.Q4.CarrierDelayJob /data/main /out/Q4 &

$HADOOP_HOME/bin/hadoop jar target/airline-analysis-1.0-SNAPSHOT.jar cs455.hadoop.airline.Q5.PlaneAgeJob /data/main /data/supplementary/plane-data.csv /out/Q5 &

$HADOOP_HOME/bin/hadoop jar target/airline-analysis-1.0-SNAPSHOT.jar cs455.hadoop.airline.Q6.WeatherDelayJob /data/main/ /data/supplementary/airports.csv /out/Q6 &
