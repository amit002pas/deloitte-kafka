// WordCountStream.java
package ai.nodesense.workshop;

import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.ForeachAction;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Produced;

import java.util.*;


// Input from words
// kafka-topics --zookeeper localhost:2181 --create --topic words --replication-factor 3 --partitions 3

// word count

// write the results to

// kafka-topics --zookeeper localhost:2181 --create --topic word-count-results --replication-factor 1 --partitions 1

// kafka-console-producer --broker-list localhost:9092 --topic words

// kafka-console-consumer --bootstrap-server localhost:9092 --topic word-count-results --from-beginning --property print.key=true --property print.value=true --formatter kafka.tools.DefaultMessageFormatter --property value.deserializer=org.apache.kafka.common.serialization.LongDeserializer
public class WordCountStream {
    public  static void main(final String[] args) throws Exception {
        //final String bootstrapServers = "localhost:9092";
        final String bootstrapServers = "localhost:9092,localhost:9093,localhost:9094";
        String schemaUrl = "http://localhost:8091";

        final Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "word-count-stream");
        props.put(StreamsConfig.CLIENT_ID_CONFIG, "word-count-stream-client");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());

        props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 10 * 1000);
        props.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);

        props.put("schema.registry.url", schemaUrl);

        // Serializer/Deserize string/long type
        final Serde<String> stringSerde = Serdes.String();
        final Serde<Long> longSerde = Serdes.Long();

        // In the subsequent lines we define the processing topology of the Streams application.
        final StreamsBuilder builder = new StreamsBuilder();

        // Consumer, read data from topic words, start the stream
        // starts a topology
        final KStream<String, String> lines = builder
                                              .stream("words");


        // processors, empty lines are not passed down the trimmedLines // filter
        final KStream<String, String> trimmedLines = lines
                                                      .map ( (key, value) -> new KeyValue<String, String>(key, value.trim()))
                                                      .filter( (key, value) -> !value.isEmpty()); // true or false

        trimmedLines.foreach(new ForeachAction<String, String>() {
            @Override
            public void apply(String key, String value) {
                System.out.println("trimmedLines stream input " + key + " Value is  *" + value + "*" );
            }
        });


        // Flat Map convert array into individual element in downstream
        KStream<String, String> splitWords = trimmedLines
                                                .flatMapValues(line -> Arrays.asList(line.toLowerCase().split("\\W+")));


        splitWords.foreach(new ForeachAction<String, String>() {
            @Override
            public void apply(String key, String value) {
                System.out.println("splitWords stream input " + key + " Value is  *" +  value + "*" );
            }
        });

        // aggregation, word count, KTable, word is a key [String], count is a value [Long]
        // stream can produce table, table change log can produce stream
        // Stateful
        KTable<String, Long> wordCount = splitWords
                                         .groupBy( (_$, word) -> word)
                                         .count(); // produce a table, counting all the words


        // Table Change log into stream, do print, write other topic
        KStream<String, Long> wordCountStream = wordCount.toStream();

        wordCountStream.foreach(new ForeachAction<String, Long>() {
            @Override
            public void apply(String word, Long count) {
                System.out.println("Word " + word + " Count is  *" + count + "*" );
            }
        });

        // Output: Write the word count results to Kafka Topics
        // Producer
        // Key is string, word
        // Value is word count, Long
        // Need serialization
        wordCountStream.to("word-count-results", Produced.with(stringSerde, longSerde));


        // take all topologies in the builder, start stream engine
        final KafkaStreams streams = new KafkaStreams(builder.build(), props);


        try {
            System.out.println("Cleaning Stream State");
            streams.cleanUp();
            System.out.println(" Stream State cleaned");

        } catch (Exception e) {
            System.out.println("Exception while clearning stream " + e);
        }

        streams.start();

        // Add shutdown hook to respond to SIGTERM and gracefully close Kafka Streams
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }
}
