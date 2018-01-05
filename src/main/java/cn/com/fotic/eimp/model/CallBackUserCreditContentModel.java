package cn.com.fotic.eimp.model;

import java.io.Serializable;

public class CallBackUserCreditContentModel implements Serializable {
	private static final long serialVersionUID = 2620024932905963095L;
	private String  businessNo;//平台流水号
	private String  fraudScore; //反欺诈评分
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
