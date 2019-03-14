kafka-server-start $KAFKA_HOME/etc/kafka/server-1.properties
kafka-server-start $KAFKA_HOME/etc/kafka/server-2.properties
kafka-server-start $KAFKA_HOME/etc/kafka/server-3.properties


Process
    Set of Instruction, Program space, Data Space
    Started
    Stoped - loose all the data
    
    Threads - Execution Context
    
    Thread execute the instructions
    Multi-threads? Concurrent/Parallel work can be done with in the same process
    
    1 process => 'n' threads
    
    Task - User program logic, a small function/logic that to be executed
    Task 1..Task N
    
    Kafka - Topology is convereted into TASK 1, Task 2...Task N
    
    To Execute a task, you a need thread
    
    Task Scheduler, 
        take a task from a queue, assign to a thread
        thread execute a task
        thread release the task
        
        
KSTREAM/STREAM

Add Product 1 to CART
       Value - Product 1

Add Product 1 to CART
       Value - Product 1
       
Add Product 2 to CART
       Value - Product 2
       

Add Product 3 to CART
       Value - Product 3
       
     
Add Product 2 to CART
       Value - Product 2
       
       
Shopping Cart - KTable -- aggregation

Product Name          Quantity
 Product 1              2               [Insert], [Updated]
 Product 2              2               [Insert], [Updated]
 Product 3              1               [Insert]

Table Change log - whenever new data added/updated, publish the changed set as stream

[Product 1, Quantity : 1] - STREAM
[Product 1, Quantity : 2] - STREAM
[Product 2, Quantity : 1] - STREAM
[Product 3, Quantity : 1] - STREAM
[Product 2, Quantity : 2] - STREAM

STREAM CAN PRODUCE A TABLE
A TABLE CHANGE LONG CAN PRODUCE A STREAM

KAFKA STREAM START FROM A TOPIC [Consumer]
Add Operations/Filter, Cleaning, Enriching etc [Topology]
KAFKA STREAM OUTPUT TO Another Topic [Producer]


WINDOW

    - Start-End time (between start and end is called window)
    
    1:00 PM
        1:01
        1:02
        1:03
        1:04
    1:05 PM
            1:06
            1:07
            1:08
            1:09
    1:10 PM
    1:15 PM
    1:20 PM
    1:25 PM

TUMBLING: fixed-sized, non-overlapping windows based on the records' timestamps

1:00 PM,1:01, 1:02, 1:03, 1:04, 1:05 PM - 5 mins
  Start                          End
  

1:05 PM,1:06, 1:07, 1:08, 1:09, 1:10 PM - 5 mins
  Start                          End
    
    Give me data for between 1:00 PM to 1;05
    
HOPPING: fixed-sized, (possibly) overlapping windows based on the records' timestamps



1:08 - Give me data for last 5 minutes    

1:03 PM,1:04, 1:05, 1:06, 1:07, 1:08 PM - last 5 mins
  Start                          End
  
  
1:09 - Give me data for last 5 minutes    

1:04 PM,1:05, 1:06, 1:07, 1:08, 1:09 PM - last 5 mins
  Start                          End
  

SESSION: sessions with a period of activity and gaps in between 

User 1 - Person 1 - Shopping online

8:00 AM - session begin
  8:01 AM
  8:02 AM
  8:02 AM
  8:02 AM
  8:05 AM
  ...
  8:20 AM - Session over
  8:21 AM - Start new Session
  ..
  ..
  8:40 - Session end
  
  
  20 minutes as user session
  Capture activities, interests etc for 20 minutes interval
  
  
  
User 2 - Person 2 - Shopping online

8:10 AM - session begin
  8:01 AM
  8:02 AM

  8:30 AM - Session over
 
 
 ----
 
 
 
 # KSQL BASICS
 
 confluent start
 
 confluent stop
 
 login into one shell
 
 ksql-datagen quickstart=users format=avro topic=users maxInterval=5000
 
 login into second shell 
 
 ksql-datagen quickstart=pageviews format=avro topic=pageviews maxInterval=5000
 
 
 login into shell 
 
 SHOW STREAMS;
 SHOW TABLES;
 SHOW TOPICS;
 SHOW QUERIES;
 
 
 
 users topic
 	userid
     regionid
     gender
 
 pageviews topic
 
   userid
   pageid
   
   
  CREATE STREAM users_stream(userid varchar, regionid varchar, gender varchar) \ 
                WITH (kafka_topic='users', value_format='AVRO');
 
 SHOW STREAMS;
 SHOW TOPICS;
 
 QUERY - Non Persistent Query
 	to Stop, Ctrl + C
     temporary, not shall persist the result into topics
     
 select * from users_stream;
 
 select userid, gender from users_stream;
 
 
 LIMIT max number to get
 
 select userid, gender from users_stream LIMIT 5;
 
 
 select userid, gender from users_stream where gender='FEMALE' LIMIT 5;
 
 ----
 
 # PERSISTENT QUERY
 
 
 It runs like other query, but store the results into another kafka topic
 To Stop the query, TERMINATE command
 
 CREATE STREAM users_female AS \
  select userid, regionid, gender from users_stream where gender='FEMALE';

 
 

 SHOW STREAMS;
SHOW TOPICS;

you can see USERS_FEMALE as topic



 CREATE STREAM pageviews_stream(userid varchar, pageid varchar) \ 
               WITH (kafka_topic='pageviews', value_format='AVRO');

SHOW STREAMS;


users_stream (userid, regionid, gender)
pageviews_stream (userid, pageid)

Join users_stream & pageviews_stream on users_stream.userid == pageviews_stream.userid

Join Output 

userid, pageid,  regionid, gender

enriching data. 
Persisted query, output is written to a topic

CREATE STREAM users_pageviews_stream AS \
	SELECT users_stream.userid AS userid, pageid, regionid, gender \
	FROM pageviews_stream \
    LEFT JOIN users_stream \
    WITHIN 1 HOURS \
    ON users_stream.userid = pageviews_stream.userid;

select * from users_pageviews_stream;

CREATE TABLE users_pagevies_by_region_gender \
	 WITH (value_format='AVRO') AS \ 
     SELECT gender, regionid, COUNT() AS numusers \
     FROM users_pageviews_stream \
     WINDOW TUMBLING (SIZE 30 SECOND) \
     GROUP BY gender, regionid \
     HAVING COUNT() >= 1;


select * from users_pagevies_by_region_gender;

select gender, regionid, numusers from users_pagevies_by_region_gender;

SHOW TABLES;

SHOW QUERIES;

EXPLAIN CSAS_USERS_FEMALE_0;
TERMINATE CSAS_USERS_FEMALE_0;

# CONNECT BASICS

# KAFKA CONNECT

confluent list connectors

confluent status connectors

File source/sink connectors

> mkdir krish
> cd krish

/root/krish>touch krish-file-source.properties
/root/krish>touch krish-file-sink.properties
/root/krish>touch input-file.txt
/root/krish>touch output-file.txt

 
/root/krish>nano krish-file-source.properties

paste below content into file

name=krish-file-source
connector.class=FileStreamSource
tasks.max=1
file=/root/krish/input-file.txt
topic=krish-file-content


Ctrl + O to write the file
Ctrl + X to exit

cat krish-file-source.properties


/root/krish>nano krish-file-sink.properties

paste below content

name=krish-file-sink
connector.class=FileStreamSink
tasks.max=1
file=/root/krish/output-file.txt
topics=krish-file-content


Ctrl + O to write the file
Ctrl + X to exit

cat krish-file-sink.properties


--

# Load connectors


/root/krish> confluent load krish-file-source -d krish-file-source.properties


/root/krish> confluent load krish-file-sink -d krish-file-sink.properties


confluent status connectors


confluent unload krish-file-source 
confluent unload krish-file-sink 

confluent status connectors


confluent status krish-file-source

confluent status krish-file-sink


echo "line 1" >> input-file.txt

Kafka source connector will notice file change, read the line 1 text, publish to kafka topic

kafka sink connector, will notice new message in the topic, 
  take the message and write to output-file.txt
  
  
  kafka-console-consumer --bootstrap-server localhost:9092 --topic krish-file-content --from-beginning







