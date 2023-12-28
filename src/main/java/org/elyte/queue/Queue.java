package org.elyte.queue;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import java.nio.charset.StandardCharsets;
import com.rabbitmq.client.MessageProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.elyte.util.AppConfig;
import lombok.Getter;

public class Queue extends AppConfig {

    @Getter
    private Channel channel;

    private static final Logger log = LoggerFactory.getLogger(Queue.class);
    

    public Queue() {

        ConnectionFactory cf = new ConnectionFactory();

        cf.setHost(this.getConfigValue("RABBIT_HOST"));
        cf.setUsername(this.getConfigValue("RABBITMQ_DEFAULT_USER"));
        cf.setPassword(this.getConfigValue("RABBITMQ_DEFAULT_PASS"));
        cf.setVirtualHost("/");
        cf.setPort(Integer.valueOf(this.getConfigValue("RABBITMQ_NODE_PORT")));

        try {
            Connection connection = cf.newConnection();
            channel = connection.createChannel();
        } catch (Exception e) {
            log.error("[+] Connection Exception: ", e.getLocalizedMessage());
        }
    }

    public void createExchangeQueue(String queueName, String exchangeName, String exchangeType, String key) {
        try {
            channel.queueDeclare(queueName, true, false, false, null);
            channel.exchangeDeclare(exchangeName, exchangeType, true);
            channel.queueBind(queueName, exchangeName, key);
            channel.basicQos(1); // accept only one unack-ed message at a time
            log.info(" [*] Waiting for messages. To exit press CTRL+C");
        } catch (Exception e) {
            log.error("[+] Creating Exchange Exception: ", e.getLocalizedMessage());
        }
    }

    public void listenToQueue(String queueName, DeliverCallback dlr) {
        try {
            channel.basicConsume(queueName, false, dlr, consumerTag -> {
            });

        } catch (Exception e) {
            log.error("[+] Consumer Exception: ", e.getLocalizedMessage());
        }
    }

    public void sendMessage(String exchange, String key, String message) {
        try {
            channel.basicPublish(exchange, key, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            log.error("[+] Publisher Exception: ", e.getLocalizedMessage());
        }
    }

}
