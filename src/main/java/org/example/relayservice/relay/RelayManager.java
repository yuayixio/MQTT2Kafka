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

import org.eclipse.paho.client.mqttv3.MqttException;
import org.example.relayservice.model.RelayRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RelayManager {
    private final static Logger LOG = LoggerFactory.getLogger(RelayManager.class);

    private final MqttKafkaRelay mqttKafkaBridge;
    private final RelayRequest req;

    private boolean connected = false;

    public RelayManager(RelayRequest req) throws MqttException {
        this.mqttKafkaBridge = new MqttKafkaRelay(req);
        this.req = req;
    }

    public void startRelay() throws MqttException {
        LOG.info("Start event relay for topic: {}", req.sourceTopic);
        mqttKafkaBridge.start();
    }

    public void stopRelay() throws MqttException {
        LOG.info("Stop event relay for topic: {}", req.sourceTopic);
        mqttKafkaBridge.close();
    }

    public String stats() {
        LOG.info("Get current stats of event relay for topic: {}", req.sourceTopic);
        return mqttKafkaBridge.getStats();
    }
}
