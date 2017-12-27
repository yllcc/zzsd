package cn.com.fotic.eimp.repository.model;

public class UserCreditReturnModel {
	private String  reCode;//01-接收成功  02-接收失败
	private String  reDesc; //相关描述
	private String  errorcode;
	private String  errormsg;
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
	public String getErrorcode() {
		return errorcode;
	}
	public void setErrorcode(String errorcode) {
		this.errorcode = errorcode;
	}
	public String getErrormsg() {
		return errormsg;
	}
	public void setErrormsg(String errormsg) {
		this.errormsg = errormsg;
	}

	
	

}
