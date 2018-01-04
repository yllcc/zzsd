package cn.com.fotic.eimp.archive;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.jms.Queue;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
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

import com.alibaba.fastjson.JSON;

import cn.com.fotic.eimp.model.CustomerScoreModel;
import cn.com.fotic.eimp.model.CustomerScoreReturnModel;
import cn.com.fotic.eimp.model.UserCreditModel;
import cn.com.fotic.eimp.model.UserCreditReturnModel;
import cn.com.fotic.eimp.service.CreditService;
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
	private CreditService creditstartService;

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
		
		String token=user.getToken();
		// 查询征信数据库
		boolean a=creditstartService.creditOracle(token,businessNo, custName, idType, idNo);
		if(a==true) {	
			log.info("查询征信处理成功,业务流水号："+businessNo);
		}else {
			log.info("失败.....,业务流水号："+businessNo);
		}
		

	}

	@JmsListener(destination = "${queue.archiveCallback.destination}", concurrency = "${queue.archiveCallback.concurrency}")
	public void callbackQueueConsumer(String reqSerial) {

		String content = redisTemplate.opsForValue().get(reqSerial);

		log.info(reqSerial + ":" + content + "003结束开始处理");

		redisTemplate.delete(reqSerial);
		log.info(reqSerial + "003结束处理完成，已从redis队列删除");
	}

	
	/*public UserCreditReturnModel CustomerScore(HttpServletRequest request,String user) throws Exception {
		request.getServletContext().getAttribute(user);
		StringBuffer json = new StringBuffer();
		InputStream inputStream = request.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
		  StringBuffer sb = new StringBuffer("");
		  String lines;
          while ((lines = reader.readLine()) != null) {
              lines = new String(lines.getBytes(), "utf-8");
              sb.append(lines);
          }
          String sss = URLDecoder.decode(sb.toString(), "utf-8");
          System.out.println(sss);*/
          
	@RequestMapping(value = "/customerScore")
          private UserCreditReturnModel customerScore(HttpServletRequest request) throws IOException  {
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
		log.info("start...."+user);
		CustomerScoreReturnModel rm = new CustomerScoreReturnModel();
		UserCreditReturnModel um = new UserCreditReturnModel();
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
		boolean verification = creditstartService.VerificationService(custName, idNo, idType);
		if (verification == true) {
			log.info("调用征信......");
			log.info(businessNo + ":开始处理");
			String jsonUser = JaxbUtil.toJSon(user);
			redisTemplate.opsForValue().set(businessNo, jsonUser);
			jmsMessagingTemplate.convertAndSend(archiveBufferQueue,businessNo);		
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
