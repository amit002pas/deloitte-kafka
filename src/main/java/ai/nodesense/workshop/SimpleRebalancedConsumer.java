//SimpleRebalancedConsumer.java
package ai.nodesense.workshop;

import org.apache.avro.reflect.MapEntry;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

import static org.apache.kafka.clients.consumer.ConsumerConfig.*;

public class SimpleRebalancedConsumer {
    public static String BOOTSTRAP_SERVERS = "localhost:9092";
    public static String TOPIC = "greetings";

    public static void main(String[] args) throws Exception {
        Properties props = new Properties();
        props.put(BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);

        props.put(GROUP_ID_CONFIG, "greeting-consumer-rebalance"); // offset, etc, TODO

        props.put(ENABLE_AUTO_COMMIT_CONFIG, "false");
        props.put(AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");

        props.put(SESSION_TIMEOUT_MS_CONFIG, "30000");

        props.put(KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");

        // <Key as string, Value as string>
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);

        // when a partition is assigned to a consumer instance,
        // own logic, own db
        // 1. read from beginning
        // 2. read from end
        // 3. read from particular offset
        // 4. read from last commited offset [default]

        String position = "offset"; // offset | begin | end | lastCommitted

        // subscribe from one or more topics
        consumer.subscribe(Collections.singletonList(TOPIC),
                new ConsumerRebalanceListener() {
                    @Override
                    public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
                        // called when a partition is pulled out of this consumer instance
                        System.out.printf("%s topic-partitions are revoked from this consumer\n", Arrays.toString(partitions.toArray()));
                    }

                    @Override
                    public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
                        // called when a partition is assigned to this consumer instance
                        System.out.printf("%s topic-partitions are assigned for this consumer\n", Arrays.toString(partitions.toArray()));

                        Iterator<TopicPartition> topicPartitionIterator = partitions.iterator();
                        while(topicPartitionIterator.hasNext()) {
                            TopicPartition topicPartition = topicPartitionIterator.next();

                            System.out.println("Topic Name " + topicPartition.topic());
                            System.out.println("Partition No " + topicPartition.partition());

                            System.out.println("Current position is " +
                                    consumer.position(topicPartition) +
                                    " committed offset is ->" +
                                    consumer.committed(topicPartition));

                            if (position == "begin") {
                                // (re)set the offset to beginning
                                // for this consumer group
                                consumer.seekToBeginning(Arrays.asList(topicPartition));
                            }

                            if (position == "end") {
                                consumer.seekToEnd(Arrays.asList(topicPartition));
                            }

                            if (position == "offset") {
                                // get messages from offset 20 onwards
                                consumer.seek(topicPartition, 20);
                            }

                            if (position == "lastCommitted") {
                                // default bevaiour
                                OffsetAndMetadata offsetMeta = consumer.committed(topicPartition);
                                consumer.seek(topicPartition, offsetMeta.offset());
                            }
                        }




                    }
                }
        );



        List<PartitionInfo> partitions = consumer.partitionsFor(TOPIC);
        for (PartitionInfo partitionInfo: partitions) {
            System.out.println("Partition " + partitionInfo);
        }

        System.out.println("Consumer starting..");



        // Get the offset from broker for the given timestamp
        List<TopicPartition> topicPartitions = consumer.partitionsFor(TOPIC)
                .stream()
                .map(pi -> new TopicPartition(pi.topic(), pi.partition()))
                .collect(Collectors.toList());

        long time = 1552373728662L; // time since 1970 in milli seconds


        // iterate over all partitions, set the timestamp as input
        Map<TopicPartition, Long> topicPartitionToTimestampMap = topicPartitions.stream()
                .collect(Collectors.toMap(tp -> tp, tp -> time));

        Map<TopicPartition, OffsetAndTimestamp> result = consumer.offsetsForTimes(topicPartitionToTimestampMap);

        for ( Map.Entry<TopicPartition, OffsetAndTimestamp> entry : result.entrySet()) {
            System.out.println("Offset Key = " + entry.getKey() +
                    ", Offset Value = " + entry.getValue());

            OffsetAndTimestamp offsetAndTimestamp = entry.getValue();
            TopicPartition tp = entry.getKey();

            System.out.printf("For Given Partition %d for time %d, offset is \n",
                                                tp.partition(),
                                                offsetAndTimestamp.timestamp(),
                                                offsetAndTimestamp.offset());
        }




        while (true) {
            // Consumer poll for the data with wait time
            // poll for msgs for 1 second, any messges within second, group together
            // if no msg, exit in 1 second, records length is 0
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));
            if (records.count() == 0)
                continue; // wait for more msg

            // Iterate record and print the record
            for (ConsumerRecord<String, String> record: records) {
                System.out.printf("partition=%d, offset=%d\n", record.partition(),
                        record.offset());

                System.out.printf("key=%s,value=%s, timestamp=%d\n", record.key(), record.value(), record.timestamp());
            }

            consumer.commitSync(); // manual commit, set the offset
        }

    }

}
