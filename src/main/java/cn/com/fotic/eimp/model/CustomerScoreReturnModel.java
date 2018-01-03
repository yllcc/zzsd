package cn.com.fotic.eimp.model;

import java.io.Serializable;
/**
 * 信贷调用客户评分 返回响应model
 * 
 * @author liugj
 *
 */
public class CustomerScoreReturnModel implements Serializable {
	private static final long serialVersionUID = 1221024932905963095L;
	private String  businessNo;
	private String fraudScore;
	public String getBusinessNo() {
		return businessNo;
	}
	public void setBusinessNo(String businessNo) {
		this.businessNo = businessNo;
	}
	public String getFraudScore() {
		return fraudScore;
	}
	public void setFraudScore(String fraudScore) {
		this.fraudScore = fraudScore;
	}
	

}
