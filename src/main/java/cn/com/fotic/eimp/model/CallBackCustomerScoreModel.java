package cn.com.fotic.eimp.model;

import java.io.Serializable;
/**
 * 信贷调用评分model content
 * @author liugj
 *
 */

public class CallBackCustomerScoreModel implements Serializable {
	private static final long serialVersionUID = 1221024932905963095L;
	private String  txTime;//请求时间
	private String  serialNo; //请求流水号
	private String  platformNo;//平台编码
	private String  token; //口令
	private CallBackCustomerScoreContentModel  content;//请求内容
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
	public CallBackCustomerScoreContentModel getContent() {
		return content;
	}
	public void setContent(CallBackCustomerScoreContentModel content) {
		this.content = content;
	}
	
}
