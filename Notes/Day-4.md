Setup the Cluster

1. More than 1 server(s)
2. DNS - Domain Naming System. domain name => IP Address, domain name should be discoverable


zookeeper-shell k1.nodesense.ai:2181

ls /brokers/ids

get /brokers/ids/0
get /brokers/ids/1
get /brokers/ids/2
get /brokers/ids/3

ls /brokers/topics

// modify broker.id and zookeeper properites

---

Update Kafka Cluster

cd /root/confluent-5.1.2/etc/schema-registry
nano /root/confluent-5.1.2/etc/schema-registry/schema-registry.properties

kafkastore.connection.url=k1.nodesense.ai:2181



# update kafka-rest

nano /root/confluent-5.1.2/etc/kafka-rest/kafka-rest.properties

schema.registry.url=http://k1.nodesense.ai:8081
zookeeper.connect=k1.nodesense.ai:2181
bootstrap.servers=PLAINTEXT://k1.nodesense.ai:9092



# Connect

nano /root/confluent-5.1.2/etc/kafka/connect-standalone.properties

bootstrap.servers=k1.nodesense.ai:9092

nano /root/confluent-5.1.2/etc/kafka/connect-distributed.properties

bootstrap.servers=k1.nodesense.ai:9092



confluent stop
confluent start


----


cd <<yourname>>
  
cd krish

touch krish-mysql-product-source.json
touch krish-mysql-product-sink.properties


nano krish-mysql-product-source.json

paste below 

{
  "name": "krish-mysql-product-source",
  "config": {
    "connector.class": "io.confluent.connect.jdbc.JdbcSourceConnector",
    "key.converter": "io.confluent.connect.avro.AvroConverter",
    "key.converter.schema.registry.url": "http://k1.nodesense.ai:8081",
    "value.converter": "io.confluent.connect.avro.AvroConverter",
    "value.converter.schema.registry.url": "http://k1.nodesense.ai:8081",
    "connection.url": "jdbc:mysql://k2.nodesense.ai:3306/krish?user=team&password=team1234",
    "_comment": "Which table(s) to include",
    "table.whitelist": "products",
    "mode": "timestamp",
     "timestamp.column.name": "update_ts",
    "validate.non.null": "false",
    "_comment": "The Kafka topic will be made up of this prefix, plus the table name  ",
    "topic.prefix": "db-"
  }
}



confluent load krish-mysql-product-source -d krish-mysql-product-source.json

confluent status krish-mysql-product-source
 


kafka-avro-console-consumer --bootstrap-server k1.nodesense.ai:9092 --topic db-products  --from-beginning


Topics:
    products
    db-products
    
3rd party system:
    publish the data into products topic [avro-producer]
    
    Kafka Connect Sink
        Sink read the data from products topic
        insert into products table
        
    Kafka Connect Source
        Observe products table insert/update changes
        when data inserted/updated, pull the updated one,
            publish to topic db-products
            
    In avro consumer, we listen for db-products
    
    ----
    
    
    Sink
    
    nano krish-mysql-product-sink.properties
    
    
    name=krish-mysql-product-sink
    connector.class=io.confluent.connect.jdbc.JdbcSinkConnector
    tasks.max=1
    topics=products
    connection.url=jdbc:mysql://k4.nodesense.ai:3306/krish?user=team&password=team1234
    auto.create=true
    key.converter=io.confluent.connect.avro.AvroConverter
    key.converter.schema.registry.url=http://k1.nodesense.ai:8081
    value.converter=io.confluent.connect.avro.AvroConverter
    value.converter.schema.registry.url=http://k1.nodesense.ai:8081
    
    
    confluent load krish-mysql-product-sink -d krish-mysql-product-sink.properties
    
    confluent status krish-mysql-product-sink
    
    
    
    
    kafka-avro-console-producer --broker-list k1.nodesense.ai:9092 --topic products --property value.schema='{"type":"record","name":"product","fields":[{"name":"id","type":"int"},{"name":"name", "type": "string"}, {"name":"price", "type": "int"}]}'
    
    
    {"id": 999, "name": "from avro pro", "price": 1111}
    
    
    {"id": 1999, "name": "from avro pro 2", "price": 2222}
    
    
    kafka-avro-console-consumer --bootstrap-server k1.nodesense.ai:9092 --topic db-products  --from-beginning
    
    
    # KAFKA REST Proxy
    
    https://github.com/confluentinc/kafka-rest
    
    Browser - No Kafka protocol
    iot - 2-10 KB, small mcu, not much to handle kafka native protocols
     HTTP, REST , JSON
    
    
    HTTP Client      =>    KAFKA REST PROXY  ==> KAFKA CLUSTER
    
    HTTP Client  =>    KAFKA REST PROXY (HTTP/REST/GET/POST/PUT/DELETE)
    C/C++/node.js
    js/python/lua
    
    KAFKA REST PROXY           ==> KAFKA CLUSTER (Consumer/Producer SDK)
    JAVA Application       Java
    (Consumer/Producer SDK)
    
    POST a message to kafka cluster, Producer
    GET a message from kafka cluster, Consumer
    Consumer Group? handled by Kafka REST Proxy
    
   curl "http://k1.nodesense.ai:8082/topics"
         
    
       # Get info about one topic
   curl "http://localhost:8082/topics/jsontest"
   
   
   
   
   
   curl "http://k1.nodesense.ai:8082/topics"
         
    # Get info about one topic
   curl "http://k1.nodesense.ai:8082/topics/greetings"
   
      curl "http://k1.nodesense.ai:8082/topics/test"
      
      
      kafka-console-consumer --bootstrap-server k1.nodesense.ai:9092 --topic greetings --from-beginning


      
      curl -X POST -H "Content-Type: application/vnd.kafka.json.v2+json" \
          --data '{"records":[{"value": "Hello from curl" }]}' \
          "http://k1.nodesense.ai:8082/topics/greetings"
          
          
          
    Consumer
    
    Web -> we have many active application, within application, active users
    
    curl -X POST -H "Content-Type: application/vnd.kafka.v2+json" -H "Accept: application/vnd.kafka.v2+json" \
    --data '{"name": "inv-terminal-1", "format": "json", "auto.offset.reset": "earliest"}' \
    http://k1.nodesense.ai:8082/consumers/invoices


get response like

{"instance_id":"inv-terminal-1",
  "base_uri":"http://k1.nodesense.ai:8082/consumers/invoices/instances/inv-terminal-1"}
%                 
  
  --
Create Subscription againt instance id

  # Subscribe the consumer to a topic
    
 curl -X POST -H "Content-Type: application/vnd.kafka.v2+json" --data '{"topics":["greetings"]}' \
    http://k1.nodesense.ai:8082/consumers/invoices/instances/inv-terminal-1/subscription
    

Get data from REST Proxy using susbcription

 curl -X GET -H "Accept: application/vnd.kafka.json.v2+json" \
    http://k1.nodesense.ai:8082/consumers/invoices/instances/inv-terminal-1/records


curl -X GET -H "Accept: application/vnd.kafka.binary.v1+json" \
  http://k1.nodesense.ai:8082/consumers/invoices/instances/inv-terminal-1/records
 
 ----
 
 Q/A
 
 can you share a prod instance where kafka will fit and a general architecture?
 
 KAFKA STREAM/KSQL
             Source ? Kafka Topic
             Process
             Sink ? Kafka Topic
 
 KAFKA CONNECT SOURCE
             Source/Input? File, MySQL, Mongod, HDFS, MQTT
             Will not process/ No Business Logic
             Read from source, publish to Topic
             Output: Kafka Topic
 
 KAFKA CONNECT SINK
             Source/Input: Kafka Topic
             Will not process/ No Business Logic
             Output: File, SQL, HDFS, S3...
 



Machine Learning
    Spark MLib/DF/DS

Click Pages on internet
    Spring MVC app
            Capture the event
                Stream to Kafka 
                    Kafka store to file
                        From kafka, stream to spark 
 
                What if spark cluster fails in between?

Spark - no storage, in memory computation

Store the data into kafka/topics
  Kafka Stream
    Pull data into kafka stream
        send to spark (write to topic)
            spark apply machine learning, heafty algorithms
                write the result back to kafka topic 

    take results from spark output topic, 
        process again/forward to mysql/etc
        
        
 Legacy 
     XML - 10 KB
     CSV
     JSON
 
 <person_name>Krish</person_name> 32 chars x 2 bytes = 64 bytes
 
 Topic that accept XML - Give retention period very short 12 hours
 
 Run Kafka Stream, run 24/7 
   That accept message from XML Topics [10 KB]
   Conver to Json
   Write to Avro Topics [500 bytes]
 
   Avro Topics - retention period can be higher 1 month
 
 Kafka
     Optimal size
     Avro - 500 bytes