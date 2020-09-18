package org.example.relayservice;/*
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

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.KafkaAdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.TopicConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class TestConn {
    private final static Logger LOG = LoggerFactory.getLogger(TestConn.class);

    private static final String topic = "org.apache.streampipes.flowrate04";

    public static void main(String ... args) {
        createKafaTopic();
    }

    private static void createKafaTopic() {
        String zookeeperHost = "192.168.0.35" + ":" + 2181;

        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.0.35" + ":" + 9095);

        Map<String, String> topicConfig = new HashMap<>();
        topicConfig.put(TopicConfig.RETENTION_MS_CONFIG, "600000");

        AdminClient adminClient = KafkaAdminClient.create(props);

        final NewTopic newTopic = new NewTopic(topic, 1, (short) 1);
        newTopic.configs(topicConfig);

        final CreateTopicsResult createTopicsResult = adminClient.createTopics(Collections.singleton(newTopic));

        try {
            createTopicsResult.values().get(topic).get();
        } catch (InterruptedException | ExecutionException e) {
            LOG.error("Could not create topic: " + topic + " on broker " + zookeeperHost);
        }
    }
}
