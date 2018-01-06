package cn.com.fotic.eimp.archive;

import java.io.IOException;
import java.net.URLDecoder;
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

import cn.com.fotic.eimp.model.HdCreditReturnModel;
import cn.com.fotic.eimp.model.JSON_TYPE;
import cn.com.fotic.eimp.model.UserCreditContentModel;
import cn.com.fotic.eimp.model.UserCreditQueneModel;
import cn.com.fotic.eimp.model.UserCreditReturnModel;
import cn.com.fotic.eimp.service.CreditService;
import cn.com.fotic.eimp.service.FraudService;
import cn.com.fotic.eimp.utils.JaxbUtil;
import cn.com.fotic.eimp.utils.JsonTypeUtil;
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
		log.info("流水号:"+reqSerial + "JSON数据:" + json);
		
		redisTemplate.opsForValue().set(reqSerial,json);
		jmsMessagingTemplate.convertAndSend(archiveProcessQueue,reqSerial);
	}

	@JmsListener(destination = "${queue.archiveProcess.destination}", concurrency = "${queue.archiveProcess.concurrency}")
	public void processQueueConsumer(String reqSerial) {
		String json = redisTemplate.opsForValue().get(reqSerial);
		log.info("22流水号:"+reqSerial + "22-----JSON数据:" + json);
		
		JSONObject jsonObject = JSON.parseObject(json);
		String token = jsonObject.getString("token");
		String serialNo=jsonObject.getString("serialNo");
		// 判断是否存在content
		if (jsonObject.containsKey("content")) {
			String value = jsonObject.getString("content");
			log.info(value);
			JSON_TYPE jsonType = JsonTypeUtil.getJsonType(value);
			// 判断是否为content数组
			if (JSON_TYPE.JSON_TYPE_ARRAY.equals(jsonType)) {						
	         List<UserCreditContentModel> contentList = JSON.parseArray(value, UserCreditContentModel.class);
				for (UserCreditContentModel user : contentList) {
					String businessNo = user.getBusinessNo();
					String idNo = user.getIdNo();					
					String custName = user.getCustName();
					// 1.生成xml
					String xml = fraudService.HdFraudService(idNo, custName);
					log.info(xml);
					// 2.进行数据加密,发送数据给韩迪
					try {
						HdCreditReturnModel r = creditService.checkRiskSystem(xml);
						
						
						
						
						if (r.getResCode().equals("0000") ) {
							// 韩迪返回查询成功信息
							fraudService.fraudCallBack(token, businessNo, r.getData().get(0).getScore(),custName);
							log.info("查询反欺诈处理成功,业务流水号：" + businessNo);
						} else {
							// 韩迪返回查询错误信息
							fraudService.fraudCallBack(token, businessNo, "00",custName);
							log.info("查询反欺诈处理失败,业务流水号：" + businessNo + ",韩迪返回失败原因：" + r.getResCode()+r.getResMsg());
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					redisTemplate.opsForValue().set(businessNo, json);
					jmsMessagingTemplate.convertAndSend(archiveCallbackQueue, businessNo);
					redisTemplate.delete(reqSerial);
					log.info(reqSerial + "处理完成，已从redis队列删除");
				}	
		}
				}else {
					//TODO
				}
	
	}
	@JmsListener(destination = "${queue.archiveCallback.destination}", concurrency = "${queue.archiveCallback.concurrency}")
	public void callbackQueueConsumer(String reqSerial) {
		String content = redisTemplate.opsForValue().get(reqSerial);
		log.info(reqSerial + ":" + content + "结束开始处理");
		redisTemplate.delete(reqSerial);
		log.info(reqSerial + "结束处理完成，已从redis队列删除");
	}

	@RequestMapping(value = "/independentAudit")
	private UserCreditReturnModel independentAudit(HttpServletRequest request) throws IOException {
		       //1.接受http请求
				String b=creditService.longinHttep(request);
				//2.校验数据
				UserCreditReturnModel um =creditService.verificationCredit(b);
				//3.获取流水号
				String serialNo=creditService.flownNo(b);
				//4.返回队列(JSON,流水号)
				redisTemplate.opsForValue().set(serialNo, b);
				jmsMessagingTemplate.convertAndSend(archiveBufferQueue, serialNo);
				return um;
	}
}