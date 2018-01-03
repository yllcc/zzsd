package cn.com.fotic.eimp.model;

import java.io.Serializable;
/**
 * 信贷调用评分回调model content
 * @author liugj
 *
 */

public class CallBackCustomerScoreContentModel implements Serializable {
	private static final long serialVersionUID = 1221024932905963095L;
	private String  businessNo;//请求流水号
	private String  customScore;//征信评分
	public String getBusinessNo() {
		return businessNo;
	}
	public void setBusinessNo(String businessNo) {
		this.businessNo = businessNo;
	}
	public String getCustomScore() {
		return customScore;
	}
	public void setCustomScoree(String customScore) {
		this.customScore = customScore;
	}
    
	

}
