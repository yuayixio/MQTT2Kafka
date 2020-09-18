package org.example.relayservice.relay;/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

import com.google.common.base.Throwables;
import com.google.gson.Gson;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.KafkaAdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.config.TopicConfig;
import org.apache.kafka.common.errors.TopicExistsException;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.example.relayservice.config.Config;
import org.example.relayservice.model.RelayRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ExecutionException;

import static org.apache.kafka.clients.producer.ProducerConfig.*;

public abstract class MqttKafkaBase implements MqttCallback {
    private final static Logger LOG = LoggerFactory.getLogger(MqttKafkaBase.class);

    public final int qos = 1;
    public final int EVENT_BUFFER_SIZE = Config.INSTANCE.getEventBufferSizeOrDefault();
    public final int EVENT_BUFFER_TIMEOUT = Config.INSTANCE.getEventBufferTimeoutOrDefault();
    public final ArrayList<ProducerRecord<Long, String>> temporaryRecordBuffer = new ArrayList<>();
    public int numDroppedEvents = 0;
    public int numRelayedEvents = 0;
    public boolean buffering = false;
    public KafkaProducer<Long, String> producer;
    public final MqttClient mqttClient;
    public final RelayRequest relayRequest;
    private long relayStarted;

    public MqttKafkaBase(RelayRequest relayRequest) throws MqttException {
        this.relayRequest = relayRequest;
        this.producer = createKafkaProducer();
        this.mqttClient = createMqttClient();
        this.mqttClient.setCallback(this);
    }

    public void start() throws MqttException {
        relayStarted = System.currentTimeMillis();
        createKafaTopic(relayRequest.targetHost, relayRequest.targetPort, relayRequest.targetHost, 2181);
        mqttClient.connect();
        mqttClient.subscribe(relayRequest.sourceTopic, qos);
    }

    public void close() throws MqttException {
        temporaryRecordBuffer.clear();
        mqttClient.disconnect();
        producer.close();
    }

    public String getStats() {
        Gson gson = new Gson();
        Map<String, Object> m = new HashMap<>();
        m.put("id", relayRequest.sourceTopic);
        m.put("startedAt", relayStarted);
        m.put("numRelayedEvents", numRelayedEvents);
        return gson.toJson(m);
    }

    private MqttClient createMqttClient() throws MqttException {
        return new MqttClient(getMqttUri(),"relay-" + UUID.randomUUID(), new MemoryPersistence());
    }

    private String getMqttUri() {
        return "tcp://" + relayRequest.sourceHost + ":" + relayRequest.sourcePort;
    };

    private KafkaProducer<Long, String> createKafkaProducer() {
        Properties props = new Properties();
        props.put(BOOTSTRAP_SERVERS_CONFIG, relayRequest.targetHost + ":" + relayRequest.targetPort);
        // exactly-once
//        props.put("enable.idempotence", "true");
//        props.put("transactional.id", "prod-" + UUID.randomUUID());
        props.put(ACKS_CONFIG, "all");
        //props.put(RETRIES_CONFIG, 3);
        props.put(REQUEST_TIMEOUT_MS_CONFIG, 2000);
        props.put(MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 1);
        // TODO: tune batch size and linger ms to suit use case of potential far away broker
        // higher batch size/linger results in higher throughput
        props.put(BATCH_SIZE_CONFIG, 16384);
        props.put(LINGER_MS_CONFIG, 20);
        props.put(BUFFER_MEMORY_CONFIG, 33554432);
        props.put(KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class.getName());
        props.put(VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        return new KafkaProducer<Long, String>(props);
    }

    private void createKafaTopic(String kafkaHost, int kafkaPort, String zkHost, int zkPort) {
        String zookeeperHost = zkHost + ":" + zkPort;

        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaHost + ":" + kafkaPort);

        Map<String, String> topicConfig = new HashMap<>();
        topicConfig.put(TopicConfig.RETENTION_MS_CONFIG, "600000");

        AdminClient adminClient = KafkaAdminClient.create(props);

        final NewTopic newTopic = new NewTopic(relayRequest.targetTopic, 1, (short) 1);
        newTopic.configs(topicConfig);

        final CreateTopicsResult createTopicsResult = adminClient.createTopics(Collections.singleton(newTopic));

        for (Map.Entry<String, KafkaFuture<Void>> entry : createTopicsResult.values().entrySet()) {
            try {
                entry.getValue().get();
                LOG.info("topic {} created", entry.getKey());
            } catch (InterruptedException | ExecutionException e) {
                if (Throwables.getRootCause(e) instanceof TopicExistsException) {
                    LOG.info("topic {} existed", entry.getKey());
                }
            }
        }
    }

}
