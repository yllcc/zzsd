package cn.com.fotic.eimp.service;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.com.fotic.eimp.model.CallBackCustomerScoreContentModel;
import cn.com.fotic.eimp.model.CallBackCustomerScoreModel;
import cn.com.fotic.eimp.model.HdAntiFraudModel;
import cn.com.fotic.eimp.model.HdCreditReturnContentModel;
import cn.com.fotic.eimp.model.HdCreditReturnModel;
import cn.com.fotic.eimp.model.HdCreditScoreModel;
import cn.com.fotic.eimp.model.UserCreditContentModel;
import cn.com.fotic.eimp.model.UserCreditQueneModel;
import cn.com.fotic.eimp.model.UserCreditReturnModel;
import cn.com.fotic.eimp.repository.BankCreditRepository;
import cn.com.fotic.eimp.repository.CreditRepository;
import cn.com.fotic.eimp.repository.entity.BackCredit;
import cn.com.fotic.eimp.repository.entity.BankCredit;
import cn.com.fotic.eimp.utils.Base64Utils;
import cn.com.fotic.eimp.utils.HttpUtil;
import cn.com.fotic.eimp.utils.JaxbUtil;
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

	@Autowired
	private CreditRepository creditRepository;

	@Autowired
	private BankCreditRepository bankCreditRepository;

	@Autowired
	private CreditPersonalService creditPersonalService;

	/**
	 * 1.接收转换成json
	 * 
	 * @param request
	 * @return
	 * @throws IOException
	 */
	public String longinHttep(HttpServletRequest request) throws IOException {
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
		return b;

	}

	/**
	 * 校验证件类型证件号
	 * 
	 * @param custName
	 * @param idNo
	 * @param idType
	 * @return
	 */
	public boolean VerificationService(String custName, String idNo, String idType) {
		boolean name = VerificationUtils.nameValidate(custName);
		if (name == true) {
			boolean idTypeAndIdNo = VerificationUtils.cardNocheck(idType, idNo);
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
	 * 接受数据返回给上游
	 * @param json
	 * @return
	 */
	public UserCreditReturnModel verificationCredit(String json) {
		UserCreditReturnModel um = new UserCreditReturnModel();
		JSONObject jsonObject = JSON.parseObject(json);
		// 判断是否存在content
		if (jsonObject.containsKey("content")) {
			String value = jsonObject.getString("content");
			List<UserCreditContentModel> contentList = JSON.parseArray(value, UserCreditContentModel.class);
			for (UserCreditContentModel user : contentList) {
				String idType = user.getIdType();
				String idNo = user.getIdNo();
				String custName = user.getCustName();
				boolean verification = this.VerificationService(custName, idNo, idType);
				if (verification == true) {
					um.setReCode("01");
					um.setReDesc("成功");
					return um;
				} else {
					um.setReCode("02");
					um.setReDesc("证件号或证件类型校验失败");
					return um;
				}
			}
		} else {
			um.setReCode("03");
			um.setReDesc("上送的格式不对，不是一个数组");
			return um;
		}
		return um;
	}
/**
 * 获取这批次流水号
 * @param json
 * @return
 */
	public String flownNo(String json) {

		JSONObject jsonObject = JSON.parseObject(json);
		String serialNo = jsonObject.getString("serialNo");
		return serialNo;

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
		hd.setProductItemCode("100108");
		hd.setWifiMac("");
		hd.setMac("");
		String xmlReq = JaxbUtil.convertToXml(hd);
		return xmlReq;

	}

	/**
	 * 调用翰迪接口 加密请求数据
	 * 
	 * @return
	 * @throws Exception
	 */
	public HdCreditReturnModel checkRiskSystem(String xml) throws Exception {
		HdCreditReturnModel hrm = new HdCreditReturnModel();
		// 测试
		// String URL = "http://testds.handydata.cn:8010/dataservice/service.ac";
		// String publicKey =
		// "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAIkt25i+OsoCyMhjNQ9+298iCSEzQjtStStv+gJK+rQ57DA23ie3tI/7+/845IlrqGF8U42Om6POVEIj/jNHPbkCAwEAAQ==";
		// String channelId = "11009028";
		// 生产
		String URL = "https://ds.handydata.cn/ds/service.ac";
		String publicKey = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAIGm7bNehbstkfG/fAcZnrA3UGHVWjCRkP3S3/ZfF456ypdRAaEeqGILT+wB139K1HZIUy/Gl8slGS9r6TR961MCAwEAAQ==";
		String channelId = "11009035";
		String mkey = UUID.randomUUID().toString();
		// 加密报文体格式：BASE64(商户号)| BASE64(RSA(报文加密密钥))| BASE64(3DES(报文原文))
		String strKey = RSAUtils.encryptByPublicKey(new String(mkey.getBytes(), "utf-8"), publicKey);
		String strxml = new String(Base64Utils.encode(ThreeDESUtils.encrypt(xml.toString().getBytes("utf-8"), mkey.getBytes())));
		String returnXml = new String(Base64Utils.encode(channelId.getBytes("utf-8"))) + "|" + strKey + "|" + strxml;
		String reutrnResult = HttpUtil.sendXMLDataByPost(URL, returnXml);
		String xmlArr[] = reutrnResult.split("\\|");
		if (xmlArr[0].equals("0")) {
			String resMsg = new String(Base64Utils.decode(xmlArr[2]), "utf-8");
			hrm.setResMsg(resMsg);
			return hrm;
		} else {
			byte[] b = ThreeDESUtils.decrypt(Base64Utils.decode(xmlArr[1]), mkey.getBytes());

			String tradeXml = new String(b, "utf-8");
			log.info("333" + tradeXml);
			JSONObject jsonObject = JSON.parseObject(tradeXml);
			String resCode = jsonObject.getString("resCode");
			String resMsg = jsonObject.getString("resMsg");
			if (resCode.equals("0000")) {
				// 判断是否存在content
				if (jsonObject.containsKey("data")) {
					String value = jsonObject.getString("data");
					log.info("------------------" + value);

					JSON.parseArray(value, HdCreditReturnContentModel.class);
					List<HdCreditReturnContentModel> contentList = JSON.parseArray(value,
							HdCreditReturnContentModel.class);
					List<HdCreditReturnContentModel> list = new ArrayList<HdCreditReturnContentModel>();
					for (HdCreditReturnContentModel data : contentList) {
						HdCreditReturnContentModel r = new HdCreditReturnContentModel();
						r.setScore(data.getScore());
						r.setResultCode(data.getResultCode());
						r.setItemId(data.getItemId());
						r.setResMsg(data.getResMsg());
						log.info("分数：" + data.getScore());
						list.add(r);

					}
					hrm.setData(list);
					hrm.setResCode(resCode);
					hrm.setResMsg(resMsg);
				}

			} else {
				hrm.setResCode(resCode);
				hrm.setResMsg(resMsg);
			}
			return hrm;
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
	 * @throws Exception
	 */
	public BankCredit creditOracle(String token, String businessNo, String cust_name, String cert_type, String cert_num,
			String phoneNo) throws Exception {
		HdCreditReturnModel hcm = new HdCreditReturnModel();

		// 查询人行存在评级信息
		BackCredit cm = creditRepository.getOptName(cust_name, cert_type, cert_num);
		BankCredit um = new BankCredit();
		if (cm == null) {
			return null;
		} else {
			um.setSerial_No(businessNo);
			um.setCreated_time(new Date());
			um.setCreator("admin");
			um.setCert_num(cert_num);
			um.setCert_type(cert_type);
			um.setCust_name(cust_name);
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
		}
		return um;
	}

	/**
	 * 将查询到的视图入库
	 * @param um
	 */
	
	public void savecredit(BankCredit um) {
		bankCreditRepository.save(um);
	}
	
    /**
     * 入库或者发送韩迪http请求
     * @param token
     * @param businessNo
     * @param cust_name
     * @param cert_type
     * @param cert_num
     * @param phoneNo
     * @return
     * @throws Exception
     */
	public boolean saveInformation(String token, String businessNo, String cust_name, String cert_type, String cert_num,
			String phoneNo) throws Exception {

		BankCredit cm = this.creditOracle(token, businessNo, cust_name, cert_type, cert_num, phoneNo);
		if (cm == null) {
			HdCreditScoreModel returnxml = this.sendHdPost(token, businessNo, cust_name, cert_type, cert_num, phoneNo);
			//查询韩迪返回的数据进行解析评分
			log.info("-------------------------------------------");
			
			return true;
		} else {
			this.savecredit(cm);
			return false;
		}
	}
/**
 * 发送韩迪http请求
 * @param token
 * @param businessNo
 * @param cust_name
 * @param cert_type
 * @param cert_num
 * @param phoneNo
 * @return
 * @throws Exception
 */
	public HdCreditScoreModel sendHdPost(String token, String businessNo, String cust_name, String cert_type,
			String cert_num, String phoneNo) throws Exception {
		log.info("发送翰迪http请求.......");
		UserCreditQueneModel user = new UserCreditQueneModel();
		user.setBusinessNo(businessNo);
		user.setCustName(cust_name);
		user.setIdNo(cert_num);
		user.setPhoneNo(phoneNo);
		String xml = creditPersonalService.getCreditRequestXml(user);
		String returnxml = creditPersonalService.sendHdCredit(xml);
		log.info("---------------------------" + returnxml);
		HdCreditScoreModel hm = JaxbUtil.readValue(returnxml, HdCreditScoreModel.class);
		if (hm.getResCode().equals("0000")) {
			log.info("成功");
		}
		return hm;
	}

	/**
	 * 回调征信接口
	 * 
	 * @param businessNo
	 * @param customScore
	 */
	public void creditCallBack(String token, String businessNo, String customScore, String custName) {
		CallBackCustomerScoreModel cm = new CallBackCustomerScoreModel();
		List<CallBackCustomerScoreContentModel> csm = new ArrayList<CallBackCustomerScoreContentModel>();
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");// 设置日期格式
		String sendTime = df.format(new Date());// new Date()为获取当前系统时间
		CallBackCustomerScoreContentModel cc = new CallBackCustomerScoreContentModel();
		cc.setBusinessNo(businessNo);
		cc.setCustomScoree(customScore);
		csm.add(cc);
		cm.setContent(csm);
		cm.setContent(csm);
		cm.setPlatformNo(custName);
		cm.setSerialNo(businessNo);
		cm.setToken(token);
		cm.setTxTime(sendTime);

		String json = JSON.toJSONString(cm);
		log.info("回调征信接口:" + json);
		try {
			// 创建连接
			URL url = new URL("http://172.16.112.180:9090/wmxtcms/callback/custom.action");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setRequestMethod("POST");
			connection.setUseCaches(false);
			connection.setInstanceFollowRedirects(true);
			connection.setRequestProperty("Content-Type", "application/json");
			connection.connect();

			// POST请求
			DataOutputStream out = new DataOutputStream(connection.getOutputStream());

			String str = URLEncoder.encode(json, "utf-8");
			out.writeBytes(str);
			out.flush();
			out.close();

			// 读取响应
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
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
