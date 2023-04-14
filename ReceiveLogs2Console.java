package com.hnc;


import java.io.IOException;
import java.util.concurrent.TimeoutException;


import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.AMQP.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;


public class ReceiveLogs2Console {
    private static final String EXCHANGE_NAME = "logs";

    public static void main(String[] argv) throws IOException, InterruptedException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.9.200.101");
        factory.setPort(5672);
        factory.setUsername("admin");
        factory.setPassword("admin");
        // 打开连接和创建频道，与发送端一样
        com.rabbitmq.client.Connection connection =factory.newConnection();
        final Channel channel =  connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
        // 声明一个随机队列
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, EXCHANGE_NAME, "");
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        // 创建队列消费者
        final Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println(" [x] Received '" + message + "'");
            }
        };
        channel.basicConsume(queueName, true, consumer);
    }
}