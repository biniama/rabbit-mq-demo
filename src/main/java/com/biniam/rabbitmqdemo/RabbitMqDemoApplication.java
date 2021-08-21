package com.biniam.rabbitmqdemo;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class RabbitMqDemoApplication {

    /**
     * JMS queues and AMQP queues have different semantics. For example, JMS .
     * AMQP queues sends queued messages to an exchange, which can go to a single queue or fan out to multiple queues,
     * emulating the concept of JMS topics.
     */
    static final String topicExchangeName = "spring-boot-exchange";
    static final String queueName = "spring-boot";

    @Bean
    Queue queue() {
        return new Queue(queueName, false);
    }

    @Bean
    TopicExchange topicExchange() {
        return new TopicExchange(topicExchangeName);
    }

    /**
     * Spring AMQP requires that the Queue, the TopicExchange, and the Binding be declared as top-level
     * Spring beans in order to be set up properly.
     * <p>
     * We use a topic exchange, and the queue is bound with a routing key of "foo.bar.#",
     * which means that any messages sent with a routing key that begins with "foo.bar." are routed to the queue.
     */
    @Bean
    Binding binding(Queue queue, TopicExchange topicExchange) {
        return BindingBuilder
                .bind(queue)
                .to(topicExchange)
                .with("foo.bar.#");
    }

    /**
     * The bean defined in the messageListenerAdapter() method is registered as a message listener in the container (defined in container()).
     * It listens for messages on the "spring-boot" queue.
     * Because the Receiver class is a POJO, it needs to be wrapped in the MessageListenerAdapter,
     * where you specify that it invokes "receiveMessage" method.
     */
    @Bean
    MessageListenerAdapter messageListenerAdapter(Receiver receiver) {
        return new MessageListenerAdapter(receiver, "receiveMessage");
    }

    @Bean
    SimpleMessageListenerContainer container(ConnectionFactory connectionFactory, MessageListenerAdapter messageListenerAdapter) {

        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addQueueNames(queueName);
        container.setMessageListener(messageListenerAdapter);
        return container;
    }

    public static void main(String[] args) {
        SpringApplication.run(RabbitMqDemoApplication.class, args);
    }

}
