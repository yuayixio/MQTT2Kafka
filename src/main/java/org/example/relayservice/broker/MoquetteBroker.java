package org.example.relayservice.broker;/*
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

import io.moquette.BrokerConstants;
import io.moquette.broker.Server;
import io.moquette.broker.config.*;
import io.moquette.interception.InterceptHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class MoquetteBroker {

    private final static Logger LOG = LoggerFactory.getLogger(MoquetteBroker.class.getName());
    private final Server mqttBroker;
    private final IConfig config;
    private final List<? extends InterceptHandler> userHandlers;

    private final String MOQUETTE_HOST = "0.0.0.0";
    private final String MOQUETTE_PORT = "1883";

    public MoquetteBroker() {
        this.mqttBroker = new Server();
        Properties props = new Properties();
        props.setProperty(BrokerConstants.HOST_PROPERTY_NAME, MOQUETTE_HOST);
        props.setProperty(BrokerConstants.PORT_PROPERTY_NAME, MOQUETTE_PORT);
        props.setProperty(BrokerConstants.ALLOW_ANONYMOUS_PROPERTY_NAME, "true");

        this.config = new MemoryConfig(props);

        LOG.info("Start Moquette MQTT broker...");
        userHandlers = Collections.singletonList(new MessageListener());
    }

    public void start() {
        try {
//            mqttBroker.startServer(config, userHandlers);
            mqttBroker.startServer(config);
        } catch (IOException e) {
            LOG.error("Moquette MQTT broker start failed...");
        }
    }

    public void stop() {
        LOG.info("Stop Moquette MQTT broker...");
        mqttBroker.stopServer();
    }
}
