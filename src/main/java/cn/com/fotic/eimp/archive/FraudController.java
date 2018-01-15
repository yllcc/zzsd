package cn.com.fotic.eimp.archive;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	public void bufferQueueConsumer(String flowno) {
		String json = fraudRedisTemplate.opsForValue().get(flowno);
		log.info(flowno + "反欺诈进入JSON数据,第一步:" + json);
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
				String businessNo = "fraud" + userCredit.getBusinessNo();
				fraudRedisTemplate.opsForValue().set(businessNo, processJson);
				fraudJmsMessagingTemplate.convertAndSend(fraudArchiveProcessQueue, businessNo);
			}
			fraudRedisTemplate.delete(flowno);
		}

	}

	@JmsListener(destination = "${queue.fraudArchiveProcess.destination}", concurrency = "${queue.fraudArchiveProcess.concurrency}")
	public void processQueueConsumer(String businessNo) {

		String json = fraudRedisTemplate.opsForValue().get(businessNo);
		log.info(businessNo + "反欺诈处理的单条记录,第二步" + json);
		CallBackUserCreditModel cm = fraudService.fraudContentService(json);
		String callbackjson = JSON.toJSONString(cm);
		fraudRedisTemplate.opsForValue().set(businessNo, callbackjson);
		fraudJmsMessagingTemplate.convertAndSend(fraudArchiveCallbackQueue, businessNo);

	}

	@JmsListener(destination = "${queue.fraudArchiveCallback.destination}", concurrency = "${queue.fraudArchiveCallback.concurrency}")
	public void callbackQueueConsumer(String businessNo) {
		String content = fraudRedisTemplate.opsForValue().get(businessNo);
		// 回调信贷
		log.info(businessNo + "：反欺诈处理回调，第三步：" + content);
		boolean a = false;
		for (int i = 0; i < 3; i++) {
			a = fraudService.fraudCallBack(content);
			if (a == true) {
				fraudRedisTemplate.delete(businessNo);
				log.info(businessNo + "征信结束处理完成，已从redis队列删除。请求成功");
				break;
			} else {
				a = false;
			}
		}
	}

	@RequestMapping(value = "/independentAudit")
	@ResponseBody
	private UserCreditReturnModel independentAudit(HttpServletRequest request) throws IOException {
		// 1.校验数据
		String requestJson = creditService.longinHttep(request);
		UserCreditReturnModel um = creditService.verificationCredit(requestJson);
		if ("01" == um.getReCode()) {
			// 2.获取流水号
			String flowno = creditService.flownNo(requestJson);
			// 3.返回队列(JSON,流水号)
			fraudRedisTemplate.opsForValue().set(flowno, requestJson);
			fraudJmsMessagingTemplate.convertAndSend(fraudArchiveBufferQueue, flowno);
		}
		return um;
	}
}