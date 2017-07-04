package com.tairanchina.csp.dew.core.cluster.spi.rabbit;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.tairanchina.csp.dew.core.cluster.ClusterMQ;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

@Component
@ConditionalOnBean(RabbitAdapter.class)
public class RabbitClusterMQ implements ClusterMQ {

    @Autowired
    private RabbitAdapter rabbitAdapter;

    @Override
    public void publish(String topic, String message) {
        logger.trace("[MQ] publish {}:{}", topic, message);
        Connection connection = rabbitAdapter.getConnection();
        Channel channel = connection.createChannel(false);
        try {
            channel.exchangeDeclare(topic, "fanout");
            channel.basicPublish(topic, "", null, message.getBytes());
        } catch (IOException e) {
            logger.error("[MQ] Rabbit publish error.", e);
        } finally {
            try {
                channel.close();
            } catch (IOException | TimeoutException e) {
                logger.error("[MQ] Rabbit publish error.", e);
            }
            connection.close();
        }
    }

    @Override
    public void subscribe(String topic, Consumer<String> consumer) {
        Channel channel = rabbitAdapter.getConnection().createChannel(false);
        try {
            channel.exchangeDeclare(topic, "fanout");
            String queueName = channel.queueDeclare().getQueue();
            channel.queueBind(queueName, topic, "");
            channel.basicConsume(queueName, false, new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String message = new String(body, "UTF-8");
                    logger.trace("[MQ] subscribe {}:{}", topic, message);
                    try {
                        consumer.accept(message);
                        channel.basicAck(envelope.getDeliveryTag(), false);
                    } catch (Exception e) {
                        logger.error("[MQ] Rabbit subscribe error.", e);
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("[MQ] Rabbit response error.", e);
        }
    }

    @Override
    public void request(String address, String message) {
        logger.trace("[MQ] request {}:{}", address, message);
        Connection connection = rabbitAdapter.getConnection();
        Channel channel = connection.createChannel(false);
        try {
            channel.queueDeclare(address, true, false, false, null);
            channel.basicPublish("", address, null, message.getBytes());
        } catch (IOException e) {
            logger.error("[MQ] Rabbit request error.", e);
        } finally {
            try {
                channel.close();
            } catch (IOException | TimeoutException e) {
                logger.error("[MQ] Rabbit request error.", e);
            }
            connection.close();
        }
    }

    @Override
    public void response(String address, Consumer<String> consumer) {
        Channel channel = rabbitAdapter.getConnection().createChannel(false);
        try {
            channel.queueDeclare(address, true, false, false, null);
            channel.basicConsume(address, false, new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String message = new String(body, "UTF-8");
                    logger.trace("[MQ] response {}:{}", address, message);
                    try {
                        consumer.accept(message);
                        channel.basicAck(envelope.getDeliveryTag(), false);
                    } catch (Exception e) {
                        logger.error("[MQ] Rabbit response error.", e);
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("[MQ] Rabbit response error.", e);
        }
    }

}