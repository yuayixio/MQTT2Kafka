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

import org.example.relayservice.api.RelayResources;
import org.example.relayservice.broker.MoquetteBroker;
import org.example.relayservice.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Init {
    private final static Logger LOG = LoggerFactory.getLogger(Init.class);
    public static void main (String... args) {
        LOG.info("Start relay service ...");
        RelayResources.start();

        if (Config.INSTANCE.useEmbeddedBroker()) {
            //Start broker
            MoquetteBroker moquetteBroker = new MoquetteBroker();
            moquetteBroker.start();

            // Bind a shutdown hook
            Runtime.getRuntime().addShutdownHook(new Thread(moquetteBroker::stop));
        }
    }
}
