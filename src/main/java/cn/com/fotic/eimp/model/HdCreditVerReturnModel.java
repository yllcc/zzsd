package cn.com.fotic.eimp.model;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * @author liugj
 *
 */
public class HdCreditVerReturnModel implements Serializable {
	private static final long serialVersionUID = 2690024932905963095L;
	private String resCode;
	private String resMsg;
    private List<HdCreditVerReturnContentModel> data;
	public String getResCode() {
		return resCode;
	}
	public void setResCode(String resCode) {
		this.resCode = resCode;
	}
	public String getResMsg() {
		return resMsg;
	}
	public void setResMsg(String resMsg) {
		this.resMsg = resMsg;
	}
	public List<HdCreditVerReturnContentModel> getData() {
		return data;
	}
	public void setData(List<HdCreditVerReturnContentModel> data) {
		this.data = data;
	}
	
    
}
