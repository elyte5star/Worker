package org.elyte.worker;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import java.nio.charset.StandardCharsets;
import lombok.Getter;


public class Queue {
    private static final String HOST = "localhost";

    @Getter
    private Channel channel;
   

    public Queue() {
        ConnectionFactory cf = new ConnectionFactory();
        cf.setHost(HOST);
        try {
            Connection connection = cf.newConnection();
            channel = connection.createChannel();
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    public void createExchangeQueue(String queueName, String exchangeName, String exchangeType, String key) {
        try {
            channel.queueDeclare(queueName, true, false, false, null);
            channel.exchangeDeclare(exchangeName, exchangeType,true);
            channel.queueBind(queueName, exchangeName, key);
            channel.basicQos(1); // accept only one unack-ed message at a time
            System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    public void listenToQueue(String queueName, DeliverCallback dlr) {
        try {
            channel.basicConsume(queueName, false, dlr, consumerTag -> {
            });

        } catch (Exception e) {
            System.err.println(e);
        }
    }

    public void sendMessage(String exchange, String key, String message) {
        try {
            channel.basicPublish(exchange, key, null, message.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            System.err.println(e);
        }
    }

}
