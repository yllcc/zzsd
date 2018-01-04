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
import cn.com.fotic.eimp.model.UserCreditContentModel;
import cn.com.fotic.eimp.model.UserCreditQueneModel;
import cn.com.fotic.eimp.model.UserCreditReturnModel;
import cn.com.fotic.eimp.service.CreditService;
import cn.com.fotic.eimp.utils.JaxbUtil;
import cn.com.fotic.eimp.utils.JsonTypeUtil;
import lombok.extern.slf4j.Slf4j;
import cn.com.fotic.eimp.model.JSON_TYPE;

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
	private CreditService creditstartService;

	@JmsListener(destination = "${queue.archiveBuffer.destination}", concurrency = "${queue.archiveBuffer.concurrency}")
	public void bufferQueueConsumer(String reqSerial) {

		String json = redisTemplate.opsForValue().get(reqSerial);
		log.info(reqSerial + ":" + json);
		UserCreditQueneModel user = JaxbUtil.readValue(json, UserCreditQueneModel.class);

		// 客户名称
		String businessNo = user.getBusinessNo();
		// 证件类型
		String idType = user.getIdType();
		// 证件号码
		String idNo = user.getIdNo();

		// 客户名称
		String custName = user.getCustName();
		String token = user.getToken();
		// 查询征信数据库
		boolean a = creditstartService.creditOracle(token, businessNo, custName, idType, idNo);
		if (a == true) {
			log.info("查询征信处理成功,业务流水号：" + businessNo);
		} else {
			log.info("失败.....,业务流水号：" + businessNo);
		}

	}

	@JmsListener(destination = "${queue.archiveCallback.destination}", concurrency = "${queue.archiveCallback.concurrency}")
	public void callbackQueueConsumer(String reqSerial) {

		String content = redisTemplate.opsForValue().get(reqSerial);

		log.info(reqSerial + ":" + content + "003结束开始处理");

		redisTemplate.delete(reqSerial);
		log.info(reqSerial + "003结束处理完成，已从redis队列删除");
	}

	@RequestMapping(value = "/customerScore")
	private UserCreditReturnModel customerScore(HttpServletRequest request) throws IOException {
		UserCreditReturnModel um = new UserCreditReturnModel();
		int contentLength = request.getContentLength();
		if (contentLength < 0) {
			return null;
		}
		byte buffer[] = new byte[contentLength];
		for (int i = 0; i < contentLength;) {
			int len = request.getInputStream().read(buffer, i, contentLength - i);
			if (len == -1) {
				break;
			}
			i += len;
		}
		String a = new String(buffer, "utf-8");
		String b = URLDecoder.decode(a.toString(), "utf-8");
		log.info(b);
		JSONObject jsonObject = JSON.parseObject(b);
		String token = jsonObject.getString("token");
		// 判断是否存在content
		if (jsonObject.containsKey("content")) {
			String value = jsonObject.getString("content");
			JSON_TYPE jsonType = JsonTypeUtil.getJsonType(value);
			// 判断是否为content数组
			if (JSON_TYPE.JSON_TYPE_ARRAY.equals(jsonType)) {
				List<UserCreditContentModel> contentList = JSON.parseArray(value, UserCreditContentModel.class);
				for (UserCreditContentModel user : contentList) {
					// 信贷业务号
					String businessNo = user.getBusinessNo();
					// 证件类型
					String idType = user.getIdType();
					// 证件号码
					String idNo = user.getIdNo();
					// 客户名称
					String custName = user.getCustName();
					boolean verification = creditstartService.VerificationService(custName, idNo, idType);
					if (verification == true) {
						log.info("调用征信......");
						log.info(businessNo + ":开始处理");
						UserCreditQueneModel userQuene = new UserCreditQueneModel();
						userQuene.setBusinessNo(businessNo);
						userQuene.setCustName(custName);
						userQuene.setIdNo(idNo);
						userQuene.setIdType(idType);
						userQuene.setToken(token);
						String jsonUser = JaxbUtil.toJSon(userQuene);
						redisTemplate.opsForValue().set(businessNo, jsonUser);
						jmsMessagingTemplate.convertAndSend(archiveBufferQueue, businessNo);
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
		}
		return um;
	}
}