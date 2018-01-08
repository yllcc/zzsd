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
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.com.fotic.eimp.model.CallBackUserCreditContentModel;
import cn.com.fotic.eimp.model.CallBackUserCreditModel;
import cn.com.fotic.eimp.model.HdAntiFraudModel;
import cn.com.fotic.eimp.model.HdCreditReturnModel;
import cn.com.fotic.eimp.model.UserCreditContentModel;
import cn.com.fotic.eimp.primary.CreditFraudRepository;
import cn.com.fotic.eimp.repository.entity.CreditFraudDic;
import cn.com.fotic.eimp.repository.entity.CreditPersonalDic;
import cn.com.fotic.eimp.second.BackCreditRepository;
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
	
	@Autowired
	private CreditService creditService;

	@Autowired
	private CreditFraudRepository  creditFraudRepository;
	
	private final String channelId = "11009028";// 渠道号

	private final String application = "GwBiz.Req";// 应用名称

	private final String version = "1.0.0";// 当前版本取值

	private final String transCode = "100101";// 固定交易代码

	private final String LinkedMerchantId = "2088621466375255";// 商户ID

	private final String ProductItemCode = "100102";// 产品子项
	// 回调反欺诈URL
	private final String fraudUrl = "http://172.16.112.180:9090/wmxtcms/callback/fraud.action";
	
	/**
	 * 
	 * @param fraudjson
	 * @return
	 */
	

	public CallBackUserCreditModel fraudService(String fraudjson) {
		
	CallBackUserCreditModel cm = new CallBackUserCreditModel();
	List<CallBackUserCreditContentModel> reclist = new ArrayList<CallBackUserCreditContentModel>();
	JSONObject jsonObject = JSON.parseObject(fraudjson);
	String token = jsonObject.getString("token");
	String serialNo = jsonObject.getString("serialNo");
	String platformNo = jsonObject.getString("platformNo");
	String txTime = jsonObject.getString("txTime");
	// 判断是否存在content
	if (jsonObject.containsKey("content")) {
		String value = jsonObject.getString("content");
		List<UserCreditContentModel> contentList = JSON.parseArray(value, UserCreditContentModel.class);
		for (UserCreditContentModel user : contentList) {
			CallBackUserCreditContentModel csc = new CallBackUserCreditContentModel();
			String businessNo = user.getBusinessNo();
			String idNo = user.getIdNo();
			String idType = user.getIdType();
			String custName = user.getCustName();			
			String phone = user.getPhoneNo();
			String fraudScore = "";
			// 1.生成xml
			String xml = this.HdFraudService(idNo, custName);
			// 2.进行数据加密,发送数据给韩迪
			try {
				HdCreditReturnModel r = creditService.checkRiskSystem(xml);
				if (r.getResCode().equals("0000")) {
					// 韩迪返回查询成功信息
					CreditFraudDic cpd=new CreditFraudDic();
					fraudScore = r.getData().get(0).getScore();
					cpd.setSerialNo(serialNo);
					cpd.setBusinessNo(businessNo);
					cpd.setApplyTime(new Date());
					cpd.setCustName(custName);
					cpd.setCertType(idType);
					cpd.setCertNum(idNo);
					cpd.setPhone(phone);
					cpd.setFraudScore(fraudScore);
					cpd.setFraudNum(businessNo);
					fraudScore = r.getData().get(0).getScore();
					//将反欺诈分数入库
					 this.fraudScoreSave(cpd);
					 
					log.info("反欺诈入库处理成功,业务流水号：" + businessNo);
				} else {
					// 韩迪返回查询错误信息
					log.info("查询反欺诈处理失败,业务流水号：" + businessNo + ",韩迪返回失败原因：" + r.getResCode() + r.getResMsg());
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			csc.setBusinessNo(businessNo);
			csc.setFraudScore(fraudScore);
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
		hd.setChannelOrderId(sendTime);
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
		this.callBackCommon(fraudUrl, fraudjson);

	}
	/**
	 * 反欺诈评分入库
	 */
	public void fraudScoreSave(CreditFraudDic cpd) {		
		creditFraudRepository.save(cpd);	
	}

	public void callBackCommon(String fraudUrl, String json) {

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
