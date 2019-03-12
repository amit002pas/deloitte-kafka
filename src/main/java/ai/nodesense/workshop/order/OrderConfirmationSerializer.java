// OrderConfirmationSerializer.java
package ai.nodesense.workshop.order;


import java.nio.charset.StandardCharsets;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

import java.nio.charset.StandardCharsets;
import java.util.Map;

// Convert OrderConfirmation object (java object) to serialized format/JSON bytes
// OrderConfirmationSerializer is called by the producer
//  when producer.send(.., orderConfirmation)

public class OrderConfirmationSerializer<T> implements Serializer<T> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Default constructor needed by Kafka
     */
    public OrderConfirmationSerializer() {
        System.out.println("OrderConfirmationSerializer object created ");
    }

    @Override
    public void configure(Map<String, ?> props, boolean isKey) {
        // producer props new Producer(props)
    }

    // invoked when producer.send(orderData)
    @Override
    public byte[] serialize(String topic, T orderConfirmationObj) {
        System.out.println("Orderconfirmation serialize called ");

        if (orderConfirmationObj == null)
            return null;

        try {
            // convert orderconfirmation to JSON bytes
            byte[] bytes = objectMapper.writeValueAsBytes(orderConfirmationObj);
            System.out.println("Bytes " + bytes);

            System.out.println("Bytes string " +  new String(bytes, StandardCharsets.UTF_8));
            return bytes;

        } catch (Exception e) {
            throw new SerializationException("Error serializing JSON message", e);
        }
    }

    @Override
    public void close() {
        // when producer closed, release memory/db connection
    }
}