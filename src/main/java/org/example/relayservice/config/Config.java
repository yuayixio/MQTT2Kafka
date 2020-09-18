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

public enum Config {
    INSTANCE;

    public static final String DEFAULT_MQTT_HOST = "mosquitto";
    public static final int DEFAULT_MQTT_PORT = 1883;
    public static final int DEFAULT_MQTT_QOS = 1;
    public static final String DEFAULT_KAFKA_HOST = "kafka";
    public static final int DEFAULT_KAFKA_PORT = 9092;

    public static final int DEFAULT_EVENT_BUFFER_SIZE = 1000;
    public static final int DEFAULT_EVENT_BUFFER_TIMEOUT_MS = 18000; // 5 * 60 * 60

    Config(){
    }

    public String getMqttHostOrDefault() {
        return envExist(ConfigKeys.MQTT_HOST) ? getEnvAsString(ConfigKeys.MQTT_HOST) : DEFAULT_MQTT_HOST;
    }

    public int getMqttPortOrDefault() {
        return envExist(ConfigKeys.MQTT_HOST) ? getEnvAsInteger(ConfigKeys.MQTT_PORT) : DEFAULT_MQTT_PORT;
    }

    public String getKafkaHostOrDefault() {
        return envExist(ConfigKeys.KAFKA_HOST) ? getEnvAsString(ConfigKeys.KAFKA_HOST) : DEFAULT_KAFKA_HOST;
    }

    public int getKafkaPortOrDefault() {
        return envExist(ConfigKeys.KAFKA_PORT) ? getEnvAsInteger(ConfigKeys.KAFKA_PORT) : DEFAULT_KAFKA_PORT;
    }

    public boolean useEmbeddedBroker() {
        return envExist(ConfigKeys.USE_EMBEDDED_BROKER) ? getEnvAsBoolean(ConfigKeys.USE_EMBEDDED_BROKER) : false;
    }

    public int getEventBufferSizeOrDefault() {
        return envExist(ConfigKeys.EVENT_BUFFER_SIZE) ? getEnvAsInteger(ConfigKeys.EVENT_BUFFER_SIZE) : DEFAULT_EVENT_BUFFER_SIZE;
    }

    public int getEventBufferTimeoutOrDefault() {
        return envExist(ConfigKeys.EVENT_BUFFER_TIMEOUT_MS) ? getEnvAsInteger(ConfigKeys.EVENT_BUFFER_TIMEOUT_MS) : DEFAULT_EVENT_BUFFER_TIMEOUT_MS;
    }

    private boolean envExist(String key) {
        return System.getenv(key) != null;
    }

    private String getEnv(String key) {
        return System.getenv(key);
    }

    private String getEnvAsString(String key) {
        return String.valueOf(getEnv(key));
    }

    private int getEnvAsInteger(String key) {
        return Integer.parseInt(getEnv(key));
    }

    private boolean getEnvAsBoolean(String key) {
        return Boolean.parseBoolean(getEnv(key));
    }

}
