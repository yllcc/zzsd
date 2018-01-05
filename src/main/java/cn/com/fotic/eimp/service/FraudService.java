package cn.com.fotic.eimp.service;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;

import cn.com.fotic.eimp.model.CallBackUserCreditContentModel;
import cn.com.fotic.eimp.model.CallBackUserCreditModel;
import cn.com.fotic.eimp.model.HdAntiFraudModel;
import cn.com.fotic.eimp.utils.JaxbUtil;
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
		hd.setProductItemCode("100102");
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
   public void fraudCallBack(String token,String businessNo,String fraudScore,String custName) {
	   CallBackUserCreditModel cm=new CallBackUserCreditModel();
		List<CallBackUserCreditContentModel> csm = new ArrayList<CallBackUserCreditContentModel>();
	   SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");// 设置日期格式
		String sendTime = df.format(new Date());// new Date()为获取当前系统时间
		CallBackUserCreditContentModel a=new CallBackUserCreditContentModel();
		a.setBusinessNo(businessNo);
		a.setFraudScore(fraudScore);
		csm.add(a);
		cm.setContent(csm);
        cm.setContent(csm);
		cm.setPlatformNo(custName);
		cm.setSerialNo(businessNo);
		cm.setToken(token);
		cm.setTxTime(sendTime);

	   String json = JSON.toJSONString(cm);
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
