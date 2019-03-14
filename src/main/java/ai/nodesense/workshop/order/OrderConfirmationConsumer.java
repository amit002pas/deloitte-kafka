// OrderConfirmationConsumer.java
package ai.nodesense.workshop.order;


import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.Properties;

import static java.time.Duration.ofSeconds;
import static java.util.Collections.singletonList;
import static org.apache.kafka.clients.consumer.ConsumerConfig.*;

// kafka-topics --zookeeper localhost:2181 --create --topic order-confirmations --replication-factor 1 --partitions 3


public class OrderConfirmationConsumer {

    public static String BOOTSTRAP_SERVERS = "k1.nodesense.ai:9092";
    public static String TOPIC = "order-confirmations";

//    public static String BOOTSTRAP_SERVERS = "116.203.31.40:9092";
//    public static String TOPIC = "greetings";



    public static void main(String[] args) {

        Properties props = new Properties();
        props.put(BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);

        props.put(GROUP_ID_CONFIG, "orderconfirmation-consumer-group"); // offset, etc, TODO

        props.put(ENABLE_AUTO_COMMIT_CONFIG, "false");
        props.put(AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");

        props.put(SESSION_TIMEOUT_MS_CONFIG, "30000");

        props.put(KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(VALUE_DESERIALIZER_CLASS_CONFIG, OrderConfirmationDeserializer.class);

        // <Key as string, Value as OrderConfirmation>
        KafkaConsumer<String, OrderConfirmation> consumer = new KafkaConsumer<>(props);

        // Subscribe for topic(s)
        consumer.subscribe(singletonList(TOPIC));

        System.out.println("Consumer Starting!");

        while (true) {
            // poll (timeout value), wait for 1 second, get all the messages
            // <Key, Value>
            // poll internally calls deserialize, convert bytes to object data
            System.out.println("Calling poll");
            ConsumerRecords<String, OrderConfirmation> records = consumer.poll(ofSeconds(1));
            // if no messages
            if (records.count() == 0)
                continue;


            System.out.println("Fetched " + records.count() + " order confirmations");
            // Iterating over each record
            for (ConsumerRecord<String, OrderConfirmation> record : records) {

                System.out.printf("partition= %d, offset= %d, key= %s, value= %s\n",
                        record.partition(),
                        record.offset(),
                        record.key(),
                        record.value());

                OrderConfirmation orderConfirmation = record.value();
                System.out.println(("order no " + orderConfirmation.orderId));
                System.out.println(("Country code  " + orderConfirmation.country));
            }

            consumer.commitSync();

        }
    }
}