# Broker Roles & Responsiblities 
    - Leader partition
    - Persist the data
      - log files
      - <<topic>>-<<partition>>
      - .log - the data stored as bytes
      - .index - offset numbers to byte position in the .log file
      - .timeindex - timestamp to offset
    - can you delete data in log file? NO [immutable]
    - can you edit the data in log file? NO [immutable]
    - can you query/sort the data? NO
    
    - serve data to consumer
    - from consumer, is pull, consumer pull from broker
    - consumer_offset, maintained agaist consumer group offset, partition
    
    - heart beat with zookeeper
    
    - unique id broker.id
    - log directory, where all the files stored
    - broker runs as deamon process, server process on a port number 9092
    
    - replicas
    
    - LEAD/Replicas[Follows]
    - JVM
    
    
# Producer
    - the one publish the data to broker
    - serialize the data [key/value] into bytes format
    - decides the partition
        - if key is null, round robin basics
        - if key is not null, [bytes], uses hash code % number of parititions
        - custom partition
        
    - handshake with broker(s)
    - Ack level setting 
        all - all replicas should be updated
        0 - taken by broker, but not written to disk
        1 - taken by broker, written to disk, replicas not updated
        
    - producers uses multiple threads/worker thread
    - jvm/python/.net/go
    
# Consumer
    - consumer is jvm/python/.net/go
    - deserialize the data into object
    - consumer group 
        - multiple consumer instances can consume data parallelly
        - one consumer instance per partition
        - one consumer instance can take data from multiple partitions
        - multiple consumer instances CANNOT read data from SAME Partition
        
    - commit offset
        - auto commit [configuration]
            - as soon as, data is fetched by the consumer
            - risk is data is not yet processed
        - manual commit [commitSync, commitAsync]
            - get the data
            - process the data
            - commit the offset
            
            
# Zookeeper
    - co-ordinator in the distributed computing
    - configuration management 
    - leader election [partition leader]
    - heart beat with all brokers
    - Topics Meta maintained here [leader/followers/in-sync/replicas]
    - Schemas are stored here too
    - Multiple instances of zookeeper to be run
    - Leader and follower
    
# Broker list vs bootstrap servers

Bootstrap servers
    Broker-0 192.168.1.2:9092
    Broker-1 192.168.1.3:9093
    Broker-2 192.168.1.4:9094
    Broker-3 192.168.1.5:9095
    
    Some more brokers [guest/on-demand]
    Broker-4
    Broker-5
    
    Remove brokers [guest/on-demand]
    Broker-4
    Broker-5
            
            
    kafka-console-producer --broker-list localhost:9095 --topic test
    broker-3 is not maintaining test/paritions/not leader
    -- works, internally broker-3 shall forward request to broker-0/broker-1/broker-2
    -- take the ack from the respective broker, send ack back to client
    
    -- bootstrap servers means, list of servers always running
    -- bootstrap acts a meta data server on behalve of zookeeper
        -- discover all the other topic data/other brokers involved
    
    
