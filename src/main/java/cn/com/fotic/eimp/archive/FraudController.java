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

import cn.com.fotic.eimp.model.CallBackUserCreditModel;
import cn.com.fotic.eimp.model.UserCreditContentModel;
import cn.com.fotic.eimp.model.UserCreditQueneModel;
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
		JSONObject jsonObject = JSON.parseObject(json);
		String flowno = jsonObject.getString("flowno");
		String accesstoken = jsonObject.getString("accesstoken");
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
				user.setAccesstoken(accesstoken);
				user.setFlowno(flowno);
				user.setReqTime(reqTime);
				String processJson = JSON.toJSONString(user);
				String businessNo = userCredit.getBusinessNo();
				fraudRedisTemplate.opsForValue().set(businessNo, processJson);
				fraudJmsMessagingTemplate.convertAndSend(fraudArchiveProcessQueue, businessNo);
			}
		}
		fraudRedisTemplate.delete(reqSerial);
	}

	@JmsListener(destination = "${queue.fraudArchiveProcess.destination}", concurrency = "${queue.fraudArchiveProcess.concurrency}")
	public void processQueueConsumer(String businessNo) {

		String json = fraudRedisTemplate.opsForValue().get(businessNo);
		log.info("需要进行处理的记录:" + json);
		CallBackUserCreditModel cm = fraudService.fraudContentService(json);
		String callbackjson = JSON.toJSONString(cm);
		fraudRedisTemplate.opsForValue().set(businessNo, callbackjson);
		fraudJmsMessagingTemplate.convertAndSend(fraudArchiveCallbackQueue, businessNo);

	}

	@JmsListener(destination = "${queue.fraudArchiveCallback.destination}", concurrency = "${queue.fraudArchiveCallback.concurrency}")
	public void callbackQueueConsumer(String businessNo) {
		String content = fraudRedisTemplate.opsForValue().get(businessNo);

		// 回调信贷
		fraudService.fraudCallBack(content);
		fraudRedisTemplate.delete(businessNo);
		log.info(businessNo + "结束处理完成，已从redis队列删除");

	}

	@RequestMapping(value = "/independentAudit")
	@ResponseBody
	private UserCreditReturnModel independentAudit(HttpServletRequest request) throws IOException {
		// 1.接收信贷http请求
		String requestJson = creditService.longinHttep(request);
		// 2.校验数据
		UserCreditReturnModel um = creditService.verificationCredit(requestJson);
		// 3.获取流水号
		String flowno = creditService.flownNo(requestJson);
		// 4.返回队列(JSON,流水号)
		fraudRedisTemplate.opsForValue().set(flowno, requestJson);
		fraudJmsMessagingTemplate.convertAndSend(fraudArchiveBufferQueue, flowno);
		return um;
	}
}