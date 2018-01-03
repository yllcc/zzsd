package cn.com.fotic.eimp.model;

import java.io.Serializable;
/**
 * 信贷调用评分model
 * @author liugj
 *
 */

public class CustomerScoreModel implements Serializable {
	private static final long serialVersionUID = 1221024932905963095L;
	private String  txTime;
	private String serialNo;
	private String platformNo;
	private String  token; //口令
	private CustomerScoreContentModel content;
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
	public CustomerScoreContentModel getContent() {
		return content;
	}
	public void setContent(CustomerScoreContentModel content) {
		this.content = content;
	}


}
