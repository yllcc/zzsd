package cn.com.fotic.eimp.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import cn.com.fotic.eimp.model.HdCreditRequestModel;
import cn.com.fotic.eimp.model.UserCreditQueneModel;
import cn.com.fotic.eimp.utils.Base64Utils;
import cn.com.fotic.eimp.utils.HttpUtil;
import cn.com.fotic.eimp.utils.JaxbUtil;
import cn.com.fotic.eimp.utils.Md5Utils;
import cn.com.fotic.eimp.utils.RSAUtils;
import cn.com.fotic.eimp.utils.ThreeDESUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * 翰迪征信接口类
 * @author yangll
 *
 */
@Slf4j
@Service
public class CreditPersonalService {
	
	private final String key="MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAIGm7bNehbstkfG/fAcZnrA3UGHVWjCRkP3S3/ZfF456ypdRAaEeqGILT+wB139K1HZIUy/Gl8slGS9r6TR961MCAwEAAQ==";

	private final String URL = "https://ds.handydata.cn/ds/service.ac";//翰迪征信接口地址
	
	private final String application ="GwBiz.Req";//应用名称

	private final String  version ="1.0.0";//当前版本取值
	private final String  transCode ="300618";//固定交易代码
	
	/**
	 * 获取请求翰迪征信xml
	 * @param UserCreditQueneModel
	 * @return xml
	 */
	public String getCreditRequestXml(UserCreditQueneModel model) {
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");// 设置日期格式
		String sendTime = df.format(new Date());// new Date()为获取当前系统时间
		HdCreditRequestModel requestModel=new HdCreditRequestModel();
		requestModel.setApplication(application);
		requestModel.setVersion(version);
		requestModel.setSendTime(sendTime);
		requestModel.setTransCode(transCode);
		requestModel.setChannelId("11111100011");
		requestModel.setChannelOrderId(sendTime);
		requestModel.setName(model.getCustName());
		requestModel.setCid(model.getIdNo());
		requestModel.setMobile(model.getPhoneNo());
		requestModel.setCardNo("");//银行卡号 选填
//		requestModel.setName("张三");
//		requestModel.setCid("120101199801017858");
//		requestModel.setMobile("18693152204");
		//requestModel.setCardNo("622301199002040312");
		String xmlReq = JaxbUtil.convertToXml(requestModel);
		return xmlReq;
	}
	
	
	/**
	 * 翰迪征信请求
	 * @param xml
	 * @return
	 * @throws Exception
	 */
	public String sendHdCredit(String requestXml) throws Exception {
		String channelId = "11009035";//TODO  渠道号
		
		String mkey = UUID.randomUUID().toString();

		//BASE64(RSA(报文加密密钥))
		String rsaXml = RSAUtils.encryptByPublicKey(new String(mkey.getBytes(), "utf-8"), key);
		//BASE64(3DES(报文原文))
		String desXml = new String(
				Base64Utils.encode(ThreeDESUtils.encrypt(requestXml.toString().getBytes("utf-8"), mkey.getBytes())));

		// 加密报文体格式：BASE64(商户号)| BASE64(RSA(报文加密密钥))| BASE64(3DES(报文原文))
		String returnXml = new String(Base64Utils.encode(channelId.getBytes("utf-8"))) + "|" + rsaXml + "|" + desXml;
		String reutrnResult = HttpUtil.sendXMLDataByPost(URL, returnXml);
		String errormsg="";
		if(StringUtils.isNotEmpty(reutrnResult)) {
			String xmlArr[] = reutrnResult.split("\\|");

			if ("0".equals(xmlArr[0])) {
				errormsg = new String(Base64Utils.decode(xmlArr[2]), "utf-8");
				return errormsg;
			} else {
				byte[] reponseByte = ThreeDESUtils.decrypt(Base64Utils.decode(xmlArr[1]), mkey.getBytes());
				String reponseXml = new String(reponseByte, "utf-8");
				log.info("3DES(报文)" + reponseXml);
				log.info("MD5(报文)" + new String(Base64Utils.encode(Md5Utils.md5ToHexStr(reponseXml).getBytes("utf-8"))));
				return reponseXml;
			}
		}else {
			errormsg="未接收到返回信息";
			return errormsg;
		}
	}
	
	
}
