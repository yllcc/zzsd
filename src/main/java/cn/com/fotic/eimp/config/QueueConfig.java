package cn.com.fotic.eimp.config;


import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.jms.Queue;

@Configuration
@EnableConfigurationProperties(QueueProperties.class)
public class QueueConfig {

    @Autowired
    private QueueProperties queueProperties;

    @Bean
    public Queue archiveBufferQueue(){
        return new ActiveMQQueue(queueProperties.getDestination("archiveBuffer"));
    }

    @Bean
    public Queue archiveProcessQueue(){
        return new ActiveMQQueue(queueProperties.getDestination("archiveProcess"));
    }

    @Bean
    public Queue archiveCallbackQueue(){
        return new ActiveMQQueue(queueProperties.getDestination("archiveCallback"));
    }
}
