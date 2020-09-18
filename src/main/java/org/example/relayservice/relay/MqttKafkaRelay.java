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

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.KafkaException;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.example.relayservice.model.RelayRequest;
import org.example.relayservice.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MqttKafkaRelay extends MqttKafkaBase {
    private final static Logger LOG = LoggerFactory.getLogger(MqttKafkaRelay.class);


    public MqttKafkaRelay(RelayRequest relayRequest) throws MqttException {
        super(relayRequest);
    }

    @Override
    public void connectionLost(Throwable throwable) {
        LOG.info("Connection lost because: {}", throwable.toString());
        System.exit(1);
    }

    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {

        String event = new String(mqttMessage.getPayload());

        long time = System.currentTimeMillis();
        ProducerRecord<Long, String> currEventRecord = new ProducerRecord<Long, String>(relayRequest.targetTopic, time, event);

        // Check if Kafka is reachable
        // If Kafka is not reachable buffer events in temporary record buffer
        // TODO: isServiceReachable should run independant of callback
        if(Utils.isServiceReachable(relayRequest.targetHost, relayRequest.targetPort, 10)) {
            if (temporaryRecordBuffer.isEmpty()) {
                // send events to upstream
                try{
                    producer.send(currEventRecord);
                    numRelayedEvents++;
                }  catch( KafkaException e ) {
                    LOG.error("Kafka exception: {}", e);
                }
                //this.totalRelayedEventsSize = this.totalRelayedEventsSize + event.getBytes(StandardCharsets.UTF_8).length;
            }
            else {
                // TODO: send buffered event should run independant of callback
                // send buffered events & clear buffer
                LOG.info("Connection re-established. Send events from temporary buffer for opic={} (send={}, dropped={})", relayRequest.targetTopic, temporaryRecordBuffer.size(), numDroppedEvents);

                // add current event from callback
                temporaryRecordBuffer.add(currEventRecord);
                temporaryRecordBuffer.forEach(r -> {
                    try{
                        producer.send(r);
                        numRelayedEvents++;
                    }  catch( KafkaException e ) {
                        LOG.error("Kafka exception: {}", e);
                    }
                });
                temporaryRecordBuffer.clear();
                numDroppedEvents = 0;
                buffering = false;
            }
        }
        else {
            // add event to buffer
            if(!buffering) {
                LOG.info("Connection issue. Temporarily store event in buffer for topic={}", relayRequest.targetTopic);
                buffering = true;
            }

            if (temporaryRecordBuffer.size() != EVENT_BUFFER_SIZE) {
                temporaryRecordBuffer.add(currEventRecord);
            }
            else {
                // evict oldest event
                temporaryRecordBuffer.remove(0);
                numDroppedEvents++;
            }
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }


}
