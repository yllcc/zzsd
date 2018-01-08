package cn.com.fotic.eimp.archive;

import java.io.IOException;


import javax.jms.Queue;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.fastjson.JSON;
import cn.com.fotic.eimp.model.CallBackUserCreditModel;
import cn.com.fotic.eimp.model.UserCreditReturnModel;
import cn.com.fotic.eimp.service.CreditService;
import cn.com.fotic.eimp.service.FraudService;
import lombok.extern.slf4j.Slf4j;

/**
 * 反欺诈接口
 * 
 * @author liugj
 *
 */

@Controller
@Slf4j
@RestController
@RequestMapping("/")
public class FraudController {
	@Autowired
	private JmsMessagingTemplate fraudJmsMessagingTemplate;

	@Autowired
	private StringRedisTemplate fraudRedisTemplate;

	@Autowired
	private Queue fraudArchiveBufferQueue;

	@Autowired
	private Queue fraudArchiveProcessQueue;

	@Autowired
	private Queue fraudArchiveCallbackQueue;

	@Autowired
	private CreditService creditService;

	@Autowired
	private FraudService fraudService;

	@JmsListener(destination = "${queue.fraudArchiveBuffer.destination}", concurrency = "${queue.fraudArchiveBuffer.concurrency}")
	public void bufferQueueConsumer(String reqSerial) {
		 
		String json = fraudRedisTemplate.opsForValue().get(reqSerial);
		log.info("流水号:" + reqSerial + "JSON数据:" + json);

		fraudRedisTemplate.opsForValue().set(reqSerial, json);
		fraudJmsMessagingTemplate.convertAndSend(fraudArchiveProcessQueue, reqSerial);
	}

	@JmsListener(destination = "${queue.fraudArchiveProcess.destination}", concurrency = "${queue.fraudArchiveProcess.concurrency}")
	public void processQueueConsumer(String reqSerial) {
		 
		String json = fraudRedisTemplate.opsForValue().get(reqSerial);
		CallBackUserCreditModel cm = fraudService.fraudService(json);
		String callbackjson = JSON.toJSONString(cm);
		fraudRedisTemplate.opsForValue().set(reqSerial, callbackjson);
		fraudJmsMessagingTemplate.convertAndSend(fraudArchiveCallbackQueue, reqSerial);
		  
	}

	@JmsListener(destination = "${queue.fraudArchiveCallback.destination}", concurrency = "${queue.fraudArchiveCallback.concurrency}")
	public void callbackQueueConsumer(String reqSerial) {
		String content = fraudRedisTemplate.opsForValue().get(reqSerial);
		
		// 回调信贷
		fraudService.fraudCallBack(content);
		fraudRedisTemplate.delete(reqSerial);
		log.info(reqSerial + "结束处理完成，已从redis队列删除");
		 
	}

	@RequestMapping(value = "/independentAudit")
	private UserCreditReturnModel independentAudit(HttpServletRequest request) throws IOException {
		// 1.接收信贷http请求
		String requestJson = creditService.longinHttep(request);
		// 2.校验数据
		UserCreditReturnModel um = creditService.verificationCredit(requestJson);
		// 3.获取流水号
		String serialNo = creditService.flownNo(requestJson);
		// 4.返回队列(JSON,流水号)
		fraudRedisTemplate.opsForValue().set(serialNo, requestJson);
		fraudJmsMessagingTemplate.convertAndSend(fraudArchiveBufferQueue, serialNo);
		return um;
	}
}