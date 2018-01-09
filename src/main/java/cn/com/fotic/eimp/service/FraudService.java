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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.com.fotic.eimp.model.CallBackCustomerScoreContentModel;
import cn.com.fotic.eimp.model.CallBackCustomerScoreModel;
import cn.com.fotic.eimp.model.CallBackUserCreditContentModel;
import cn.com.fotic.eimp.model.CallBackUserCreditModel;
import cn.com.fotic.eimp.model.HdAntiFraudModel;
import cn.com.fotic.eimp.model.HdCreditReturnModel;
import cn.com.fotic.eimp.model.UserCreditContentModel;
import cn.com.fotic.eimp.model.UserCreditQueneModel;
import cn.com.fotic.eimp.primary.CreditFraudRepository;
import cn.com.fotic.eimp.repository.entity.CreditFraudDic;
import cn.com.fotic.eimp.utils.JaxbUtil;
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
	private CreditService creditService;

	@Autowired
	private CreditFraudRepository creditFraudRepository;

	@Value("${hd.fraud.channelId}")
	private String channelId;// 渠道号

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
		// 2.进行数据加密,发送数据给韩迪
		try {
			HdCreditReturnModel r = creditService.hdCreditService(xml);
			if ("0000".equals(r.getResCode())) {
				// 韩迪返回查询成功信息
				CreditFraudDic cpd = new CreditFraudDic();
				String fraudScore = r.getData().get(0).getScore();
				cpd.setFraudNum(flowNo);
				cpd.setBusinessNo(businessNo);
				cpd.setApplyTime(new Date());
				cpd.setCustName(custName);
				cpd.setCertType(idType);
				cpd.setCertNum(idNo);
				cpd.setPhone(phoneNo);
				cpd.setFraudScore(fraudScore);
				cpd.setFraudNum(businessNo);
				// 将反欺诈分数入库
				this.fraudScoreSave(cpd);
				csc.setBusinessNo(businessNo);
				csc.setFraudScore(fraudScore);
				csclist.add(csc);
				log.info("反欺诈入库处理成功,业务流水号：" + businessNo);
			} else {
				// 韩迪返回查询错误信息
				log.info("查询反欺诈处理失败,业务流水号：" + businessNo + ",韩迪返回失败原因：" + r.getResCode() + r.getResMsg());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
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
		hd.setIp("");
		hd.setLinkedMerchantId(LinkedMerchantId);
		hd.setMobile("");
		hd.setName(custName);
		hd.setOpenId("");
		hd.setEmail("");
		hd.setImei("");
		hd.setAddress("");
		hd.setAddress("");
		hd.setBankCard("");
		hd.setProductItemCode(ProductItemCode);
		hd.setWifiMac("");
		hd.setMac("");
		String xmlReq = JaxbUtil.convertToXml(hd);
		return xmlReq;
	}

	/**
	 * 回调反欺诈借口
	 * 
	 * @param businessNo
	 * @param fraudScore
	 */
	public void fraudCallBack(String fraudjson) {
		log.info("回调反欺诈接口" + fraudjson);
		this.callBackFraud(fraudjson);
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
	public void callBackFraud(String json) {
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
			log.info(callBackCredit);
			reader.close();
			// 断开连接
			connection.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
