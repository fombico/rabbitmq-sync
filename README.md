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
**Query** has an example of an integration test, which uses an in-memory plugin of a different AMQP framework known as [Qpid](https://qpid.apache.org/)

The test was written based on https://novotnyr.github.io/scrolls/qpid-as-mocking-amqp-broker-for-integration-tests/.

Also see notes on RabbitMQ and Qpid interoperability here: https://www.rabbitmq.com/interoperability.html

The test runs Qpid on port 9000. This is configured in (unfortunately) 3 places:
- `qpid-embedded-inmemory-configuration.json` - config for Qpid
- `test/resources/application.properties` - config RabbitMQ to use this port and to create a **new variable** `qpid.amqp_port`
- `MessageListenerTest`'s usage of `CachingConnectionFactory`, which uses the `qpid.amqp_port`
 