package cn.com.fotic.eimp.model;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author liugj
 *
 */
public class HdCreditVerReturnContentModel implements Serializable {
	private static final long serialVersionUID = 2690024932905963095L;
	    private String resultCode;
	    private String resMsg;
	    private List<String> verifyCode;
	    private String itemId;
	    public void setResultCode(String resultCode) {
	         this.resultCode = resultCode;
	     }
	     public String getResultCode() {
	         return resultCode;
	     }

	    public void setResMsg(String resMsg) {
	         this.resMsg = resMsg;
	     }
	     public String getResMsg() {
	         return resMsg;
	     }
	   
		public List<String> getVerifyCode() {
			return verifyCode;
		}
		public void setVerifyCode(List<String> verifyCode) {
			this.verifyCode = verifyCode;
		}
		public void setItemId(String itemId) {
	         this.itemId = itemId;
	     }
	     public String getItemId() {
	         return itemId;
	     }

}
