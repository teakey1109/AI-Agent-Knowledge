package com.knowledge.base.foundation.config;


import com.rabbitmq.client.ConnectionFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;


/**
 * RabbitMQ 消息队列配置
 *
 * <p>队列名与路由键使用 InstanceIdentifier 进行实例隔离，
 * 确保多开发者本地环境的消息互不干扰，
 * 自己电脑产生的 MQ 消息只被自己电脑的消费者消费。</p>
 *
 * @author fangAndlu
 */
public class RabbitMQConfig {

    // 通知交换机
    public static final String NOTIFICATION_EXCHANGE = "kb.notification.exchange";

    // 通知队列
    public static final String NOTIFICATION_QUEUE = "kb.notification.queue";
    public static final String NOTIFICATION_ROUTING_KEY = "notification.#";

    // 系统配置更新交换机
    public static final String CONFIG_EXCHANGE = "kb.config.exchange";

    // 系统配置更新队列
    public static final String CONFIG_QUEUE = "kb.config.queue";
    public static final String CONFIG_ROUTING_KEY = "config.update";

    /**
     * 通知交换机（主题模式）
     */
    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(NOTIFICATION_EXCHANGE, true, false);
    }

    /**
     * 通知队列
     */
    @Bean
    public Queue notificationQueue() {
        return QueueBuilder.durable(NOTIFICATION_QUEUE).build();
    }

    /**
     * 通知队列绑定
     */
    @Bean
    public Binding notificationBinding() {
        return BindingBuilder.bind(notificationQueue())
                .to(topicExchange())
                .with(NOTIFICATION_ROUTING_KEY);
    }

    /**
     * 系统配置更新交换机（直连模式）
     */
    @Bean
    public DirectExchange configExchange() {
        return new DirectExchange(CONFIG_EXCHANGE, true, false);
    }

    /**
     * 系统配置更新队列
     */
    @Bean
    public Queue configQueue() {
        return QueueBuilder.durable(CONFIG_QUEUE).build();
    }

    /**
     * 系统配置队列绑定
     */
    @Bean
    public Binding configBinding() {
        return BindingBuilder.bind(configQueue())
                .to(configExchange())
                .with(CONFIG_ROUTING_KEY);
    }

    /**
     * RabbitMQ 模板（配置消息转换器）
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        return rabbitTemplate;
    }

}
