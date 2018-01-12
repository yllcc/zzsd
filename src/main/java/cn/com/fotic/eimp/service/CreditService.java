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
import cn.com.fotic.eimp.model.HdCreditReturnModel;
import cn.com.fotic.eimp.model.HdCreditScoreModel;
import cn.com.fotic.eimp.model.UserCreditContentModel;
import cn.com.fotic.eimp.model.UserCreditQueneModel;
import cn.com.fotic.eimp.model.UserCreditReturnModel;
import cn.com.fotic.eimp.primary.BankCreditRepository;
import cn.com.fotic.eimp.primary.CreditPersonalRepository;
import cn.com.fotic.eimp.repository.entity.BackCredit;
import cn.com.fotic.eimp.repository.entity.BankCredit;
import cn.com.fotic.eimp.repository.entity.CreditPersonalDic;
import cn.com.fotic.eimp.second.BackCreditRepository;
import cn.com.fotic.eimp.utils.JaxbUtil;
import cn.com.fotic.eimp.utils.SumUtil;
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
	  
	 @Value("${xd.crediturl}") private String callbackcrediturl;
	  
	@Autowired
	private BackCreditRepository backCreditRepository;

	@Autowired
	private BankCreditRepository bankCreditRepository;

	@Autowired
	private CreditPersonalService creditPersonalService;
	
	@Autowired
	private CreditPersonalRepository creditPersonalRepository;


	/**
	 * 信贷征信入口
	 * 
	 * @param creditjson
	 * @return
	 */
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
			String flowNo=user.getFlowNo();
		  try {
			   String score = this.saveInformation(flowNo, businessNo, custName, idType, idNo, phoneNo);
			   csc.setBusinessNo(businessNo);
			   csc.setCustomScoree(score);
			   csclist.add(csc);
			   cs.setContent(csclist);
		  } catch (Exception e) {
			   // TODO Auto-generated catch block
			   e.printStackTrace();
		  }
			cs.setAccessToken(user.getAccessToken());
			cs.setReqTime(user.getReqTime());
			cs.setFlowNo(user.getFlowNo());	
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
		String a = new String(buffer, "utf-8");
		String b = URLDecoder.decode(a.toString(), "utf-8");
		log.info("征信请求进来的json："+b);
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
				if (verification != true) {
					um.setReCode("02");
					um.setReDesc("证件号或证件类型校验失败");
					log.info("证件号或证件类型校验失败");
					return um;
				}	
			}
			um.setReCode("01");
			um.setReDesc("成功");
			log.info("校验成功");
			return um;
		} else {
			um.setReCode("03");
			um.setReDesc("上送的格式不对，不是一个数组");
			log.info("上送的格式不对，不是一个数组");
			return um;
		}
	}
	

	/**
	 * 获取这批次流水号
	 * 
	 * @param json
	 * @return
	 */
	public String flownNo(String json) {
		JSONObject jsonObject = JSON.parseObject(json);
		String flowNo = jsonObject.getString("flowNo");
		return flowNo;
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
			String rllr=cm.getRate_loanoff_loanopen_ratio();
			if("NaN".equals(rllr)) {
				um.setRateLoanoffLoanopenRatio("0");
			}else {
			    um.setRateLoanoffLoanopenRatio(rllr);
			}
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
	public HdCreditScoreModel sendHdPost(String flowNo,String businessNo, String cust_name, String cert_type,
			String cert_num, String phoneNo) throws Exception {
		log.info("征信发送翰迪http请求查询征信.......");
		UserCreditQueneModel user = new UserCreditQueneModel();
		user.setBusinessNo(businessNo);
		user.setCustName(cust_name);
		user.setIdNo(cert_num);
		user.setPhoneNo(phoneNo);
		String xml = creditPersonalService.getCreditRequestXml(user);
		String returnxml = creditPersonalService.sendHdCredit(xml);
		log.info("征信翰迪返回：" + returnxml);
		HdCreditScoreModel hm = JaxbUtil.readValue(returnxml, HdCreditScoreModel.class);
		String istargeted=hm.getResData().getIs_targeted();
		CreditPersonalDic cpd=new CreditPersonalDic();		
		if("TRUE".equals(istargeted)){
			String isTargeted="1";
			cpd.setIsTargert(isTargeted);
		}else {
			String isTargeted="2";
			cpd.setIsTargert(isTargeted);
		}
		if ("0000".equals(hm.getResCode())) {			
			cpd.setApplyNum(businessNo);
			cpd.setSerialNo(flowNo);
			cpd.setBusinessNo(businessNo);
			cpd.setApplyTime(new Date());
			cpd.setCustName(cust_name);
			cpd.setCertType(cert_type);
			cpd.setPhone(phoneNo);
			cpd.setCreditScore(hm.getResData().getScore());
			cpd.setStability(hm.getResData().getStability());
			cpd.setBuyingIndex(hm.getResData().getBuyingIndex());
			cpd.setRiskIndex(hm.getResData().getRiskIndex());
			cpd.setPerformanceIndex(hm.getResData().getPerformanceIndex());
			cpd.setResonableConsuming(hm.getResData().getResonableConsuming());
			cpd.setCity(hm.getResData().getCity());
			cpd.setComsumSocial(hm.getResData().getComsumingSocial());
			cpd.setIncome(hm.getResData().getIncomming());
			cpd.setCellPhone_preference(hm.getResData().getCellphonePreference());
			cpd.setEcommerceActiveness(hm.getResData().getEcommerceActiveness());
			cpd.setEcommerceAddressStability(hm.getResData().getEcommerceAddressStability());
			cpd.setEcommerceCellPhoneStability(hm.getResData().getEcommercecellphoneStability());
			cpd.setEcommerceAccountHistory(hm.getResData().getEcommerceAccountHistory());
			cpd.setCashPreference(hm.getResData().getCellphonePreference());
			cpd.setRiskPeriodPayment(hm.getResData().getRiskPeriodPayment());
			cpd.setRiskCategoryPayment(hm.getResData().getRiskCategoryPayment());
			cpd.setResponseBody(returnxml);	
			creditPersonalRepository.save(cpd);
			log.info("征信韩迪返回入库成功");
		}else {
			log.info("征信韩迪征信返回的错误代码,错误信息："+hm.getResCode(),hm.getResMsg());
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
	public String saveInformation(String flowNo,String businessNo, String cust_name, String cert_type, String cert_num,
			String phoneNo) throws Exception {

		BankCredit cm = this.creditOracle( businessNo, cust_name, cert_type, cert_num, phoneNo);
		if (null == cm ) {
			HdCreditScoreModel returnxml = this.sendHdPost(flowNo,businessNo, cust_name, cert_type, cert_num, phoneNo);
			// 查询韩迪返回的数据进行解析评分
			String score=returnxml.getResData().getScore();
			log.info(businessNo + "：征信韩迪返回的分数："+score);
			return score;
		} else {
			this.savecredit(cm);
			// 查询人行的数据的数据进行解析评分
			int sum=SumUtil.countScore(cm);
			String score=String.valueOf(sum);
			log.info(businessNo + "：征信人行返回的分数:"+score);
			return score;
		}
	}
	/**
	 * 回调征信接口
	 * 
	 * @param json
	 */
	public void creditCallBack(String json) {
		log.info("征信回调征信接口：" + json);
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
				log.info("征信回调成功,返回:"+callBackCredit);
				reader.close();
				// 断开连接
				connection.disconnect();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}	
}
