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

import io.moquette.interception.AbstractInterceptHandler;
import io.moquette.interception.messages.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.nio.charset.StandardCharsets.UTF_8;

public class MessageListener extends AbstractInterceptHandler {

    private final static Logger LOG = LoggerFactory.getLogger(MessageListener.class);

    @Override
    public String getID() {
        return "MessageListener";
    }

    @Override
    public void onConnect(InterceptConnectMessage msg) {
        LOG.info("client connected: " + msg.getClientID());
    }

    @Override
    public void onDisconnect(InterceptDisconnectMessage msg) {
        LOG.info("client disconnected: " + msg.getClientID());
    }

    @Override
    public void onSubscribe(InterceptSubscribeMessage msg) {
        LOG.info("new subsciber: {} [{}]",msg.getClientID(), msg.getTopicFilter());
    }

    @Override
    public void onUnsubscribe(InterceptUnsubscribeMessage msg) {
        LOG.info("unsubscribe: {} [{}]", msg.getClientID(), msg.getTopicFilter());
    }

    @Override
    public void onPublish(InterceptPublishMessage msg) {

        // Create array for payload
        int readableBytes = msg.getPayload().readableBytes();
        byte[] payload = new byte[readableBytes];

        // Read bytes from payload
        for (int i = 0; i < readableBytes; i++) {
            payload[i] = msg.getPayload().readByte();
        }

        // Create string from payload
        String decodedPayload = new String(payload, UTF_8);
        LOG.debug("Received on topic: " + msg.getTopicName() + " content: " + decodedPayload);
    }
}
