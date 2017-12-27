package cn.com.fotic.eimp.archive;

import javax.jms.Queue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ch.qos.logback.classic.Logger;
import cn.com.fotic.eimp.repository.model.UserCreditModel;
import cn.com.fotic.eimp.repository.model.UserCreditReturnModel;
import cn.com.fotic.eimp.utils.VerificationUtils;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/")
public class CreditController {

	private VerificationUtils vt;

	/*
	 * @Autowired private JmsMessagingTemplate jmsMessagingTemplate;
	 * 
	 * @Autowired private StringRedisTemplate redisTemplate;
	 * 
	 * @Autowired private Queue archiveProcessQueue;
	 * 
	 * @Autowired private Queue archiveCallbackQueue;
	 */

	/*
	 * @JmsListener(destination = "${queue.archiveBuffer.destination}", concurrency
	 * = "${queue.archiveBuffer.concurrency}") public void
	 * bufferQueueConsumer(String reqSerial) {
	 * 
	 * String content = redisTemplate.opsForValue().get(reqSerial);
	 * 
	 * log.info(reqSerial+":"+content+"开始处理");
	 * 
	 * redisTemplate.opsForValue().set("10000002","测试2");
	 * jmsMessagingTemplate.convertAndSend(archiveProcessQueue, "10000002");
	 * 
	 * redisTemplate.delete(reqSerial); log.info(reqSerial+"处理完成，已从redis队列删除"); }
	 * 
	 * @JmsListener(destination = "${queue.archiveProcess.destination}", concurrency
	 * = "${queue.archiveProcess.concurrency}") public void
	 * processQueueConsumer(String reqSerial) {
	 * 
	 * String content = redisTemplate.opsForValue().get(reqSerial);
	 * 
	 * log.info(reqSerial+":"+content+"开始处理");
	 * 
	 * redisTemplate.opsForValue().set("10000003","测试3");
	 * jmsMessagingTemplate.convertAndSend(archiveCallbackQueue, "10000003");
	 * 
	 * redisTemplate.delete(reqSerial); log.info(reqSerial+"处理完成，已从redis队列删除");
	 * 
	 * }
	 * 
	 * @JmsListener(destination = "${queue.archiveCallback.destination}",
	 * concurrency = "${queue.archiveCallback.concurrency}") public void
	 * callbackQueueConsumer(String reqSerial) {
	 * 
	 * String content = redisTemplate.opsForValue().get(reqSerial);
	 * 
	 * //log.info(reqSerial+":"+content+"开始处理");
	 * 
	 * redisTemplate.delete(reqSerial); log.info(reqSerial+"处理完成，已从redis队列删除"); }
	 */
	@RequestMapping(value = "/creditstart")
	public UserCreditReturnModel callcredit(UserCreditModel user) {
		UserCreditReturnModel rm = new UserCreditReturnModel();
		// 信贷业务号
		String appId = user.getAppId();
		// 01-反欺诈02-征信
		String callType = user.getCallType();
		// 证件类型
		String idType = user.getIdType();
		// 证件号码
		String idNo = user.getIdNo();
		// 客户名称
		String custName = user.getCustName();
		boolean name = vt.nameValidate(custName);
		if (name == true) {
			boolean idTypeAndIdNo = vt.cardNocheck(idType, idNo);
			if (idTypeAndIdNo == true && name == true) {
				if (callType == "01") {
					// 01-反欺诈
				} else if (callType == "01") {
					// 02-征信
				} else {
					// 其他类型
				}
				rm.setReCode("01");
				rm.setReDesc("成功");
				return rm;

			} else {
				rm.setReCode("02");
				rm.setReDesc("失败");
				rm.setErrorcode("001");
				rm.setErrormsg("证件号或证件类型验证失败");
				return rm;
			}
		} else {
			rm.setReCode("02");
			rm.setReDesc("失败");
			rm.setErrorcode("001");
			rm.setErrormsg("证件号或证件类型验证失败");
			return rm;
		}

	}

}
	
