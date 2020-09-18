package org.example.relayservice.api;/*
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

import org.example.relayservice.relay.RelayManager;
import org.example.relayservice.relay.RunningRelayInstances;
import org.example.relayservice.relay.Status;
import org.example.relayservice.model.RelayRequest;
import org.example.relayservice.model.StopRequest;
import org.rapidoid.http.MediaType;
import org.rapidoid.http.Resp;
import org.rapidoid.setup.On;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RelayResources {
    private final static Logger LOG = LoggerFactory.getLogger(RelayResources.class);

    private static final String API_VERSION = "/api/v1";
    private static final String RELAY_ROUTE = "/relay";

    public RelayResources() {
    }

    public static void start() {

        On.post(API_VERSION + RELAY_ROUTE + "/start").json((RelayRequest req, Resp res) -> {
            Status status;

            if (!RunningRelayInstances.INSTANCE.isRunning(req.sourceTopic)) {
                RelayManager relay = new RelayManager(req);
                relay.startRelay();
                RunningRelayInstances.INSTANCE.addRelay(req.sourceTopic, relay);
                status = Status.ACCEPTED;
            }
            else {
                status = Status.REJECTED;
            }
            String json = "{\"status\": \"" + status + "\"}";

            return res.code(200).contentType(MediaType.JSON).body(json.getBytes());
        });

        On.post(API_VERSION + RELAY_ROUTE + "/stop").json((StopRequest stopRequest, Resp res) -> {
            Status status;

            if (RunningRelayInstances.INSTANCE.isRunning(stopRequest.id)) {
                RelayManager relay = RunningRelayInstances.INSTANCE.removeRelay(stopRequest.id);
                relay.stopRelay();
                status = Status.ACCEPTED;
            }
            else {
                status = Status.REJECTED;
            }
            String json = "{\"status\": \"" + status + "\"}";

            return res.code(200).contentType(MediaType.JSON).body(json.getBytes());
        });

        On.get(API_VERSION + RELAY_ROUTE + "/stats").json((String id, Resp res) -> {

            RelayManager relay = RunningRelayInstances.INSTANCE.get(id);
            String json = relay !=null ? relay.stats() : "{\"status\": \"" + Status.REJECTED + "\"}";

            return res.code(200).contentType(MediaType.JSON).body(json.getBytes());
        });
    }

}
