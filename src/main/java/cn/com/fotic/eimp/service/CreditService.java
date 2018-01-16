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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import cn.com.fotic.eimp.model.CallBackCustomerScoreContentModel;
import cn.com.fotic.eimp.model.CallBackCustomerScoreModel;
import cn.com.fotic.eimp.model.HdCreditScoreContentModel;
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
 * 
 * @author liugj
 *
 */
@Slf4j
@Service
public class CreditService {

	@Value("${xd.crediturl}")
	private String callbackcrediturl;
	
	@Value("${hd.creditDefault.score}")
	private String score;

	@Autowired
	private BackCreditRepository backCreditRepository;

	@Autowired
	private BankCreditRepository bankCreditRepository;

	@Autowired
	private CreditPersonalService creditPersonalService;

	@Autowired
	private CreditPersonalRepository creditPersonalRepository;

	public static final String CREDIT_CONTENT = "content";
	public static final String CREDIT_ERRORCODE = "02";
	public static final String CREDIT_ERRORMSG = "证件号或证件类型校验失败";
	public static final String CREDIT_ERRORCODE1 = "03";
	public static final String CREDIT_ERRORMSG1 = "上送的格式不对，不是一个数组";
	public static final String CREDIT_SUCCESSCODE = "01";
	public static final String CREDIT_SUCCESSMSG = "成功";
	public static final String CREDIT_FLOWNO = "flowNo";
	public static final String CREDIT_NAN = "NaN";
	public static final String CREDIT_ZERO = "0";
	public static final String CREDIT_ONE = "1";
	public static final String CREDIT_TWO = "2";
	public static final String CREDIT_TRUE = "true";
	public static final String CREDIT_HDSUCCESS = "0000";
	public static final String CREDIT_EMPTY = "";

	/**
	 * 信贷征信入口
	 * 
	 * @param creditjson
	 * @return
	 * 
	 */
	public CallBackCustomerScoreModel creditContentService(String creditjson) {
		CallBackCustomerScoreModel cs = new CallBackCustomerScoreModel();
		CallBackCustomerScoreContentModel csc = new CallBackCustomerScoreContentModel();
		List<CallBackCustomerScoreContentModel> csclist = new ArrayList<CallBackCustomerScoreContentModel>();
		UserCreditQueneModel user = JaxbUtil.readValue(creditjson, UserCreditQueneModel.class);
		String businessNo = user.getBusinessNo();
		String idType = user.getIdType();
		String idNo = user.getIdNo();
		String custName = user.getCustName();
		String phoneNo = user.getPhoneNo();
		String flowNo = user.getFlowNo();
		try {
			String score = this.saveInformation(flowNo, businessNo, custName, idType, idNo, phoneNo);
			csc.setBusinessNo(businessNo);
			csc.setCustomScoree(score);
			csclist.add(csc);
			cs.setContent(csclist);
		} catch (Exception e) {
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
		log.info("征信请求进来的json：" + b);
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
		if (jsonObject.containsKey(CREDIT_CONTENT)) {
			String value = jsonObject.getString(CREDIT_CONTENT);
			List<UserCreditContentModel> contentList = JSON.parseArray(value, UserCreditContentModel.class);
			for (UserCreditContentModel user : contentList) {
				String idType = user.getIdType();
				String idNo = user.getIdNo();
				String custName = user.getCustName();
				boolean verification = this.VerificationService(custName, idNo, idType);
				if (verification != true) {
					um.setReCode(CREDIT_ERRORCODE);
					um.setReDesc(CREDIT_ERRORMSG);
					return um;
				}
			}
			um.setReCode(CREDIT_SUCCESSCODE);
			um.setReDesc(CREDIT_SUCCESSMSG);
			log.info("校验成功");
			return um;
		} else {
			um.setReCode(CREDIT_ERRORCODE1);
			um.setReDesc(CREDIT_ERRORMSG1);
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
		String flowNo = jsonObject.getString(CREDIT_FLOWNO);
		return flowNo;
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
			String rllr = cm.getRate_loanoff_loanopen_ratio();
			// 人行视图返回的rate_loanoff_loanopen_ratio字段存在NAN值，需要进行判断，默认为0
			if (CREDIT_NAN.equals(rllr)) {
				um.setRateLoanoffLoanopenRatio(CREDIT_ZERO);
			} else {
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
	public CreditPersonalDic sendHdPost(String flowNo, String businessNo, String cust_name, String cert_type,
			String cert_num, String phoneNo) throws Exception {
		UserCreditQueneModel user = new UserCreditQueneModel();
		CreditPersonalDic cpd = new CreditPersonalDic();
		user.setBusinessNo(businessNo);
		user.setCustName(cust_name);
		user.setIdNo(cert_num);
		user.setPhoneNo(phoneNo);
		String xml = creditPersonalService.getCreditRequestXml(user);
		String returnxml = creditPersonalService.sendHdCredit(xml);
		log.info("征信翰迪返回：" + returnxml);
		cpd.setApplyNum(businessNo);
		cpd.setSerialNo(flowNo);
		cpd.setBusinessNo(businessNo);
		cpd.setApplyTime(new Date());
		cpd.setCustName(cust_name);
		cpd.setCertType(cert_type);
		cpd.setPhone(phoneNo);
		if (!"".equals(returnxml) || null != returnxml) {
			HdCreditScoreModel hm = JaxbUtil.readValue(returnxml, HdCreditScoreModel.class);
			if (CREDIT_HDSUCCESS.equals(hm.getResCode())) {
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
				cpd.setScore(SumUtil.getCreditScore(hm.getResData().getScore()));
				cpd.setResponseBody(returnxml);
				HdCreditScoreContentModel resData = hm.getResData();
				// istargeted,true值为1，false值为2
				String istargeted = resData.getIs_targeted();
				if (StringUtils.isNotBlank(istargeted)) {
					if (CREDIT_TRUE.equals(istargeted)) {
						String isTargeted = CREDIT_ONE;
						cpd.setIsTargert(isTargeted);
					} else {
						String isTargeted = CREDIT_TWO;
						cpd.setIsTargert(isTargeted);
					}
				}
				log.info("征信韩迪返回入库成功:分数：" + hm.getResData().getScore());
			} else {
				cpd.setScore(score);
				cpd.setResponseBody(returnxml);
			}
		} else {
			cpd.setScore(score);
			cpd.setResponseBody(returnxml);
		}
		creditPersonalRepository.save(cpd);
		return cpd;
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
	public String saveInformation(String flowNo, String businessNo, String cust_name, String cert_type, String cert_num,
			String phoneNo) throws Exception {
		BankCredit cm = this.creditOracle(businessNo, cust_name, cert_type, cert_num, phoneNo);
		if (null == cm) {
			// 查询韩迪返回的数据进行解析评分
			CreditPersonalDic returnxml = this.sendHdPost(flowNo, businessNo, cust_name, cert_type, cert_num, phoneNo);
			String score = returnxml.getScore();
			return score;
		} else {
			// 查询人行的数据的数据进行解析评分
			int sum = SumUtil.countScore(cm);
			String score = String.valueOf(sum);
			// 将人行视图存入本地数据库
			cm.setScore(score);
			this.savecredit(cm);
			log.info("本地入库成功,本地分数:"+score);
			return score;
		}
	}

	/**
	 * 回调征信接口
	 * 
	 * @param json
	 */
	public boolean creditCallBack(String json) {
		return this.callBackCredit(json);
	}

	/**
	 * 业务处理成功回调信贷
	 * 
	 * @param fraudUrl
	 * @param json
	 */
	public boolean callBackCredit(String json) {
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
			StringBuffer sb = new StringBuffer(CREDIT_EMPTY);
			while ((lines = reader.readLine()) != null) {
				lines = new String(lines.getBytes(), "utf-8");
				sb.append(lines);
			}
			String callBackCredit = URLDecoder.decode(sb.toString(), "utf-8");
			log.info("征信回调成功,返回:" + callBackCredit);
			if (CREDIT_EMPTY.equals(callBackCredit)) {
				reader.close();
				// 断开连接
				connection.disconnect();
				return false;
			} else {
				reader.close();
				// 断开连接
				connection.disconnect();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
}
