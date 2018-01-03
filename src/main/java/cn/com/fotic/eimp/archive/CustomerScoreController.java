package cn.com.fotic.eimp.archive;

import javax.jms.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import cn.com.fotic.eimp.model.CustomerScoreModel;
import cn.com.fotic.eimp.model.CustomerScoreReturnModel;
import cn.com.fotic.eimp.model.UserCreditModel;
import cn.com.fotic.eimp.model.UserCreditReturnModel;
import cn.com.fotic.eimp.service.CreditstartService;
import cn.com.fotic.eimp.utils.JaxbUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 客户评分接口
 * 
 * @author liugj
 *
 */

@Controller
@Slf4j
@RestController
@RequestMapping("/")
public class CustomerScoreController {

	@Autowired
	private JmsMessagingTemplate jmsMessagingTemplate;

	@Autowired
	private StringRedisTemplate redisTemplate;

	@Autowired
	private Queue archiveBufferQueue;


	@Autowired
	private CreditstartService creditstartService;

	@JmsListener(destination = "${queue.archiveBuffer.destination}", concurrency = "${queue.archiveBuffer.concurrency}")
	public void bufferQueueConsumer(String reqSerial) {

		String json = redisTemplate.opsForValue().get(reqSerial);
		log.info(reqSerial + ":" + json);
		UserCreditModel user = JaxbUtil.readValue(json, UserCreditModel.class);
		String businessNo = user.getContent().getBusinessNo();
		// String token = user.getToken();
		// 证件类型
		String idType = user.getContent().getIdType();
		// 证件号码
		String idNo = user.getContent().getIdNo();
		// 客户名称
		String custName = user.getContent().getCustName();
		// 查询征信数据库
		creditstartService.creditOracle(businessNo, custName, idType, idNo);
		log.info(businessNo);

	}

	@JmsListener(destination = "${queue.archiveCallback.destination}", concurrency = "${queue.archiveCallback.concurrency}")
	public void callbackQueueConsumer(String reqSerial) {

		String content = redisTemplate.opsForValue().get(reqSerial);

		log.info(reqSerial + ":" + content + "003结束开始处理");

		redisTemplate.delete(reqSerial);
		log.info(reqSerial + "003结束处理完成，已从redis队列删除");
	}

	@RequestMapping(value = "/customerScore", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public UserCreditReturnModel CustomerScore(@RequestBody CustomerScoreModel user) throws Exception {
		log.info("start....");
		CustomerScoreReturnModel rm = new CustomerScoreReturnModel();
		UserCreditReturnModel um = new UserCreditReturnModel();
		// UserCreditModel userModel=new UserCreditModel();
		// for(UserCreditModel user:userList){
		// 信贷业务号
		String appId = user.getContent().getBusinessNo();
		// 证件类型
		String idType = user.getContent().getIdType();
		// 证件号码
		String idNo = user.getContent().getIdNo();
		// 客户名称
		String token = user.getToken();
		// 客户名称
		String custName = user.getContent().getCustName();
		boolean verification = creditstartService.VerificationService(custName, idNo, idType);
		if (verification == true) {
			log.info("调用征信......");
			log.info(appId + ":开始处理");
			String json = JaxbUtil.toJSon(user);
			redisTemplate.opsForValue().set(appId, json);
			jmsMessagingTemplate.convertAndSend(archiveBufferQueue, appId);
			/*
			 * rm.setBusinessNo(appId); rm.setFraudScore("1000");
			 */
			um.setReCode("01");
			um.setReDesc("成功");
			return um;
		} else {
			um.setErrorcode("001");
			um.setErrormsg("证件号或证件类型校验失败");
			return um;
		}
	}

}
