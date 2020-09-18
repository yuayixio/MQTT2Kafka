package org.example.relayservice.config;/*
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

public class ConfigKeys {
    public static final String MQTT_HOST = "SP_MQTT_HOST";
    public static final String MQTT_PORT = "SP_MQTT_PORT";
//    public static final int MQTT_QOS = 1;
    public static final String KAFKA_HOST = "SP_KAFKA_HOST";
    public static final String KAFKA_PORT = "SP_KAFKA_PORT";

    public static final String USE_EMBEDDED_BROKER = "SP_USE_EMBEDDED_BROKER";
    public static final String EVENT_BUFFER_SIZE = "SP_EVENT_BUFFER_SIZE";
    public static final String EVENT_BUFFER_TIMEOUT_MS = "SP_EVENT_BUFFER_TIMEOUT_MS";
}
