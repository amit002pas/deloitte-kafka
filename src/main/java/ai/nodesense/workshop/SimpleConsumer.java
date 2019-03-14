// SimpleConsumer.java
package ai.nodesense.workshop;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

import static org.apache.kafka.clients.consumer.ConsumerConfig.*;

public class SimpleConsumer {
    public static String BOOTSTRAP_SERVERS = "k1.nodesense.ai:9092";
    public static String TOPIC = "greetings";

    public static void main(String[] args) throws Exception {
        Properties props = new Properties();
        props.put(BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);

        props.put(GROUP_ID_CONFIG, "greeting-consumer-2"); // offset, etc, TODO

        props.put(ENABLE_AUTO_COMMIT_CONFIG, "false");
        props.put(AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");

        props.put(SESSION_TIMEOUT_MS_CONFIG, "30000");

        props.put(KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");

        // <Key as string, Value as string>
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);

        // subscribe from one or more topics
        consumer.subscribe(Collections.singletonList(TOPIC));

        System.out.println("Consumer starting..");

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

                System.out.printf("key=%s,value=%s\n", record.key(), record.value());
            }

            consumer.commitSync(); // manual commit, set the offset
        }

    }

}
