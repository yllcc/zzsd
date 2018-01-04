package cn.com.fotic.eimp.archive;

import java.io.IOException;
import java.net.URLDecoder;

import javax.jms.Queue;
import javax.servlet.http.HttpServletRequest;

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
import cn.com.fotic.eimp.model.UserCreditModel;
import cn.com.fotic.eimp.model.UserCreditReturnModel;
import cn.com.fotic.eimp.service.CreditService;
import cn.com.fotic.eimp.service.FraudService;
import cn.com.fotic.eimp.utils.JaxbUtil;
import lombok.extern.slf4j.Slf4j;


/**
 * 反欺诈接口
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
	private CreditService creditService;
	
	@Autowired
	private FraudService fraudService;
	
	@JmsListener(destination = "${queue.archiveBuffer.destination}", concurrency = "${queue.archiveBuffer.concurrency}")
	public void bufferQueueConsumer(String reqSerial) {

		String json = redisTemplate.opsForValue().get(reqSerial);
		log.info(reqSerial + ":" + json);
		UserCreditModel user = JaxbUtil.readValue(json, UserCreditModel.class);
		String businessNo = user.getContent().getBusinessNo();
		String token = user.getToken();
		// 证件类型
		String idType = user.getContent().getIdType();
		// 证件号码
		String idNo = user.getContent().getIdNo();
		// 客户名称
		String custName = user.getContent().getCustName();				
		// 1.生成xml
	    String xml = fraudService.HdFraudService(idNo, custName);
		log.info(xml);
		// 2.进行数据加密,发送数据给韩迪		
		try {
			String HdReturn = creditService.checkRiskSystem(xml);
			if(HdReturn.equals("0000")||HdReturn=="0000") {
				//韩迪返回查询成功信息
				fraudService.fraudCallBack(token,businessNo, "1000");
				log.info("查询反欺诈处理成功,业务流水号："+businessNo);
			}else {
				//韩迪返回查询错误信息
				fraudService.fraudCallBack(token,businessNo, "00");
			    log.info("查询反欺诈处理失败,业务流水号："+businessNo+",韩迪返回失败原因："+HdReturn);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}


	@JmsListener(destination = "${queue.archiveCallback.destination}", concurrency = "${queue.archiveCallback.concurrency}")
	public void callbackQueueConsumer(String reqSerial) {

		String content = redisTemplate.opsForValue().get(reqSerial);

		log.info(reqSerial + ":" + content + "003结束开始处理");

		redisTemplate.delete(reqSerial);
		log.info(reqSerial + "003结束处理完成，已从redis队列删除");
	}

	@RequestMapping(value = "/independentAudit")
	private UserCreditReturnModel independentAudit(HttpServletRequest request) throws IOException  {
        int contentLength = request.getContentLength();
         if(contentLength<0){
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
         String a=new String(buffer, "utf-8");
         log.info(a);
         String b= URLDecoder.decode(a.toString(), "utf-8");;
         log.info(b);
         
     UserCreditModel user = JaxbUtil.readValue(b, UserCreditModel.class);  
		log.info("start....");
		UserCreditReturnModel rm = new UserCreditReturnModel();
		// UserCreditModel userModel=new UserCreditModel();
		// for(UserCreditModel user:userList){
		// 信贷业务号
		String businessNo = user.getContent().getBusinessNo();
		// 证件类型
		String idType = user.getContent().getIdType();
		// 证件号码
		String idNo = user.getContent().getIdNo();
		// 客户名称
		String token = user.getToken();
		// 客户名称
		String custName = user.getContent().getCustName();
		boolean verification = creditService.VerificationService(custName, idNo, idType);
		if (verification == true) {
			String json = JaxbUtil.toJSon(user);
			redisTemplate.opsForValue().set(businessNo, json);
			jmsMessagingTemplate.convertAndSend(archiveBufferQueue,businessNo);	
				
				rm.setReCode("01");
				rm.setReDesc("成功");
				return rm;
		} else {
			rm.setReCode("02");
			rm.setReDesc("失败");
			rm.setErrorcode("001");
			rm.setErrormsg("证件号或证件类型校验失败");
			return rm;
		}
		
		// }
		// return rm;
	}

}
