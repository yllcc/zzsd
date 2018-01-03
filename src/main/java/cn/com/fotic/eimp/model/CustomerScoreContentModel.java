package cn.com.fotic.eimp.model;

import java.io.Serializable;
/**
 * 信贷调用评分model content
 * @author liugj
 *
 */

public class CustomerScoreContentModel implements Serializable {
	private static final long serialVersionUID = 1221024932905963095L;
	private String  businessNo;
	private String idType;
	private String idNo;
	private String custName;
	private String phoneNo;
    
	public String getBusinessNo() {
		return businessNo;
	}
	public void setBusinessNo(String businessNo) {
		this.businessNo = businessNo;
	}
	public String getIdType() {
		return idType;
	}
	public void setIdType(String idType) {
		this.idType = idType;
	}
	public String getIdNo() {
		return idNo;
	}
	public void setIdNo(String idNo) {
		this.idNo = idNo;
	}
	public String getCustName() {
		return custName;
	}
	public void setCustName(String custName) {
		this.custName = custName;
	}
	public String getPhoneNo() {
		return phoneNo;
	}
	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}
	

}
