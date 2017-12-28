package cn.com.fotic.eimp.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
//import org.eclipse.persistence.oxm.annotations.XmlCDATA;

@XmlRootElement(name = "subatm")
@XmlAccessorType(XmlAccessType.FIELD)
public class HdAntiFraudModel {
	@XmlElement
	private String Application;// 应用名称
	@XmlElement
	private String version; // 通讯协议版本号
	@XmlElement
	private String sendTime;// 发送时间
	@XmlElement
	private String transCode;// 交易代码
	@XmlElement
	private String channelId; // 渠道代码
	@XmlElement
	private String channelOrderId;// 渠道订单id
	@XmlElement
	private String certNo;// 身份证号码
	@XmlElement
	private String productItemCode;// 产品子项
	@XmlElement
	private String openId; // openid
	@XmlElement
	private String linkedMerchantId;// 商户id
	@XmlElement
	private String name;// 姓名
	@XmlElement
	private String mobile; // 手机号
	@XmlElement
	private String email;// 邮箱
	@XmlElement
	private String bankCard;// 银行卡
	@XmlElement
	private String address;// 地址
	@XmlElement
	private String ip;// IP
	@XmlElement
	private String mac; // 物理地址
	@XmlElement
	private String wifiMac;// wifi物理地址
	@XmlElement
	private String imei;// 国际移动设备标志

	public String getApplication() {
		return Application;
	}

	public void setApplication(String application) {
		Application = application;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getSendTime() {
		return sendTime;
	}

	public void setSendTime(String sendTime) {
		this.sendTime = sendTime;
	}

	public String getTransCode() {
		return transCode;
	}

	public void setTransCode(String transCode) {
		this.transCode = transCode;
	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public String getChannelOrderId() {
		return channelOrderId;
	}

	public void setChannelOrderId(String channelOrderId) {
		this.channelOrderId = channelOrderId;
	}

	public String getCertNo() {
		return certNo;
	}

	public void setCertNo(String certNo) {
		this.certNo = certNo;
	}

	public String getProductItemCode() {
		return productItemCode;
	}

	public void setProductItemCode(String productItemCode) {
		this.productItemCode = productItemCode;
	}

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public String getLinkedMerchantId() {
		return linkedMerchantId;
	}

	public void setLinkedMerchantId(String linkedMerchantId) {
		this.linkedMerchantId = linkedMerchantId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getBankCard() {
		return bankCard;
	}

	public void setBankCard(String bankCard) {
		this.bankCard = bankCard;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public String getWifiMac() {
		return wifiMac;
	}

	public void setWifiMac(String wifiMac) {
		this.wifiMac = wifiMac;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

}
