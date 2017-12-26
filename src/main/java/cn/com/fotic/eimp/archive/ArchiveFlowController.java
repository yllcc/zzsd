package cn.com.fotic.eimp.archive;

import javax.jms.Queue;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Component;


import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ArchiveFlowController {

	@Autowired
	private JmsMessagingTemplate jmsMessagingTemplate;
	@Autowired
	private StringRedisTemplate redisTemplate;
	@Autowired
	private Queue archiveProcessQueue;
	@Autowired
	private Queue archiveCallbackQueue;


	@JmsListener(destination = "${queue.archiveBuffer.destination}", concurrency = "${queue.archiveBuffer.concurrency}")
	public void bufferQueueConsumer(String reqSerial) {

		String content = redisTemplate.opsForValue().get(reqSerial);

		log.info(reqSerial+":"+content+"开始处理");

		redisTemplate.opsForValue().set("10000002","测试2");
		jmsMessagingTemplate.convertAndSend(archiveProcessQueue, "10000002");

		redisTemplate.delete(reqSerial);
		log.info(reqSerial+"处理完成，已从redis队列删除");
	}

	@JmsListener(destination = "${queue.archiveProcess.destination}", concurrency = "${queue.archiveProcess.concurrency}")
	public void processQueueConsumer(String reqSerial) {

		String content = redisTemplate.opsForValue().get(reqSerial);

		log.info(reqSerial+":"+content+"开始处理");

		redisTemplate.opsForValue().set("10000003","测试3");
		jmsMessagingTemplate.convertAndSend(archiveCallbackQueue, "10000003");

		redisTemplate.delete(reqSerial);
		log.info(reqSerial+"处理完成，已从redis队列删除");

	}

	@JmsListener(destination = "${queue.archiveCallback.destination}", concurrency = "${queue.archiveCallback.concurrency}")
	public void callbackQueueConsumer(String reqSerial) {

		String content = redisTemplate.opsForValue().get(reqSerial);

		log.info(reqSerial+":"+content+"开始处理");

		redisTemplate.delete(reqSerial);
		log.info(reqSerial+"处理完成，已从redis队列删除");
	}
}
