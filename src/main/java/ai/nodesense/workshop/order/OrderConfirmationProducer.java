// OrderConfirmationProducer.java
package ai.nodesense.workshop.order;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;
import java.util.Random;

import static org.apache.kafka.clients.producer.ProducerConfig.*;
import static org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.streams.StreamsConfig.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.streams.StreamsConfig.RETRIES_CONFIG;

//kafka-topics --zookeeper localhost:2181 --create --topic order-confirmations --replication-factor 1 --partitions 3

public class OrderConfirmationProducer {
    //public static String BOOTSTRAP_SERVERS = "116.203.31.40:9092";

    public static String BOOTSTRAP_SERVERS = "k1.nodesense.ai:9092";
    public static String TOPIC = "order-confirmations";

    static Random r = new Random();

    static String countries[] = new String[]{"IN", "USA", "UK", "AU"};

    // generate random data
    static OrderConfirmation nextOrder() {
        OrderConfirmation orderConfirmation = new OrderConfirmation();
        orderConfirmation.amount = 100.0 + r.nextInt(1000);
        orderConfirmation.orderId = String.valueOf(r.nextInt(1000000));
        orderConfirmation.customerId = String.valueOf(r.nextInt(1000000));

        String country = countries[r.nextInt(countries.length)];

        orderConfirmation.country = country;

        return orderConfirmation;
    }


    public static void main(String[] args) throws Exception {
        System.out.println("Welcome to producer");

        Properties props = new Properties();

        props.put(BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ACKS_CONFIG, "all");
        props.put(RETRIES_CONFIG, 0);
        props.put(BATCH_SIZE_CONFIG, 16000);
        props.put(LINGER_MS_CONFIG, 100);
        props.put(BUFFER_MEMORY_CONFIG, 33554432);
        props.put(KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");

        // custom serializer.
        props.put(VALUE_SERIALIZER_CLASS_CONFIG, OrderConfirmationSerializer.class);

        //props.put("partitioner.class", "ai.nodesense.workshop.order.OrderConfirmationPartitioner");
        props.put("partitioner.class", OrderConfirmationPartitioner.class);


        // Key as string, value as OrderConfirmation
        Producer<String, OrderConfirmation> producer = new KafkaProducer<>(props);


        Random r = new Random();

        int counter = 1000000;
        for (int i = 0; i < 1000000; i++) {
            OrderConfirmation orderConfirmation = nextOrder();
            // producer record, topic, key (null), value (message)
            // send message, not waiting for ack

            // Key is a string
            // Value is OrderConfirmation
            ProducerRecord<String, OrderConfirmation> record = new ProducerRecord<>(TOPIC,
                                                                    orderConfirmation.country, // key
                                                                    orderConfirmation); // value
            System.out.println("Sending " + orderConfirmation.orderId);

            // during send, producer calls Serializer, convert orderconfirmation into bytes
            producer.send(record);
            System.out.printf("order send %s sent\n", record);
            Thread.sleep(25); // Demo only,
        }

        producer.close();
    }
}