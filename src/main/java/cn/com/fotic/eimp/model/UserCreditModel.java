package cn.com.fotic.eimp.model;

import java.io.Serializable;
/**
 * 信贷调用反欺诈 model
 * 
 * @author liugj
 *
 */
public class UserCreditModel implements Serializable {
	private static final long serialVersionUID = 1221024932905963095L;
	private String  txTime;
	private String serialNo;
	private String platformNo;
	private String  token; //口令
	private UserCreditContentModel content;
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
	public UserCreditContentModel getContent() {
		return content;
	}
	public void setContent(UserCreditContentModel content) {
		this.content = content;
	}
    
}
