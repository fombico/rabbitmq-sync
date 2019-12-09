# Synchronous RabbitMQ example

An example using rabbit mq synchronously.

**Store** sends a request, which is picked up by **Query**, who sends a modified request to **Enrichment**.

**Enrichment** responds back to **Query**, who returns the result back to **Store**.


Multiple stores are supported - you can see messages only going to the store as the `routingKey` forwards it to the correct store. To set this up, create a copy of the run configuration for the store and override the environment variables:
- `storeNumber` - to a different value
- `server.port` - to a different port


**Query** and **Enrichment** do not have `spring-starter-web` and do not run on a port. They only listen to rabbit mq events.

Also, `spring-cloud-starter-sleuth` has been added to see trace/span ids per request.

## Requirements
- **Uses Lombok** - be sure to enable annotation processing
- **Uses RabbitMQ** - install and run via brew or docker

## Testing
**Query** has an example of an integration test, which uses a mock RabbitMQ: https://github.com/fridujo/rabbitmq-mock

## Concurrent synchronous RabbitMQ Test
A test to see if we can handle multiple concurrent requests to execute synchronously (like an HTTP server)
 
- Run Query, Enrichment, Discomm
    - Query receives messages, sends it to Enrichment, which makes HTTP calls to Discomm.
    - Discomm returns a response in between 30-60s.

- Get a copy of the rabbitmq perf test repo:
  https://github.com/rabbitmq/rabbitmq-perf-test/releases

  - create a `content1.json` and put it inside `rabbitmq-perf-test/`:
  ```json
  {
  	"userId" : "2134",
  	"storeNumber": "3000"
  }
  ```
  
  From the parent directory in the `rabbitmq-perf-test/` repo, run the perf test to generate data:
  ```bash
  bin/runjava com.rabbitmq.perf.PerfTest -x 1 -y 1 -u "loadtest.query.exchange.requests"  --id "test 1" -f persistent -ad false --body content1.json --body-content-type application/json --message-properties replyTo=asdf -t direct -r 10 -z 3
  ```

- See that Query can process multiple messages concurrently. 

This was accomplished by
- Setting `spring.rabbitmq.listener.simple.concurrency` to 10. It is 1 by default
- Creating a new `RestTemplate` for each outbound request in Enrichment. If you had a single `RestTemplate` field you would end up blocking on it while it's waiting for a different request to finish.