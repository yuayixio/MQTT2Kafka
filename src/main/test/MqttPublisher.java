import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;


import java.net.URI;
import java.net.URISyntaxException;

public class MqttPublisher implements MqttCallback {

    private final int qos = 0;
    private final MqttClient client;

    public MqttPublisher(String uri, String clientId) throws MqttException, URISyntaxException {
        this(new URI(uri), clientId);
    }

    public MqttPublisher(URI uri, String clientId) throws MqttException {
        String host = String.format("tcp://%s:%d", uri.getHost(), uri.getPort());
        this.client = new MqttClient(host, clientId, new MemoryPersistence());
        this.client.setCallback(this);
        this.client.connect();
    }

    public void sendMessage(String payload, String topic) throws MqttException {
        MqttMessage message = new MqttMessage(payload.getBytes());
        message.setQos(qos);
        this.client.publish(topic, message); // Blocking publish
    }

    /**
     * @see MqttCallback#connectionLost(Throwable)
     */
    public void connectionLost(Throwable cause) {
        System.out.println("Connection lost because: " + cause);
        System.exit(1);
    }
    /**
     * @see MqttCallback#deliveryComplete(IMqttDeliveryToken)
     */
    public void deliveryComplete(IMqttDeliveryToken token) {
    }
    /**
     * @see MqttCallback#messageArrived(String, MqttMessage)
     */

    public void messageArrived(String topic, MqttMessage message) throws MqttException {
        System.out.println(String.format("[%s] %s", topic, new String(message.getPayload())));
    }
}
