# Relay Service for MQTT to Kafka

Relay MQTT event streams to Kafka without the need of Kafka Connect or other third party software

### Development
Start local mosquitto and kafka moquetteBroker
```
docker-compose up -d
```

### Testing
Receiving event streams in mosquitto using `mosquitto_sub` subscriber client from terminal.
```
mosquitto_sub -h localhost -p 1883 --qos 1 -t 'eventStreamFile1'
mosquitto_sub -h localhost -p 1883 --qos 1 -t 'eventStreamFile2'
```

start relayRequest for event stream:
```
curl --location --request POST 'localhost:8080/api/v1/relayRequest/start' \
--header 'Content-Type: application/json' \
--data-raw '{
    "topicFrom": "eventStreamFile1",
    "topicTo": "eventStreamFile1",
    "from": "tcp://localhost:1883",
    "to": "localhost:9094"
}'   
```

stop relayRequest for event stream:
```
curl --location --request POST 'localhost:8080/api/v1/relayRequest/stop' \
--header 'Content-Type: application/json' \
--data-raw '{
    "id": "eventStreamFile1"
}'
```
get stats for event relay:
```
curl --location --request GET 'localhost:8080/api/v1/relay/stats?id=eventStreamFile1'
```

*Note: Here, org.apache.streampipes.flowrate01 is used as eventStreamFile1*
