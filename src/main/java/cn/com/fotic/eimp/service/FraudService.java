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
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import cn.com.fotic.eimp.model.CallBackUserCreditContentModel;
import cn.com.fotic.eimp.model.CallBackUserCreditModel;
import cn.com.fotic.eimp.model.HdAntiFraudModel;
import cn.com.fotic.eimp.model.HdCreditReturnContentModel;
import cn.com.fotic.eimp.model.HdCreditReturnModel;
import cn.com.fotic.eimp.model.UserCreditQueneModel;
import cn.com.fotic.eimp.primary.CreditFraudRepository;
import cn.com.fotic.eimp.repository.entity.CreditFraudDic;
import cn.com.fotic.eimp.utils.Base64Utils;
import cn.com.fotic.eimp.utils.HttpUtil;
import cn.com.fotic.eimp.utils.JaxbUtil;
import cn.com.fotic.eimp.utils.RSAUtils;
import cn.com.fotic.eimp.utils.ThreeDESUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * 自主审贷反欺诈服务
 * 
 * @author liugj
 *
 */
@Slf4j
@Service
public class FraudService {

	@Autowired
	private CreditFraudRepository creditFraudRepository;

	@Value("${hd.url}")
	private String URL;

	@Value("${hd.publicKey}")
	private String publicKey;

	@Value("${hd.fraud.channelId}")
	private String channelId;// 渠道号11009028

	@Value("${hd.fraud.hdChannelId}")
	private String hdChannelId;// 渠道号11000000

	@Value("${hd.fraud.application}")
	private String application;// 应用名称

	@Value("${hd.fraud.version}")
	private String version;// 当前版本取值

	@Value("${hd.fraud.transCode}")
	private String transCode;// 固定交易代码

	@Value("${hd.fraud.LinkedMerchantId}")
	private String LinkedMerchantId;// 商户ID

	@Value("${hd.fraud.ProductItemCode}")
	private String ProductItemCode;// 产品子项

	@Value("${xd.fraudurl}")
	private String fraudUrl;// 回调反欺诈URL
	
	@Value("${hd.fraudDefault.score}")
	private String score;
	

	public static final String FRAUD_FLOWNO = "flowNo";
	public static final String FRAUD_ZERO = "0";
	public static final String FRAUD_DATA = "data";
	public static final String FRAUD_SUCCESS = "0000";
	public static final String FRAUD_RESCODE = "resCode";
	public static final String FRAUD_RESMSG = "resMsg";
	public static final String FRAUD_EMPTY = "";

	/**
	 * 信贷反欺诈入口
	 * 
	 * @param fraudjson
	 * @return
	 */
	public CallBackUserCreditModel fraudContentService(String creditjson) {

		CallBackUserCreditModel cs = new CallBackUserCreditModel();
		CallBackUserCreditContentModel csc = new CallBackUserCreditContentModel();
		List<CallBackUserCreditContentModel> csclist = new ArrayList<CallBackUserCreditContentModel>();
		UserCreditQueneModel user = JaxbUtil.readValue(creditjson, UserCreditQueneModel.class);
		String flowNo = user.getFlowNo();
		String businessNo = user.getBusinessNo();
		String idType = user.getIdType();
		String idNo = user.getIdNo();
		String custName = user.getCustName();
		String phoneNo = user.getPhoneNo();
		String accessToken = user.getAccessToken();
		// 1.生成xml
		String xml = this.HdFraudService(idNo, custName);
		// 2.进行数据加密,发送数据给韩迪hd.fraud.channelId
		try {
			HdCreditReturnModel r = this.hdCreditService(xml);
			CreditFraudDic cpd = new CreditFraudDic();
			if (FRAUD_SUCCESS.equals(r.getResCode())) {
				// 韩迪返回查询成功信息
				String fraudScore = r.getData().get(0).getScore();
				cpd.setFraudScore(fraudScore);
			} else {
				// 韩迪返回查询错误信息
				log.info("反欺诈处理失败,业务流水号：" + businessNo + ",韩迪返回失败原因：" + r.getResCode() + r.getResMsg());
				cpd.setFraudScore(score);
			}
			cpd.setBusinessNo(businessNo);
			cpd.setApplyTime(new Date());
			cpd.setCustName(custName);
			cpd.setCertType(idType);
			cpd.setCertNum(idNo);
			cpd.setPhone(phoneNo);
			cpd.setCheckTime(new Date());
			cpd.setResponseBody(creditjson);
			cpd.setSerialNo(flowNo);
			cpd.setFraudNum(businessNo);
			// 将反欺诈分数入库
			this.fraudScoreSave(cpd);
			csc.setBusinessNo(businessNo);
			csc.setFraudScore(cpd.getFraudScore());
			csclist.add(csc);
			log.info("反欺诈入库处理成功,业务流水号：" + businessNo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		cs.setAccessToken(accessToken);
		cs.setReqTime(user.getReqTime());
		cs.setFlowNo(user.getFlowNo());
		cs.setContent(csclist);
		return cs;
	}

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
		hd.setTransCode(transCode);
		hd.setVersion(version);
		hd.setApplication(application);
		hd.setCertNo(idNo);
		hd.setChannelId(channelId);
		hd.setChannelOrderId(JaxbUtil.getRandomStringByLength(30));
		hd.setIp(FRAUD_EMPTY);
		hd.setLinkedMerchantId(LinkedMerchantId);
		hd.setMobile(FRAUD_EMPTY);
		hd.setName(custName);
		hd.setOpenId(FRAUD_EMPTY);
		hd.setEmail(FRAUD_EMPTY);
		hd.setImei(FRAUD_EMPTY);
		hd.setAddress(FRAUD_EMPTY);
		hd.setAddress(FRAUD_EMPTY);
		hd.setBankCard(FRAUD_EMPTY);
		hd.setProductItemCode(ProductItemCode);
		hd.setWifiMac(FRAUD_EMPTY);
		hd.setMac(FRAUD_EMPTY);
		String xmlReq = JaxbUtil.convertToXml(hd);
		return xmlReq;
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
		String returnXml = new String(Base64Utils.encode(hdChannelId.getBytes("utf-8"))) + "|" + strKey + "|" + strxml;
		String reutrnResult = HttpUtil.sendXMLDataByPost(URL, returnXml);
		String xmlArr[] = reutrnResult.split("\\|");
		if (xmlArr[0].equals(FRAUD_ZERO)) {
			String resMsg = new String(Base64Utils.decode(xmlArr[2]), "utf-8");
			hrm.setResMsg(resMsg);
			return hrm;
		} else {
			byte[] b = ThreeDESUtils.decrypt(Base64Utils.decode(xmlArr[1]), mkey.getBytes());
			String tradeXml = new String(b, "utf-8");
			// log.info("333" + tradeXml);
			JSONObject jsonObject = JSON.parseObject(tradeXml);
			String resCode = jsonObject.getString(FRAUD_RESCODE);
			String resMsg = jsonObject.getString(FRAUD_RESMSG);
			if ((FRAUD_SUCCESS).equals(resCode)) {
				// 判断是否存在content
				if (jsonObject.containsKey(FRAUD_DATA)) {
					String value = jsonObject.getString(FRAUD_DATA);
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
	 * 回调反欺诈借口
	 * 
	 * @param businessNo
	 * @param fraudScore
	 */
	public boolean fraudCallBack(String fraudjson) {
		log.info("反欺诈接口回调" + fraudjson);
		return this.callBackFraud(fraudjson);
	}

	/**
	 * 反欺诈评分入库
	 */
	public void fraudScoreSave(CreditFraudDic cpd) {
		creditFraudRepository.save(cpd);
	}

	/**
	 * 业务处理成功回调信贷
	 * 
	 * @param fraudUrl
	 * @param json
	 */
	public boolean callBackFraud(String json) {
		try {
			// 创建连接
			URL url = new URL(fraudUrl);
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
			log.info("反欺诈回调成功,返回:" + callBackCredit);
			if ("".equals(callBackCredit)) {
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
