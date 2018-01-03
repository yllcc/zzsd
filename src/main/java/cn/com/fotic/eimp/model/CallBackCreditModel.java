package cn.com.fotic.eimp.model;

import java.io.Serializable;

public class CallBackCreditModel implements Serializable {
	private static final long serialVersionUID = 2620024932905963095L;
	private String  brNo;//合作机构号
	private String  txCode; //接口编号
	private String  reqDate;//请求日期
	private String  reqTime;//请求时间
	private String  token; //口令
	private String  reqSerial;//流水号
	private String  content;//请求内容
	public String getBrNo() {
		return brNo;
	}
	public void setBrNo(String brNo) {
		this.brNo = brNo;
	}
	public String getTxCode() {
		return txCode;
	}
	public void setTxCode(String txCode) {
		this.txCode = txCode;
	}
	public String getReqDate() {
		return reqDate;
	}
	public void setReqDate(String reqDate) {
		this.reqDate = reqDate;
	}
	public String getReqTime() {
		return reqTime;
	}
	public void setReqTime(String reqTime) {
		this.reqTime = reqTime;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getReqSerial() {
		return reqSerial;
	}
	public void setReqSerial(String reqSerial) {
		this.reqSerial = reqSerial;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	

	
	

}
