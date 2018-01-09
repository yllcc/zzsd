package cn.com.fotic.eimp.archive;

import java.io.IOException;
import java.util.List;

import javax.jms.Queue;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import cn.com.fotic.eimp.model.CallBackCustomerScoreModel;
import cn.com.fotic.eimp.model.UserCreditContentModel;
import cn.com.fotic.eimp.model.UserCreditQueneModel;
import cn.com.fotic.eimp.model.UserCreditReturnModel;
import cn.com.fotic.eimp.service.CreditService;
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
	private Queue archiveProcessQueue;

	@Autowired
	private Queue archiveCallbackQueue;

	@Autowired
	private CreditService creditService;

	@JmsListener(destination = "${queue.archiveBuffer.destination}", concurrency = "${queue.archiveBuffer.concurrency}")
	public void bufferQueueConsumer(String reqSerial) {
		String json = redisTemplate.opsForValue().get(reqSerial);
		log.info("征信流水号:" + reqSerial + "JSON数据:" + json);
		JSONObject jsonObject = JSON.parseObject(json);
		String flowNo = jsonObject.getString("flowNo");
		String accessToken = jsonObject.getString("accessToken");
		String reqTime = jsonObject.getString("reqTime");
		if (jsonObject.containsKey("content")) {
			String value = jsonObject.getString("content");
			List<UserCreditContentModel> contentList = JSON.parseArray(value, UserCreditContentModel.class);
			for (UserCreditContentModel userCredit : contentList) {
				UserCreditQueneModel user = new UserCreditQueneModel();
				user.setBusinessNo(userCredit.getBusinessNo());
				user.setCustName(userCredit.getCustName());
				user.setIdNo(userCredit.getIdNo());
				user.setIdType(userCredit.getIdType());
				user.setPhoneNo(userCredit.getPhoneNo());
				user.setAccessToken(accessToken);
				user.setFlowNo(flowNo);
				user.setReqTime(reqTime);
				String processJson = JSON.toJSONString(user);
				String businessNo = userCredit.getBusinessNo();
				redisTemplate.opsForValue().set(businessNo, processJson);
				jmsMessagingTemplate.convertAndSend(archiveProcessQueue, businessNo);
			}
		}
		// redisTemplate.delete(reqSerial);
	}

	@JmsListener(destination = "${queue.archiveProcess.destination}", concurrency = "${queue.archiveProcess.concurrency}")
	public void processQueueConsumer(String businessNo) {

		String json = redisTemplate.opsForValue().get(businessNo);
		log.info("征信需要进行处理的单条记录:" + json);
		CallBackCustomerScoreModel cm = creditService.creditContentService(json);
		String callbackjson = JSON.toJSONString(cm);
		redisTemplate.opsForValue().set(businessNo, callbackjson);
		jmsMessagingTemplate.convertAndSend(archiveCallbackQueue, businessNo);
	}

	@JmsListener(destination = "${queue.archiveCallback.destination}", concurrency = "${queue.archiveCallback.concurrency}")
	public void callbackQueueConsumer(String businessNo) {
		String content = redisTemplate.opsForValue().get(businessNo);
		// 回调信贷
		creditService.creditCallBack(content);
		redisTemplate.delete(businessNo);
		log.info(businessNo + "征信结束处理完成，已从redis队列删除");
	}

	@RequestMapping(value = "/customerScore")
	@ResponseBody
	private UserCreditReturnModel customerScore(HttpServletRequest request) throws IOException {
		// 1.校验数据
		String requestParam = creditService.longinHttep(request);
		UserCreditReturnModel um = creditService.verificationCredit(requestParam);

		// 2.获取流水号
		String serialNo = creditService.flownNo(requestParam);
		// 3.返回队列(JSON,流水号)
		redisTemplate.opsForValue().set(serialNo, requestParam);
		jmsMessagingTemplate.convertAndSend(archiveBufferQueue, serialNo);
		return um;
	}
}