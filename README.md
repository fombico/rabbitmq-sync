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

## Reactive concurrent RPC calls

A demo to answer the question: "Can we make concurrent RPC calls?"
- Yes we can, recommend using the reactive Flux/Mono from Spring

Demo:
- Run Store, Query, Enrichment
  - a POST to Store causes an RPC to Query, which sends two concurrent RPC calls to Enrichment
- make POST to http://localhost:8080/query
- On Query logs, see that concurrent requests are made to two different exchanges on enrichment.
  The responses are received at different times, however Query waits for both to return before returning a response to the Store.
