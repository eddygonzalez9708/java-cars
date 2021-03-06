package com.lambdaschool.javacars;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class JavaCarsApplication {
    public static final String EXCHANGE_NAME = "CarServer";
    public static final String QUEUE_CAR_LOG = "CarLog";

    public static void main(String[] args) {
        SpringApplication.run(JavaCarsApplication.class, args);
    }

    @Bean
    public TopicExchange appExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue appQueue() {
        return new Queue(QUEUE_CAR_LOG);
    }

    @Bean
    public Binding declareBinding() {
        return BindingBuilder.bind(appQueue()).to(appExchange()).with(QUEUE_CAR_LOG);
    }

    @Bean
    public RabbitTemplate rt(final ConnectionFactory cf) {
        final RabbitTemplate rt = new RabbitTemplate(cf);
        rt.setMessageConverter(producerJackson2MessageConverter());
        return rt;
    }

    @Bean
    public Jackson2JsonMessageConverter producerJackson2MessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
