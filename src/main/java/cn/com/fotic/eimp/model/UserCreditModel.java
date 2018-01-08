package cn.com.fotic.eimp.model;

import java.io.Serializable;
import java.util.List;
/**
 * 信贷调用反欺诈 model
 * 
 * @author liugj
 *
 */
public class UserCreditModel implements Serializable {
	private static final long serialVersionUID = 1221024932905963095L;
	private String  reqTime;//请求时间
	private String  flowno; //请求流水号
	private String  accesstoken;//平台编码
	private String  signed; //口令
	private List<UserCreditContentModel> content;
	
	public String getReqTime() {
		return reqTime;
	}
	public void setReqTime(String reqTime) {
		this.reqTime = reqTime;
	}
	public String getFlowno() {
		return flowno;
	}
	public void setFlowno(String flowno) {
		this.flowno = flowno;
	}
	public String getAccesstoken() {
		return accesstoken;
	}
	public void setAccesstoken(String accesstoken) {
		this.accesstoken = accesstoken;
	}
	public String getSigned() {
		return signed;
	}
	public void setSigned(String signed) {
		this.signed = signed;
	}
	public List<UserCreditContentModel> getContent() {
		return content;
	}
	public void setContent(List<UserCreditContentModel> content) {
		this.content = content;
	}

    
}
