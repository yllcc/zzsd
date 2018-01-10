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
    public Queue fraudArchiveBufferQueue(){
        return new ActiveMQQueue(queueProperties.getDestination("fraudArchiveBuffer"));
    }

    @Bean
    public Queue fraudArchiveProcessQueue(){
        return new ActiveMQQueue(queueProperties.getDestination("fraudArchiveProcess"));
    }

    @Bean
    public Queue fraudArchiveCallbackQueue(){
        return new ActiveMQQueue(queueProperties.getDestination("fraudArchiveCallback"));
    }
    
    @Bean
    public Queue creditArchiveBufferQueue(){
        return new ActiveMQQueue(queueProperties.getDestination("creditArchiveBuffer"));
    }

    @Bean
    public Queue creditArchiveProcessQueue(){
        return new ActiveMQQueue(queueProperties.getDestination("creditArchiveProcess"));
    }

    @Bean
    public Queue creditArchiveCallbackQueue(){
        return new ActiveMQQueue(queueProperties.getDestination("creditArchiveCallback"));
    }
    
    
}
