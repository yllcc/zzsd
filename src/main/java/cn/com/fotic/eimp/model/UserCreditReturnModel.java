package cn.com.fotic.eimp.model;

import java.io.Serializable;

/**
 * 信贷调用反欺诈 返回响应model
 * 
 * @author liugj
 *
 */
public class UserCreditReturnModel implements Serializable {
	private static final long serialVersionUID = 1620024931115963095L;
	private String  reCode;//01-接收成功  02-接收失败
	private String  reDesc; //相关描述
	public String getReCode() {
		return reCode;
	}
	public void setReCode(String reCode) {
		this.reCode = reCode;
	}
	public String getReDesc() {
		return reDesc;
	}
	public void setReDesc(String reDesc) {
		this.reDesc = reDesc;
	}

}
