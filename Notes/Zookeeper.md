$ zookeeper/bin/zkCli.sh -server localhost:2181 #Make sure your Broker is already running
 
$  ls /brokers/ids  # Gives the list of active brokers
$  ls /brokers/topics #Gives the list of topics
$  get /brokers/ids/0
