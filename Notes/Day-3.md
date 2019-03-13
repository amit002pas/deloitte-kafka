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