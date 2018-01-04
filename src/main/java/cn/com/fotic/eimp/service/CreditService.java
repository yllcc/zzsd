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
public class CreditService {

	private VerificationUtils vt;

	@Autowired
	private CreditRepository creditRepository;

	@Autowired
	private BankCreditRepository bankCreditRepository;

	/**
	 * 校验证件类型证件号
	 * 
	 * @param custName
	 * @param idNo
	 * @param idType
	 * @return
	 */
	public boolean VerificationService(String custName, String idNo, String idType) {
		boolean name = vt.nameValidate(custName);
		if (name == true) {
			boolean idTypeAndIdNo = vt.cardNocheck(idType, idNo);
			if (idTypeAndIdNo == true) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}

	}

	/**
	 * 调用翰迪接口，征信 1.生成xml
	 * 
	 * @param idNo
	 * @param custName
	 * @return
	 */
	public String HdCreditService(String idNo, String custName) {
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
	 * 调用翰迪接口 
	 * 加密请求数据
	 * 
	 * @return
	 * @throws Exception
	 */
	public String checkRiskSystem(String xml) throws Exception {
		String publicKey = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAIkt25i+OsoCyMhjNQ9+298iCSEzQjtStStv+gJK+rQ57DA23ie3tI/7+/845IlrqGF8U42Om6POVEIj/jNHPbkCAwEAAQ==";
		String channelId = "11009028";
		// 测试
		String URL = "http://testds.handydata.cn:8010/dataservice/service.ac";
		// 平台
		// String URL = "http://testds.handydata.cn:8011/dataservice-manage/";
		String mkey = UUID.randomUUID().toString();

		// 加密报文体格式：BASE64(商户号)| BASE64(RSA(报文加密密钥))| BASE64(3DES(报文原文))
		String strKey = RSAUtils.encryptByPublicKey(new String(mkey.getBytes(), "utf-8"), publicKey);

		String strxml = new String(
				Base64Utils.encode(ThreeDESUtils.encrypt(xml.toString().getBytes("utf-8"), mkey.getBytes())));

		String returnXml = new String(Base64Utils.encode(channelId.getBytes("utf-8"))) + "|" + strKey + "|" + strxml;
		String reutrnResult = HttpUtil.sendXMLDataByPost(URL, returnXml);
		String xmlArr[] = reutrnResult.split("\\|");

		if (xmlArr[0].equals("0")) {
			String err = new String(Base64Utils.decode(xmlArr[2]), "utf-8");
			return err;
		} else {
			byte[] b = ThreeDESUtils.decrypt(Base64Utils.decode(xmlArr[1]), mkey.getBytes());

			String tradeXml = new String(b, "utf-8");
			log.info("333" + tradeXml);
			log.info("444" + new String(Base64Utils.encode(Md5Utils.md5ToHexStr(tradeXml).getBytes("utf-8"))));// BASE64(MD5(报文))
			return tradeXml;
		}
	}

	/**
	 * xml 错误解析
	 * 
	 * @param xml
	 * @return
	 */
	public static String readRespMsg(String xml) {
		if (StringUtils.isEmpty(xml)) {
			return "未知";
		}
		try {
			Element ele = DocumentHelper.parseText(xml).getRootElement();
			return ele.elementText("respDesc");
		} catch (DocumentException e) {
			return "未知2";
		}

	}

	/**
	 * 查询人行视图
	 * 
	 * @param businessNo
	 * @param cust_name
	 * @param cert_type
	 * @param cert_num
	 */
	public boolean creditOracle(String token,String businessNo, String cust_name, String cert_type, String cert_num) {
	 
		 BankCredit um = new BankCredit();
		 try {
		
			  log.info(cust_name+cert_type+cert_num);
              try {
            	// 查询人行存在评级信息
            	BackCredit cm = creditRepository.getOptName(cust_name, cert_type, cert_num);	
				um.setSerial_No(businessNo);
				um.setCreated_time(new Date());
				um.setCreator("admin");
				um.setCert_num(cm.getCert_num());
				um.setCert_type(cm.getCert_type());
				um.setCust_name(cm.getCust_name());
				um.setRATE_CREDIT_ACCOUNT_COUNT(cm.getRate_credit_account_count());
				um.setRATE_CREDITCARD_APPROVAL_COUNT(cm.getRate_creditcard_approval_count());
				um.setRATE_CREDITREPOR_COUNT(cm.getRate_creditrepor_count());
				um.setRATE_EDU_LEVEL(cm.getRate_edu_level());
				um.setRATE_FIRSTNOACCOUNT_CARDAGE(cm.getRate_firstnoaccount_cardage());
				um.setRATE_FIVEYEAR_MAXOVERDUE_COUNT(cm.getRate_fiveyear_maxoverdue_count());
				um.setRATE_LOANOFF_LOANOPEN_RATIO(cm.getRate_loanoff_loanopen_ratio());
				um.setRATE_MARITAL_STATE(cm.getRate_marital_state());
				um.setRATE_NOACCOUNT_FIRSTEND_BAL(cm.getRate_noaccount_firstend_bal());
				um.setRATE_NORMAL_AVENOTUSEDLIMITRAT(cm.getRate_normal_avenotusedlimitrat());
				um.setRATE_RECENTLY_OPENCARD_LIMIT(cm.getRate_recently_opencard_limit());
				um.setRATE_REGISTER(cm.getRate_register());
				bankCreditRepository.save(um);
              }catch(NullPointerException e) {
            	    // 不存在评级信息
  				    // 发送翰迪http请求
                    log.info("发送翰迪http请求.......");
                    String xml=this.HdCreditService(cert_num, cust_name);
          			String HdReturn = this.checkRiskSystem(xml);
          			if(HdReturn.equals("0000")||HdReturn=="0000") {
          				//翰迪返回查询成功信息
          				this.creditCallBack(token,businessNo, "1000");
          				log.info("查询翰迪成功,业务流水号："+businessNo);
          			}else {
          				//翰迪返回查询错误信息
          				this.creditCallBack(token,businessNo, "660");
          			    log.info("查询翰迪处理失败,业务流水号："+businessNo+",翰迪返回失败原因："+HdReturn);
          			}
              }
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	/**
	 * 回调征信接口
	 * @param businessNo
	 * @param customScore
	 */
 public void creditCallBack(String token,String businessNo,String customScore) {
	 CallBackCustomerScoreModel cm=new CallBackCustomerScoreModel();
	 CallBackCustomerScoreContentModel csm=new CallBackCustomerScoreContentModel();
	 SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");// 设置日期格式
	 String sendTime = df.format(new Date());// new Date()为获取当前系统时间
	 csm.setBusinessNo(businessNo);
	 csm.setCustomScoree(customScore);
	 cm.setContent(csm);
	 cm.setPlatformNo("1234");
	 cm.setSerialNo(businessNo);
	 cm.setToken(token);
	 cm.setTxTime(sendTime);
	 

	 
	 String json=JaxbUtil.toJSon(cm);
	 log.info("回调征信接口:"+json);
	 try {
         //创建连接
         URL url = new URL("http://172.16.112.180:9090/wmxtcms/callback/custom.action");
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
