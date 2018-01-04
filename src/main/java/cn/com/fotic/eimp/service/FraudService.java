package cn.com.fotic.eimp.service;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import cn.com.fotic.eimp.model.CallBackCustomerScoreContentModel;
import cn.com.fotic.eimp.model.CallBackCustomerScoreModel;
import cn.com.fotic.eimp.model.CallBackUserCreditContentModel;
import cn.com.fotic.eimp.model.CallBackUserCreditModel;
import cn.com.fotic.eimp.model.HdAntiFraudModel;
import cn.com.fotic.eimp.repository.BankCreditRepository;
import cn.com.fotic.eimp.repository.CreditRepository;
import cn.com.fotic.eimp.repository.entity.BackCredit;
import cn.com.fotic.eimp.repository.entity.BankCredit;
import cn.com.fotic.eimp.utils.Base64Utils;
import cn.com.fotic.eimp.utils.HttpUtil;
import cn.com.fotic.eimp.utils.JaxbUtil;
import cn.com.fotic.eimp.utils.Md5Utils;
import cn.com.fotic.eimp.utils.RSAUtils;
import cn.com.fotic.eimp.utils.ThreeDESUtils;
import cn.com.fotic.eimp.utils.VerificationUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author liugj
 *
 */
@Slf4j
@Service
public class FraudService {
	/**
	 * 调用翰迪接口,反欺诈 1.生成xml
	 * 
	 * @param idNo
	 * @param custName
	 * @return
	 */
	public String HdFraudService(String idNo, String custName) {
		HdAntiFraudModel hd = new HdAntiFraudModel();
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");// 设置日期格式
		String sendTime = df.format(new Date());// new Date()为获取当前系统时间
		hd.setSendTime(sendTime);
		hd.setTransCode("100101");
		hd.setVersion("1.0.0");
		hd.setApplication("GwBiz.Req");
		hd.setCertNo(idNo);
		hd.setChannelId("11009028");
		hd.setChannelOrderId(sendTime);
		hd.setIp("");
		hd.setLinkedMerchantId("2088621466375255");
		hd.setMobile("");
		hd.setName(custName);
		hd.setOpenId("");
		hd.setEmail("");
		hd.setImei("");
		hd.setAddress("");
		hd.setAddress("");
		hd.setBankCard("");
		hd.setProductItemCode("100108");
		hd.setWifiMac("");
		hd.setMac("");
		String xmlReq = JaxbUtil.convertToXml(hd);
		return xmlReq;

	}
	
	/**
	 * 回调反欺诈借口
	 * @param businessNo
	 * @param fraudScore
	 */
   public void fraudCallBack(String token,String businessNo,String fraudScore) {
	   CallBackUserCreditModel cum=new CallBackUserCreditModel();
	   CallBackUserCreditContentModel cm=new CallBackUserCreditContentModel();
	   SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");// 设置日期格式
		String sendTime = df.format(new Date());// new Date()为获取当前系统时间
	   cm.setBusinessNo(businessNo);
	   cm.setFraudScore(fraudScore);
	   cum.setContent(cm);
	   cum.setPlatformNo("0002");
	   cum.setSerialNo(businessNo);
	   cum.setToken(token);
	   cum.setTxTime(sendTime);
	   String json=JaxbUtil.toJSon(cum);
	   log.info("回调反欺诈接口"+json);
	   try {
           //创建连接
           URL url = new URL("http://172.16.112.180:9090/wmxtcms/callback/fraud.action");
           HttpURLConnection connection = (HttpURLConnection) url.openConnection();
           connection.setDoOutput(true);
           connection.setDoInput(true);
           connection.setRequestMethod("POST");
           connection.setUseCaches(false);
           connection.setInstanceFollowRedirects(true);
           connection.setRequestProperty("Content-Type","application/json");
           connection.connect();

           //POST请求
           DataOutputStream out = new DataOutputStream(connection.getOutputStream());
           
           String str = URLEncoder.encode(json, "utf-8");
           out.writeBytes(str);
           out.flush();
           out.close();

           //读取响应
           BufferedReader reader = new BufferedReader(new InputStreamReader(
                   connection.getInputStream()));
           String lines;
           StringBuffer sb = new StringBuffer("");
           while ((lines = reader.readLine()) != null) {
               lines = new String(lines.getBytes(), "utf-8");
               sb.append(lines);
           }
           String sss = URLDecoder.decode(sb.toString(), "utf-8");
           System.out.println(sss);
           reader.close();
           // 断开连接
           connection.disconnect();
       } catch (Exception e) {
           e.printStackTrace();
       }
	   
   }

	
	
}
