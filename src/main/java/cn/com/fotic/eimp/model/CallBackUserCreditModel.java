package cn.com.fotic.eimp.model;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * @author liugj
 *
 */
public class CallBackUserCreditModel implements Serializable {
	private static final long serialVersionUID = 2620024932905963095L;
	private String  reqTime;//请求时间
	private String  flowNo; //请求流水号
	private String  accessToken;//平台编码
	private String  signed; //口令
	private List<CallBackUserCreditContentModel> content;// 请求内容
	public String getReqTime() {
		return reqTime;
	}

	public void setReqTime(String reqTime) {
		this.reqTime = reqTime;
	}

	public String getFlowNo() {
		return flowNo;
	}

	public void setFlowNo(String flowNo) {
		this.flowNo = flowNo;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getSigned() {
		return signed;
	}

	public void setSigned(String signed) {
		this.signed = signed;
	}

	public List<CallBackUserCreditContentModel> getContent() {
		return content;
	}

	public void setContent(List<CallBackUserCreditContentModel> content) {
		this.content = content;
	}

}
