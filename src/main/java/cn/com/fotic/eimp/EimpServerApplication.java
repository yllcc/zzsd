package cn.com.fotic.eimp;


import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
@ComponentScan
public class EimpServerApplication implements CommandLineRunner {

	@Override
	public void run(String... args) throws Exception {

		/*redisTemplate.opsForValue().set("10000001","测试");
		jmsMessagingTemplate.convertAndSend(archiveBufferQueue, "10000001");
*/
	}

	public static void main(String[] args) throws Exception {
		SpringApplication.run(EimpServerApplication.class, args);
	}
}
