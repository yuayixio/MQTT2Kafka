import com.google.gson.Gson;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import org.example.relayservice.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SampleEventPublisher {

    private final static Logger LOG = LoggerFactory.getLogger(SampleEventPublisher.class);

    private static final String SAMPLE_DATA_ONE = "flowrate01.csv";
    private static final String SAMPLE_DATA_TWO = "flowrate02.csv";

    private static final String TOPIC_ONE = "org.apache.streampipes.flowrate01";
    private static final String TOPIC_TWO = "org.apache.streampipes.flowrate02";

    private static final int SPEED_IN_MS_TOPIC_ONE = 100;
    private static final int SPEED_IN_MS_TOPIC_TWO = 1000;

    public static final String PROTOCOL = "tcp";
    public static final String SLASH = "/";
    public static final String DOUBLE_SLASH = "//";
    public static final String COLON = ":";

    public static void main (String [] args) {

        // start two threads to publish event stream to mosquitto broker
        LOG.info("publish on {} @ {}ms", TOPIC_ONE, SPEED_IN_MS_TOPIC_ONE);
        run(SAMPLE_DATA_ONE, TOPIC_ONE, "mqtt-1", SPEED_IN_MS_TOPIC_ONE);
        LOG.info("publish on {} @ {}ms", TOPIC_TWO, SPEED_IN_MS_TOPIC_TWO);
        run(SAMPLE_DATA_TWO, TOPIC_TWO, "mqtt-2", SPEED_IN_MS_TOPIC_TWO);

    }

    private static void run(String file, String topic, String clientId, int SpeedInMillis) {
        new Thread(() -> {
            try {
                publish(readCsvToBean(file), getMqttUri(), topic, clientId, SpeedInMillis);
            } catch (MqttException | URISyntaxException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private static void publish(List<FlowRate> events, String mqttUri, String topic, String clientId, int SpeedInMillis) throws MqttException, URISyntaxException {
        MqttPublisher publisher = new MqttPublisher(mqttUri, clientId);
        Gson gson = new Gson();

        AtomicInteger idCounter = new AtomicInteger();

        // loop forever
        while (true) {
            events.forEach(f -> {
                f.setId(idCounter.getAndIncrement());
                String payload = gson.toJson(f);
                try {

                    LOG.debug("Publish event to mosquitto: {}", topic);
                    publisher.sendMessage(payload, topic);

                    Thread.sleep(SpeedInMillis);
                } catch (MqttException | InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private static String getMqttUri() {
        return PROTOCOL + COLON + DOUBLE_SLASH + Config.INSTANCE.getMqttHostOrDefault() + COLON + Config.INSTANCE.getMqttPortOrDefault();
    }

    private static List<FlowRate> readCsvToBean(String csv) {

        URL url = SampleEventPublisher.class.getClassLoader().getResource(csv);
        List<FlowRate> flowRateList = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(url.toURI()), StandardCharsets.UTF_8)) {
            HeaderColumnNameMappingStrategy<FlowRate> mappingStrategy = new HeaderColumnNameMappingStrategy<>();
            mappingStrategy.setType(FlowRate.class);

            CsvToBean<FlowRate> csvToBean = new CsvToBeanBuilder<FlowRate>(reader)
                    .withType(FlowRate.class)
                    .withMappingStrategy(mappingStrategy)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            flowRateList = csvToBean.parse();

            return flowRateList;

        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
        return flowRateList;
    }
}
