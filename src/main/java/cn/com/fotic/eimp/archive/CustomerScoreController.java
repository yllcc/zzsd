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
	private JmsMessagingTemplate creditJmsMessagingTemplate;

	@Autowired
	private StringRedisTemplate creditRedisTemplate;

	@Autowired
	private Queue creditArchiveBufferQueue;

	@Autowired
	private Queue creditArchiveProcessQueue;

	@Autowired
	private Queue creditArchiveCallbackQueue;

	@Autowired
	private CreditService creditService;

	@JmsListener(destination = "${queue.creditArchiveBuffer.destination}", concurrency = "${queue.creditArchiveBuffer.concurrency}")
	public void bufferQueueConsumer(String flownNo) {
		String json = creditRedisTemplate.opsForValue().get(flownNo);
		log.info(flownNo + "征信流水号JSON数据,第一步:" + json);
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
				String businessNo = "credit" + userCredit.getBusinessNo();
				creditRedisTemplate.opsForValue().set(businessNo, processJson);
				creditJmsMessagingTemplate.convertAndSend(creditArchiveProcessQueue, businessNo);
			}
			creditRedisTemplate.delete(flownNo);
		}
	}

	@JmsListener(destination = "${queue.creditArchiveProcess.destination}", concurrency = "${queue.creditArchiveProcess.concurrency}")
	public void processQueueConsumer(String businessNo) {

		String json = creditRedisTemplate.opsForValue().get(businessNo);
		log.info(businessNo + "：征信处理单条记录,第二步：" + json);
		CallBackCustomerScoreModel cm = creditService.creditContentService(json);
		String callbackjson = JSON.toJSONString(cm);
		creditRedisTemplate.opsForValue().set(businessNo, callbackjson);
		creditJmsMessagingTemplate.convertAndSend(creditArchiveCallbackQueue, businessNo);

	}

	@JmsListener(destination = "${queue.creditArchiveCallback.destination}", concurrency = "${queue.creditArchiveCallback.concurrency}")
	public void callbackQueueConsumer(String businessNo) {
		String content = creditRedisTemplate.opsForValue().get(businessNo);
		log.info(businessNo + "：征信业务处理回调，第三步：" + content);
		// 回调信贷
		boolean a = false;
		for (int i = 0; i < 3; i++) {
			a = creditService.creditCallBack(content);
			if (a == true) {
				creditRedisTemplate.delete(businessNo);
				log.info(businessNo + "征信结束处理完成，已从redis队列删除。请求成功");
				break;
			} else {
				a = false;
			}
		}
	}

	@RequestMapping(value = "/customerScore")
	@ResponseBody
	private UserCreditReturnModel customerScore(HttpServletRequest request) throws IOException {
		// 1.校验数据
		String requestParam = creditService.longinHttep(request);
		UserCreditReturnModel um = creditService.verificationCredit(requestParam);
		// 2.获取流水号
		String flownNo = creditService.flownNo(requestParam);
		// 3.返回队列(JSON,流水号)
		creditRedisTemplate.opsForValue().set(flownNo, requestParam);
		creditJmsMessagingTemplate.convertAndSend(creditArchiveBufferQueue, flownNo);
		return um;
	}
}