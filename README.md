## relayRequest-service
Relay mqtt event stream to kafka

### Development
Start local mosquitto and kafka moquetteBroker
```
docker-compose up -d
```

### Testing
Receiving event streams in mosquitto using `mosquitto_sub` subscriber client from terminal.
```
mosquitto_sub -h localhost -p 1883 --qos 1 -t 'org.apache.streampipes.flowrate01'
mosquitto_sub -h localhost -p 1883 --qos 1 -t 'org.apache.streampipes.flowrate02'
```

start relayRequest for event stream:
```
curl --location --request POST 'localhost:8080/api/v1/relayRequest/start' \
--header 'Content-Type: application/json' \
--data-raw '{
    "topicFrom": "org.apache.streampipes.flowrate01",
    "topicTo": "org.apache.streampipes.flowrate01",
    "from": "tcp://localhost:1883",
    "to": "localhost:9094"
}'   
```

stop relayRequest for event stream:
```
curl --location --request POST 'localhost:8080/api/v1/relayRequest/stop' \
--header 'Content-Type: application/json' \
--data-raw '{
    "id": "org.apache.streampipes.flowrate01"
}'
```
get stats for event relay:
```
curl --location --request GET 'localhost:8080/api/v1/relay/stats?id=org.apache.streampipes.flowrate01'
```

### Changelog
[**10.06.2020**]: read csv files as Java POJO and send to mosquitto moquetteBroker running in individual threads\
[**14.06.2020**]: add REST api, threaded `EventStreamRelay` service, `SampleEventPublisher` and `SampleEventConsumer`, add
stats method to get some basic statistics from the relay\
[**1.06.2020**]: refactored relay service, add Dockerfile
