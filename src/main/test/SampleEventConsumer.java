import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.example.relayservice.config.Config;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.UUID;

public class SampleEventConsumer {

    private static final String TOPIC_ONE = "org.apache.streampipes.flowrate01";
    private static final String TOPIC_TWO = "org.apache.streampipes.flowrate02";
    private static final String KAFKA_HOST = Config.INSTANCE.getKafkaHostOrDefault();
    private static final Integer KAFKA_PORT = Config.INSTANCE.getKafkaPortOrDefault();

    private static final String COLON = ":";

    public static void main(String... args) throws Exception {

        run(TOPIC_ONE, "group1");
        run(TOPIC_TWO, "group2");

    }

    static void run(String topic, String groupId) {
        new Thread(() -> {
            final Properties props = new Properties();
            props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_HOST + COLON + KAFKA_PORT);
            props.put(ConsumerConfig.GROUP_ID_CONFIG, "kafka-" + groupId);
            props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class.getName());
            props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

            final Consumer<Long, String> consumer = new KafkaConsumer<Long, String>(props);
            consumer.subscribe(Collections.singletonList(topic));

            while (true) {
                final ConsumerRecords<Long, String> consumerRecords = consumer.poll(Duration.ofMillis(1000));

                for (ConsumerRecord<Long, String> record : consumerRecords){
                    System.out.printf("Consumer Record:(%d, %s, %d, %d)\n",
                            record.key(), record.value(),
                            record.partition(), record.offset());
                }
                consumer.commitAsync();
            }
        }).start();
    }
}
