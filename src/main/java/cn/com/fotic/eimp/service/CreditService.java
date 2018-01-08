package cn.com.fotic.eimp.service;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
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
import org.springframework.beans.factory.annotation.Value;
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
 * 自主审贷信贷征信接口服务
 * @author liugj
 *
 */
@Slf4j
@Service
public class CreditService {
	    @Value("${hd.url}") private String URL;
	    @Value("${hd.publicKey}") private String publicKey;
	    @Value("${hd.credit.channelId}") private String channelId;
	    @Value("${xd.crediturl}") private String callbackcrediturl;
	@Autowired
	private BackCreditRepository backCreditRepository;

	@Autowired
	private BankCreditRepository bankCreditRepository;

	@Autowired
	private CreditPersonalService creditPersonalService;


	/**
	 * 信贷征信入口
	 * 
	 * @param creditjson
	 * @return
	 */

	/*public CallBackCustomerScoreModel creditScorreService(String creditjson) {
		CallBackCustomerScoreModel cm = new CallBackCustomerScoreModel();
		List<CallBackCustomerScoreContentModel> reclist = new ArrayList<CallBackCustomerScoreContentModel>();
		JSONObject jsonObject = JSON.parseObject(creditjson);
		
		String flowno = jsonObject.getString("flowno");
		String accesstoken = jsonObject.getString("accesstoken");
		String reqTime = jsonObject.getString("reqTime");
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
					boolean a = this.saveInformation( businessNo, custName, idType, idNo, phoneNo);
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
			cm.setAccesstoken(accesstoken);
			cm.setFlowno(flowno);
			cm.setReqTime(reqTime);
		}
		return cm;
	}*/
	   public	CallBackCustomerScoreModel creditContentService(String creditjson) {
	    	CallBackCustomerScoreModel  cs=new CallBackCustomerScoreModel ();
	    	CallBackCustomerScoreContentModel csc=new CallBackCustomerScoreContentModel();
	    	List <CallBackCustomerScoreContentModel> csclist=new ArrayList <CallBackCustomerScoreContentModel>();
	    	UserCreditQueneModel user = JaxbUtil.readValue(creditjson, UserCreditQueneModel.class);	    	
			String businessNo =  user.getBusinessNo();
			String idType =  user.getIdType();
			String idNo =  user.getIdNo();
			String custName =  user.getCustName();
			String phoneNo = user.getPhoneNo();
			String customScore = "";
			try {
				boolean a = this.saveInformation( businessNo, custName, idType, idNo, phoneNo);
				if (a == true) {
					// 查询征信数据库成功
					log.info("发送韩迪成功");
					customScore = "1000";
					csc.setBusinessNo(businessNo);
					csc.setCustomScoree(customScore);
					csclist.add(csc);				
					cs.setContent(csclist);
					cs.getContent().get(0).getCustomScore();
				} else {
					// 查询韩迪
					log.info("入库成功");
					customScore = "10000";
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
			cs.setAccesstoken(user.getAccesstoken());
			cs.setReqTime(user.getReqTime());
			cs.setFlowno(user.getFlowno());	
			return cs;	
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
		log.info(request.getContextPath());
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
	 * 接受数据信贷发送的数据进行校验返回给信贷
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
	
	
	public UserCreditReturnModel verificationCredit(HttpServletRequest request) throws IOException {
		UserCreditReturnModel um = new UserCreditReturnModel();
		JSONObject jsonObject = JSON.parseObject(this.longinHttep(request));
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
		String flowno = jsonObject.getString("flowno");
		return flowno;
	}

	/**
	 * 调用翰迪接口 加密请求数据
	 * 
	 * @return
	 * @throws Exception
	 */
	public HdCreditReturnModel hdCreditService(String xml) throws Exception {
		HdCreditReturnModel hrm = new HdCreditReturnModel();
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
	public BankCredit creditOracle(String businessNo, String cust_name, String cert_type, String cert_num,
			String phoneNo) throws Exception {
		HdCreditReturnModel hcm = new HdCreditReturnModel();
		// 查询人行存在评级信息
		BackCredit cm = backCreditRepository.getOptName(cust_name, cert_type, cert_num);
		BankCredit um = new BankCredit();
		if (cm == null) {
			return null;
		} else {
			um.setSerialNo(businessNo);
			um.setApplyTime(new Date());
			um.setApplyPerson(cust_name);
			um.setCertNum(cert_num);
			um.setCertType(cert_type);
			um.setCustName(cust_name);
			um.setRateCreditAccountCount(cm.getRate_credit_account_count());
			um.setRateCreditcardApprovalCont(cm.getRate_creditcard_approval_count());
			um.setRateCreditreporCount(cm.getRate_creditrepor_count());
			um.setRateEduLevel(cm.getRate_edu_level());
			um.setRateFirstnoaccountCardage(cm.getRate_firstnoaccount_cardage());
			um.setRateFiveyearMaxoverdueCount(cm.getRate_fiveyear_maxoverdue_count());
			um.setRateLoanoffLoanopenRatio(cm.getRate_loanoff_loanopen_ratio());
			um.setRateMaritalState(cm.getRate_marital_state());
			um.setRateNoaccountFirstendBal(cm.getRate_noaccount_firstend_bal());
			um.setRateNormalAvenotusedlimitrat(cm.getRate_normal_avenotusedlimitrat());
			um.setRateRecentlyOpencardLimit(cm.getRate_recently_opencard_limit());
			um.setRateRegister(cm.getRate_register());
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
	public HdCreditScoreModel sendHdPost(String businessNo, String cust_name, String cert_type,
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
	public boolean saveInformation( String businessNo, String cust_name, String cert_type, String cert_num,
			String phoneNo) throws Exception {

		BankCredit cm = this.creditOracle( businessNo, cust_name, cert_type, cert_num, phoneNo);
		if (null == cm ) {
			HdCreditScoreModel returnxml = this.sendHdPost(businessNo, cust_name, cert_type, cert_num, phoneNo);
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
		this.callBackCredit(json);
	}
	  /**
	    * 业务处理成功回调信贷
	    * @param fraudUrl
	    * @param json
	    */
		public void callBackCredit(String json) {
			try {
				// 创建连接
				URL url = new URL(callbackcrediturl);
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
				String callBackCredit = URLDecoder.decode(sb.toString(), "utf-8");
				log.info(callBackCredit);
				reader.close();
				// 断开连接
				connection.disconnect();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
}
