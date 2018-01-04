package cn.com.fotic.eimp.model;

import java.io.Serializable;

public class CallBackUserCreditModel implements Serializable {
	private static final long serialVersionUID = 2620024932905963095L;
	private String  txTime;//合作机构号
	private String  serialNo; //接口编号
	private String  platformNo;//请求日期
	private String  token; //口令
	private CallBackUserCreditContentModel  content;//请求内容
	public String getTxTime() {
		return txTime;
	}
	public void setTxTime(String txTime) {
		this.txTime = txTime;
	}
	public String getSerialNo() {
		return serialNo;
	}
	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}
	public String getPlatformNo() {
		return platformNo;
	}
	public void setPlatformNo(String platformNo) {
		this.platformNo = platformNo;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public CallBackUserCreditContentModel getContent() {
		return content;
	}
	public void setContent(CallBackUserCreditContentModel content) {
		this.content = content;
	}
	
	
	
}
