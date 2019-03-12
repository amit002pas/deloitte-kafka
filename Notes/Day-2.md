

Producer(s) 10   ===> Broker  [1 Brokers]  ===> Consumer(s) 1

100 msg per second per producer
1000 msg per second by all - under a TOPIC, further down in PARTITION(s)

TOPIC 
    PARITITION P0 []
    PARITITION P1 []
    PARITITION P2 []

1 consumer can handle 200 msg per second
5 consumer(s) can handle 1000 msg per second

consumer is a JVM process - 5 machines 
start consumer process in 5 machines/5 instances (1000 msg per second)

Consumer Group - Unique ID to group the consumer process(es), same/different machines

Invoice Group: Process the message, generate invoice
    Machine 1 - Invoice Consumer 1   [ConsumerGroup - invoice-group]
    Machine 2 - Invoice Consumer 2  [ConsumerGroup - invoice-group]
    Machine 3 - Invoice Consumer 3   [ConsumerGroup - invoice-group]
    Machine 4 - Invoice Consumer 4   [ConsumerGroup - invoice-group]
    Machine 5 - Invoice Consumer 5    [ConsumerGroup - invoice-group]

Email: Process the message, send email to customer

    Machine 1 - Email Consumer 1   [ConsumerGroup - email-group] - 350 msg per second
    Machine 2 - Email Consumer 2  [ConsumerGroup - email-group] - 350 msg per second
    Machine 3 - Email Consumer 3   [ConsumerGroup - email-group] - 350 msg per second


    BAD**

        Machine 1 - Invoice Consumer 1   [ConsumerGroup - common-group]
         Machine 2 - Email Consumer 1   [ConsumerGroup - common-group] - 350 msg per second





TOPIC 
    PARITITION P0 []
    PARITITION P1 []
    PARITITION P2 []

Invoice Group: Process the message, generate invoice
    Machine 1 - Invoice Consumer 1   [ConsumerGroup - invoice-group] - 200 msg per second

    1 consumer reads data from multiple partitions
        consumer 1 read data from partition 0, reads few records
        consumer 1 read data from partition 1, reads few records
        consumer 1 read data from partition 2, reads few records


 Machine 1 - Invoice Consumer 1   [ConsumerGroup - invoice-group] - 200 msg per second
 Machine 2 - Invoice Consumer 2   [ConsumerGroup - invoice-group] - 200 msg per second

    2 consumer reads data from multiple partitions (3)
        consumer 1 read data from partition 0, & partition 1 reads few records
            consumer 1 read data from partition 0, reads few records
            consumer 1 read data from partition 1, reads few records

        consumer 2 read data from partition 2, reads few records


 Machine 1 - Invoice Consumer 1   [ConsumerGroup - invoice-group] - 200 msg per second
 Machine 2 - Invoice Consumer 2   [ConsumerGroup - invoice-group] - 200 msg per second
 Machine 3 - Invoice Consumer 3   [ConsumerGroup - invoice-group] - 200 msg per second

    3 consumer reads data from multiple partitions
        consumer 1 read data from partition 0  reads few records
        consumer 2 read data from partition 1  reads few records
        consumer 3 read data from partition 2 reads few records

 Machine 1 - Invoice Consumer 1   [ConsumerGroup - invoice-group] - 200 msg per second
 Machine 2 - Invoice Consumer 2   [ConsumerGroup - invoice-group] - 200 msg per second
 Machine 3 - Invoice Consumer 3   [ConsumerGroup - invoice-group] - 200 msg per second
 Machine 4 - Invoice Consumer 4   [ConsumerGroup - invoice-group] - 0 msg per second [IDLE]
 Machine 5 - Invoice Consumer 5   [ConsumerGroup - invoice-group] - 0 msg per second [IDLE]

    4 consumer reads data from multiple partitions (3)
        consumer 1 read data from partition 0  reads few records
        consumer 2 read data from partition 1  reads few records
        consumer 3 read data from partition 2 reads few records
        consumer 4? IDLE, waste of resource


OFFSET?
    -- Location of the msg within partition
    -- Index number
    -- Unique within Partition
    -- NOT Unique within TOPIC

P0 -  MSG DATA [MSG0, MSG2, MSG4,....]
      Offsets  [0,    1,    2, 3, 4, .....]

P1 - MSG DATA  [MSG1, MSG3, MSG5,....]
      Offsets  [0,    1,    2, 3, 4, .....]

_consumer__offset - Kafka Internal topic
    -- Consumer Group - 1 or more consumer(s) - unique id for consumer group

    Per
        Partition
        Consumer Group

    P0 -  MSG DATA [MSG0, MSG2, MSG4,....]
        Offsets  [0,    1,    2, 3, 4, .....]

    P1 - MSG DATA  [MSG1, MSG3, MSG5,....]
        Offsets  [0,    1,    2, 3, 4, .....]


    ConsumerGroupOffsets

        invoice-group
            P0 - Current Offset [2]
            P1- Current Offset [2]

    Start consumer process that consume msg from partition(s)

         Consumer read from partition 0 [Offset 0 MSG0]
         Consumer commit the offset [Ack]

         Consumer read from partition 1 [Offset 0 MSG1]
         Consumer commit the offset 0 [Ack]

         Consumer read from partition 0 [Offset 1 MSG2]
         Consumer commit the offset 1 [Ack]

         Consumer read from partition 1 [Offset 1 MSG3]
         Consumer commit the offset 1 [Ack]


Read Message from broker 
Commit the offset [auto-commit]
Process the message here [Exception]


Read Message from broker 
Process the message here [Exception]
Commit the offset [Manual commit],

1000 msg per second [4 brokers]
    Broker ID - 0  - 250 msg per second
    Broker ID - 1  - 250 msg per second
    Broker ID - 2  - 250 msg per second
    Broker ID - 3  - 250 msg per second

Partitions 
    P0 - LEADER Broker 0
    P1 - LEADER Broker 1
    P2 - LEADER Broker 2

How Broker store messages?
    Topic
        Paritition

Topology
    -- Leader
        -- Accept the write request
        -- Write the data on its own partition
        -- Read data from partition
    -- Followers
    -- Leader is a partition leader, NOT A TOPIC LEADER

LEADER IN GENERAL CONCEPT

    Broker 0  [1 Broker]
        Topic 1
            P0 - LEADER Broker 0
            P1 - LEADER Broker 0
            P2 - LEADER Broker 0

    Broker 0, Broker 1  [2 Broker(s)]
        Topic 1
            P0 - LEADER Broker 0
            P1 - LEADER Broker 0
            P2 - LEADER Broker 1

    Broker 0, Broker 1, Broker 2  [3 Broker(s)]
        Topic 1
            P0 - LEADER Broker 0
            P1 - LEADER Broker 1
            P2 - LEADER Broker 2

    Broker 0, Broker 1, Broker 2, Broker 3  [4 Broker(s)]
        Topic 1
            P0 - LEADER Broker 0
            P1 - LEADER Broker 1
            P2 - LEADER Broker 2


REPLICAS
    REPLICAS should less or equal to number of brokers

    Broker 0  [1 Broker] - Max Replicas can be 1.
        Topic 1
            P0 - LEADER Broker 0
            P1 - LEADER Broker 0
            P2 - LEADER Broker 0

    Broker 0, Broker 1  [2 Broker(s)] - Max Replicas can be 2
        Topic 1
            P0 - LEADER Broker 0
            P1 - FOLLOWER Broker 0
            P2 - LEADER Broker 0

        Topic 1 [2nd copy of the partition data]
            P0 - FOLLOWERS/REPLICA Broker 1
            P1 - LEADER Broker 1
            P2 - FOLLOWERS/REPLICA Broker 1


    Broker 0, Broker 1, Broker 2  [3 Broker(s)], 3 Replicas
        Topic 1 [Broker 0]
            P0 - LEADER Broker 0 [take write,reads]
            P1 - FOLLOWER 
            P2 - FOLLOWER

        Topic 1 [Broker 1]
            P0 - FOLLOWER
            P1 - LEADER 
            P2 - FOLLOWER

        Topic 1 [Broker 2]
            P0 - FOLLOWER
            P1 - FOLLOWER
            P2 - LEADER

    Broker 0, Broker 1, Broker 2,  Broker 3, Broker 4,   [5 Broker(s)], 3/5 replicas
          Topic 1 [Broker 0]
            P0 - LEADER Broker 0 [take write,reads]
            P2 - FOLLOWER

         Topic 1 [Broker 1]
            P1 - LEADER 

         Topic 1 [Broker 2]
            P0 - FOLLOWER
            P1 - FOLLOWER
            P2 - LEADER
    
         Topic 1 [Broker 3]
            P0 - FOLLOWER
            P1 - FOLLOWER

         Topic 1 [Broker 4]
            P2 - FOLLOWER


Producer 
    Send MSG With Ack level  - 0
        LEAD Broker Accept the message
        Before writing into Disk, send Ack back to producer

        Cons:
            Broker may have failed after sending ack, before writing to disk

        Pros:
            Fast

    Send MSG With Ack level  - 1
        LEAD Broker Accept the message
        Writing into its own Disk
        Send Ack back to producer, before REPLICAS updated

        Cons:
            Broker crashed, then is not yet replicated

        Pros:
            Medium Fast

    
    Send MSG With Ack level  - all
        LEAD Broker Accept the message
        Writing into its own Disk
        REPLICAS updated [Replicas committed the message in their HDD]
        Send Ack back to producer

        Cons:
            Slow

        Pros:
            Message is safe, no message loss


-------

class OrderConfirmation {
    id
    name
    etc
}

order = new OrderConfirmation()
order.id = 1;
name ==fdasfas;

Producer 

    Send the order to broker
    We cannot sent object it as.
    We need serialize object into bytes . Serializer object
    JSON  {
        "id": 1,
        "name": "test"
    }

    Key/Value are bytes array

Consumer
    Consumer gets key/value in bytes array
    Expecting is, Order Object
    Deserialize bytes into Order Object
    order.id == 1
    order.name == "test"

----

JSON  Text format PAYLOAD
{
    "orderId": 12344567890,
    "amount" : 12343,
    "customerId" : 432423432432,
    "timestamp" : 432142314324344
}

120 chars - Java is unicode. it will be 240 bytes

Data Model - Schema
{
    "orderId": Int, -- 4 bytes
    "amount" : Float, -- 4 bytes
    "customerId" : Int, -- 4 bytes
    "timestamp" : Long -- 8 bytes
}

To Binary Format as PAYLOAD {
    Version: 0/1/2 - 4 bytes
    4 + 4 + 4 + 8 Bytes = 20 bytes + 4 bytes
} = 24 bytes + extra  (8 times less than JSON PAYLOAD)

PAYLOAD  [Network IO, Disk IO ], MORE PAYLOAD, MORE TIME, SPACE taken
    the data send to Broker from producer
    The data stored in the broker in the HDD, save space if less bytes
    The data replicated amoung multiple brokers
    the data given to consumer from broker


## Avro

https://avro.apache.org/docs/1.8.1/spec.html

{
  "type": "record",
  "name": "Person",
  "fields" : [
    {"name": "fullName", "type": "string"},             // each element has a long
    {"name": "gender", "type": ["null", "string"]}, // optional or string
    {"name": "age", "type": ["int"]} // optional or string

{"type": "array", "items": "int"}

  ]
}
	
    How to generate POJO out of schema? Avro tools for MVN.

person = new Person();
person.setFirstName("Krish");

--

http://localhost:8091/subjects

topicname-key [key schema]
topicname-value [value schema]

try in the browser

http://localhost:8091/subjects

http://localhost:8091/subjects/invoices-key/versions
http://localhost:8091/subjects/invoices-value/versions

http://localhost:8091/subjects/invoices-key/versions/1
http://localhost:8091/subjects/invoices-value/versions/1

http://localhost:8091/schemas/ids/42



50 Brokers

Broker ID 0 - /tmp/kafka-logs
Broker ID 1  - /tmp/kafka-logs-1
Broker ID 2 - /tmp/kafka-logs-2
Broker ID 3 - /tmp/kafka-logs-3

kafka-topics --zookeeper localhost:2181 --create --topic messages --replication-factor 3 --partitions 3

