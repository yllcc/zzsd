package cn.com.fotic.eimp.service;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URLDecoder;

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
import cn.com.fotic.eimp.model.HdCreditReturnContentModel;
import cn.com.fotic.eimp.model.HdCreditReturnModel;
import cn.com.fotic.eimp.model.HdCreditScoreModel;
import cn.com.fotic.eimp.model.UserCreditContentModel;
import cn.com.fotic.eimp.model.UserCreditQueneModel;
import cn.com.fotic.eimp.model.UserCreditReturnModel;
import cn.com.fotic.eimp.primary.BankCreditRepository;
import cn.com.fotic.eimp.primary.CreditPersonalRepository;
import cn.com.fotic.eimp.repository.entity.BackCredit;
import cn.com.fotic.eimp.repository.entity.BankCredit;
import cn.com.fotic.eimp.second.BackCreditRepository;
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
	// 韩迪接口加密请求URL
	private final String URL = "https://ds.handydata.cn/ds/service.ac";
	// 韩迪接口加密公钥
	private final String publicKey = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAIGm7bNehbstkfG/fAcZnrA3UGHVWjCRkP3S3/ZfF456ypdRAaEeqGILT+wB139K1HZIUy/Gl8slGS9r6TR961MCAwEAAQ==";
	// 渠道号
	private final String channelId = "11009035";
	// 回调征信UR
	private final String callbackcrediturl = "http://172.16.112.180:9090/wmxtcms/callback/custom.action";

	@Autowired
	private BackCreditRepository backCreditRepository;

	@Autowired
	private BankCreditRepository bankCreditRepository;

	@Autowired
	private CreditPersonalService creditPersonalService;

	@Autowired
	private FraudService fraudService;
	
	public  CallBackCustomerScoreModel creditScorreService(String creditjson) {
	
	CallBackCustomerScoreModel cm = new CallBackCustomerScoreModel();
	List<CallBackCustomerScoreContentModel> reclist = new ArrayList<CallBackCustomerScoreContentModel>();
	JSONObject jsonObject = JSON.parseObject(creditjson);
	String token = jsonObject.getString("token");
	String serialNo = jsonObject.getString("serialNo");
	String platformNo = jsonObject.getString("platformNo");
	String txTime = jsonObject.getString("txTime");
	// 判断是否存在content
	if (jsonObject.containsKey("content")) {
		String value = jsonObject.getString("content");
		// 判断是否为content数组
		List<UserCreditContentModel> contentList = JSON.parseArray(value, UserCreditContentModel.class);
		for (UserCreditContentModel user : contentList) {
			CallBackCustomerScoreContentModel csc = new CallBackCustomerScoreContentModel();
			String businessNo = user.getBusinessNo();
			String idType = user.getIdType();
			String idNo = user.getIdNo();
			String custName = user.getCustName();
			String phoneNo = user.getPhoneNo();
			String customScore = "";
			try {
				boolean a = this.saveInformation(token, businessNo, custName, idType, idNo, phoneNo);
				if (a == true) {
					// 查询征信数据库成功
					log.info("发送韩迪成功");
					customScore = "1000";

				} else {
					// 查询韩迪
					log.info("入库成功");
					customScore = "10000";

				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			csc.setBusinessNo(businessNo);
			csc.setCustomScoree(customScore);
			reclist.add(csc);
		}
		cm.setContent(reclist);
		cm.setPlatformNo(platformNo);
		cm.setSerialNo(serialNo);
		cm.setToken(token);
		cm.setTxTime(txTime);
	}
	return cm;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
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
	 * 
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
	 * 
	 * @param json
	 * @return
	 */
	public String flownNo(String json) {
		JSONObject jsonObject = JSON.parseObject(json);
		String serialNo = jsonObject.getString("serialNo");
		return serialNo;

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

		String mkey = UUID.randomUUID().toString();
		// 加密报文体格式：BASE64(商户号)| BASE64(RSA(报文加密密钥))| BASE64(3DES(报文原文))
		String strKey = RSAUtils.encryptByPublicKey(new String(mkey.getBytes(), "utf-8"), publicKey);
		String strxml = new String(
				Base64Utils.encode(ThreeDESUtils.encrypt(xml.toString().getBytes("utf-8"), mkey.getBytes())));
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
		BackCredit cm = backCreditRepository.getOptName(cust_name, cert_type, cert_num);
		BankCredit um = new BankCredit();
		if (cm == null) {
			return null;
		} else {
			um.setSerial_No(businessNo);
			um.setCreated_time(new Date());
			um.setCreator(cust_name);
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
	 * 
	 * @param um
	 */

	public void savecredit(BankCredit um) {
		bankCreditRepository.save(um);
	}


	/**
	 * 发送韩迪http请求
	 * 
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
		log.info("发送翰迪http请求查询征信.......");
		UserCreditQueneModel user = new UserCreditQueneModel();
		user.setBusinessNo(businessNo);
		user.setCustName(cust_name);
		user.setIdNo(cert_num);
		user.setPhoneNo(phoneNo);
		String xml = creditPersonalService.getCreditRequestXml(user);
		String returnxml = creditPersonalService.sendHdCredit(xml);
		log.info("翰迪返回征信：" + returnxml);
		HdCreditScoreModel hm = JaxbUtil.readValue(returnxml, HdCreditScoreModel.class);
		if (hm.getResCode().equals("0000")) {
			log.info("成功");
		}
		return hm;
	}
	
	/**
	 * 入库或者发送韩迪http请求
	 * 
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
			// 查询韩迪返回的数据进行解析评分
			log.info("-------------------------------------------");

			return true;
		} else {
			this.savecredit(cm);
			//// 查询人行的数据的数据进行解析评分
			log.info("-------------------------------------------");
			return false;
		}
	}


	/**
	 * 回调征信接口
	 * 
	 * @param json
	 */
	public void creditCallBack(String json) {
		log.info("信贷回调征信接口" + json);
		fraudService.callBackCommon(callbackcrediturl, json);

	}

}
