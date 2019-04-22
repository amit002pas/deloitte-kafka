Topic 1

Logical 

Topic 1 [messages]  => [M0, M1, M2, M3, ...M99] - 100 msgs

What is partition? Sub set of topic 1/messages data

Example 1: Total Partitions - 1
Partition 0 [P0] - [M0, M1, M2, M3, ...M99] - 100 msgs


Example 2: Total Partitions - 2 [Odd/Even]
Partition 0 [P0] - [M0, M2, M4, M6, ...M98] - 50 msgs
Partition 1 [P1] - [M1, M3, M5, ..M99 ] - 50 msgs

Example 3: Total Partitions - 4 [Mn % 4 = partition no]
Partition 0 [P0] -  [M0, M4, M8, M12, ... M96] - 25 msg
Partition 1 [P1] -  [M1, M5, M9, M13....] - 25
Partition 2 [P2] -  [M2, M6, ...] - 25 Msg
Partition 3 [P3] -   [M3, M7, M11....] - 25 Msg

Example 4 Order by Country - 3 Partitions using Country code
Partition 0 [P0] -  [O-USA-1, O-USA-2..........] - n msgs
Partition 1 [P1] -  [O-IN-111, O-IN-222, .....] - m msgs
Partition 2 [P2] -  [0-UK-100, ] - o msgs
Partition 3 [P3] -  [ ] - Srilanka

Msg are stored into Topic
Msg consists of Key and Value, but stored as Bytes [Serialized format]
Msgs are split into Partitions
Key/value can be any type, key also can be null
    Use of Key? Key is useful to decide partitions
    If Key is null? - if more partitions found? 
                    round robin approach used
                    [P0, P1, P2, P3, P0, P1, P2.....]
    If key[bytes] is not null?
                Take hashcode from key [bytes]
                hashcode % number of partitions
                key is 114 % 4 = P 2
                key is 112 % 4 = P 0

    Custom Logic partitioners [custom Java code]
        if country code is IN, then P1
        if country code is USA, then P0

Topic  1
    Breaks into Partitions [2]
Topic  2
    Breaks into Partitions [20 partitions]
Topic  3 [ 1 parition ]
    Breaks into Partitions

Who decides partitions?
    Producer is the one decide the partitions

How producer works.
    1. Kafka producer connect to broker(s)
    2. Kafka producer reads meta data about topic, available brokers Topic [P0, P1, P2, P3..]

    3. Producer Send Msg0, producer to decide partition
        send msg0, partition 0

    4. Producer Send Msg1, producer to decide partition
        send msg1, partition 1

    5. Producer Send Msg2, producer to decide partition
        send msg2, partition 2

    6. Producer Send Msg3, producer to decide partition
        send msg3, partition 3

    6. Producer Send Msg4, producer to decide partition
        send msg4, partition 0 [round robin]

DAY 1: Part 1 - Setup 



Java JDK 1.8 - Yes
       https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html

IntelliJ Community - https://www.jetbrains.com/idea/download/

Confluent 5.1.2 - https://www.confluent.io/download_2

https://gitforwindows.org/

Putty - SSH [Tomorrow]



Part 1: JAVA Setup

C:\Program Files\Java\jdk1.8.0_191

Environment Variables

PATH             C:\Program Files\Java\jdk1.8.0_191\bin

JAVA_HOME          C:\Program Files\Java\jdk1.8.0_191

Click on Start menu
Search for Environ

   Choose environment for your account
   
Part 2

   Download nad Extract Kafka, move to C:\confluent-5.1.2
   
   Environment Variable
   
   ENSURE NO SPACE IN THE PATH
   
   KAFKA_HOME   C:\confluent-5.1.2
   
   
   Add to PATH  %KAFKA_HOME%\bin\windows
   
   
Kafka Day 1- Part 2: ZooKeeper & Kafka Broker

ZooKeeper [Must for Kafka]
    -- is a distributed configuration management server
    -- is a Apache Project, Hadoop, used by many other systems
    -- where it is used? 
            Topics, partitions configuration stored here
            All the brokers list
            Schema registry - Schema details
            Leader Election

    -- When a broker starts the application, it register with zookeeper
    -- Heartbeat with brokers, to know status/health of broker

## kafka-server - Broker

    - take message from publisher
    - store the message into partitions
    - give message to consumer

    - Broker will not have any topic meta data
    - Topic meta data taken from zookeeper
    - Heartbeat with zookeeper

## Commands to start


Start Zookeeper

open command prompt

zookeeper-server-start %KAFKA_HOME%\etc\kafka\zookeeper.properties

Start Kakfa-server ie Broker - single broker - ID 0

Open Second command prompt

kafka-server-start %KAFKA_HOME%\etc\kafka\server.properties

----

## Kafka Topics - Admin cli/tool to manage the topics

-- create/alter/delete topics
-- list topics

-- kafka-topics command

Open third command prompt

kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic test
 
kafka-topics --list --zookeeper localhost:2181


-- Kafka Console producer

Open forth command prompt window

kafka-console-producer --broker-list localhost:9092 --topic test


Open fifth command prompt window 

kafka-console-consumer --bootstrap-server localhost:9092 --topic test


kafka-console-consumer --bootstrap-server localhost:9092 --topic test --from-beginning




# KAFKA - Day 1 - Second Half

Topic: Test
Example 1: Total Partitions - 1
Partition 0 [P0] - [M0, M1, M2, M3 ]
                                  ^
                    offset[]

Offset: number, location of the message
Offset: 0

consumer start default [from latest offset]: start with offset 3

consumer start  [from beginning offset - 0]: start with offset 0

consumer start  [particular partition-1, offset - 2]: start with offset 2 on given partition



kafka-console-consumer --bootstrap-server localhost:9092 --topic test --partition 0 --from-beginning


kafka-console-consumer --bootstrap-server localhost:9092 --topic test --partition 0 --offset 3


-- Create topic with multiple partitions

kafka-topics --zookeeper localhost:2181 --create --topic greetings --replication-factor 1 --partitions 3

----
Internal Architecture

Partition file : greeting-0/0000000000.log (append only file)
                 append message, added at end of the file


Topic 1
P0
   [MSG-1-old, .... MSG-1-edited]

Consumer 1
    MSG-1-old - Read and Ignore, not to process
    ..
    ..
    MSG-1-edited

    Redis/mongodb,.etc, 
        ignore msg list [MSG-1]


E-commerce

100 countries

100 parititions, 1 partition per country
IN - 0
USA - 1
...

100 countries
4 paritions

P0 - IN, USA, EUROPE,..
P1: SG, SL,....

100 paritions
P0 - 50%
p1 - 20%
p2-P60 - 20%
P61 - P100 - 0%


----


kafka-topics --zookeeper localhost:2181 --create --topic greetings --replication-factor 1 --partitions 3

kafka-topics --list --zookeeper localhost:2181



kafka-topics --describe --zookeeper localhost:2181 --topic greetings


kafka-console-producer --broker-list localhost:9092 --topic greetings --property "parse.key=true" --property "key.separator=:"


kafka-console-consumer --bootstrap-server localhost:9092 --topic greetings

kafka-console-consumer --bootstrap-server localhost:9092 --topic greetings --from-beginning

kafka-console-consumer --bootstrap-server localhost:9092 --topic greetings  --from-beginning --property print.key=true --property print.timestamp=true

---
Consumer read the message, then processed
Consumer read the message, then failed to process msg

--


// whatever first condition reached, 
// group messages by max byte size, 16 KB, dispatch when it reaches 16 KB
props.put(BATCH_SIZE_CONFIG, 16000); // bytes

// group the messages by max wait time, when 100 ms reached, dispatch the message
props.put(LINGER_MS_CONFIG, 100); // milli second

// Reserved memory, pre-alloted in bytes
props.put(BUFFER_MEMORY_CONFIG, 33554432);

Producer                  Broker

100 msg per 100 ms, 100 req per 100 ms 

M1      =====>             M1
M2      =====>             M2
M3      =====>             M3
..
M100      =====>           M100

How to optimize the request?
props.put(LINGER_MS_CONFIG, 100); // 100 ms

M1, producer will wait till 100 ms if any more msg to send
m2.....m100

Producer group all 100 msgs into one single request

compress, optimize, hand-shaking time

[M1, M2, M3....]          =========>     [M1, M2, M3...]


# With Consumer Group



 
kafka-console-consumer --bootstrap-server localhost:9092 --topic messages --from-beginning --property print.key=true --property print.timestamp=true   --consumer-property group.id=test1


kafka-console-consumer --bootstrap-server localhost:9092 --topic messages --from-beginning --property print.key=true --property print.timestamp=true   --consumer-property group.id=test1




