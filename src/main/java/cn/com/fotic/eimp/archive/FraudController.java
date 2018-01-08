package cn.com.fotic.eimp.archive;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
import com.alibaba.fastjson.JSONObject;
import cn.com.fotic.eimp.model.CallBackUserCreditContentModel;
import cn.com.fotic.eimp.model.CallBackUserCreditModel;
import cn.com.fotic.eimp.model.HdCreditReturnModel;
import cn.com.fotic.eimp.model.UserCreditContentModel;
import cn.com.fotic.eimp.model.UserCreditReturnModel;
import cn.com.fotic.eimp.repository.entity.CreditPersonalDic;
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
	private JmsMessagingTemplate jmsMessagingTemplate;

	@Autowired
	private StringRedisTemplate redisTemplate;

	@Autowired
	private Queue archiveBufferQueue;

	@Autowired
	private Queue archiveProcessQueue;

	@Autowired
	private Queue archiveCallbackQueue;

	@Autowired
	private CreditService creditService;

	@Autowired
	private FraudService fraudService;

	@JmsListener(destination = "${queue.archiveBuffer.destination}", concurrency = "${queue.archiveBuffer.concurrency}")
	public void bufferQueueConsumer(String reqSerial) {

		String json = redisTemplate.opsForValue().get(reqSerial);
		log.info("流水号:" + reqSerial + "JSON数据:" + json);

		redisTemplate.opsForValue().set(reqSerial, json);
		jmsMessagingTemplate.convertAndSend(archiveProcessQueue, reqSerial);
	}

	@JmsListener(destination = "${queue.archiveProcess.destination}", concurrency = "${queue.archiveProcess.concurrency}")
	public void processQueueConsumer(String reqSerial) {
		
		String json = redisTemplate.opsForValue().get(reqSerial);
		CallBackUserCreditModel cm = fraudService.fraudService(json);
		log.info("22流水号:" + reqSerial + "22-----JSON数据:" + json);
		String callbackjson = JSON.toJSONString(cm);
		redisTemplate.opsForValue().set(reqSerial, callbackjson);
		jmsMessagingTemplate.convertAndSend(archiveCallbackQueue, reqSerial);
	}

	@JmsListener(destination = "${queue.archiveCallback.destination}", concurrency = "${queue.archiveCallback.concurrency}")
	public void callbackQueueConsumer(String reqSerial) {
		String content = redisTemplate.opsForValue().get(reqSerial);

		// 回调信贷
		fraudService.fraudCallBack(content);
		redisTemplate.delete(reqSerial);
		log.info(reqSerial + "结束处理完成，已从redis队列删除");
	}

	@RequestMapping(value = "/independentAudit")
	private UserCreditReturnModel independentAudit(HttpServletRequest request) throws IOException {
		// 1.接收信贷http请求
		String b = creditService.longinHttep(request);
		// 2.校验数据
		UserCreditReturnModel um = creditService.verificationCredit(b);
		// 3.获取流水号
		String serialNo = creditService.flownNo(b);
		// 4.返回队列(JSON,流水号)
		redisTemplate.opsForValue().set(serialNo, b);
		jmsMessagingTemplate.convertAndSend(archiveBufferQueue, serialNo);
		return um;
	}
}