package cn.com.fotic.eimp.model;

import java.io.Serializable;

public class CallBackCreditReturnModel implements Serializable {
	private static final long serialVersionUID = 2620024932901233095L;
	private String respCode; //响应码
	private String  respDesc; //响应描述
	private String  content;//响应内容
	public String getRespCode() {
		return respCode;
	}
	public void setRespCode(String respCode) {
		this.respCode = respCode;
	}
	public String getRespDesc() {
		return respDesc;
	}
	public void setRespDesc(String respDesc) {
		this.respDesc = respDesc;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}

	
	
	

}
