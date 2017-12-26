package cn.com.fotic.eimp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jms.core.JmsMessagingTemplate;

import javax.jms.Queue;

@Configuration
@EnableAutoConfiguration
@ComponentScan
public class EimpServerApplication implements CommandLineRunner {

	@Autowired
	private StringRedisTemplate redisTemplate;
	@Autowired
	private JmsMessagingTemplate jmsMessagingTemplate;
	@Autowired
	private Queue archiveBufferQueue;

	@Override
	public void run(String... args) throws Exception {

		redisTemplate.opsForValue().set("10000001","测试");
		jmsMessagingTemplate.convertAndSend(archiveBufferQueue, "10000001");

	}

	public static void main(String[] args) throws Exception {
		SpringApplication.run(EimpServerApplication.class, args);
	}
}
