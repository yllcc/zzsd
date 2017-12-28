package cn.com.fotic.eimp.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.springframework.stereotype.Service;

import cn.com.fotic.eimp.model.HdAntiFraudModel;
import cn.com.fotic.eimp.utils.JaxbUtil;
import cn.com.fotic.eimp.utils.VerificationUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author liugj
 *
 */
@Slf4j
@Service
public class CreditstartService {

	private VerificationUtils vt;

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
	 * 翰迪接口，反欺诈 生成xml
	 * 
	 * @return
	 */
	public String HdAntiFraudService() {
		HdAntiFraudModel hd = new HdAntiFraudModel();
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");// 设置日期格式
		String sendTime = df.format(new Date());// new Date()为获取当前系统时间
		hd.setSendTime(sendTime);
		hd.setTransCode("100101");
		hd.setVersion("1.0.0");
		hd.setAddress("");
		hd.setApplication("GwBiz.Req");
		hd.setAddress("");
		hd.setBankCard("");
		hd.setCertNo("");
		hd.setChannelId("");
		hd.setChannelOrderId("100101");
		hd.setEmail("");
		hd.setImei("");
		hd.setIp("127.0.0.1");
		hd.setLinkedMerchantId("");
		hd.setMac("");
		hd.setMobile("");
		hd.setName("");
		hd.setOpenId("");
		hd.setProductItemCode("");
		hd.setWifiMac("");
		String xmlReq = JaxbUtil.convertToXml(hd);
		return xmlReq;
	}
}
