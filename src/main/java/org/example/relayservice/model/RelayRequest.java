package org.example.relayservice.model;/*
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

public class RelayRequest {
    public String sourceTopic;
    public String sourceHost;
    public int sourcePort;
    public String targetTopic;
    public String targetHost;
    public int targetPort;

    public RelayRequest() {
    }

    public RelayRequest(String sourceTopic, String sourceHost, int sourcePort, String targetTopic, String targetHost, int targetPort) {
        this.sourceTopic = sourceTopic;
        this.sourceHost = sourceHost;
        this.sourcePort = sourcePort;
        this.targetTopic = targetTopic;
        this.targetHost = targetHost;
        this.targetPort = targetPort;
    }
}
