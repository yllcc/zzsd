package cn.com.fotic.eimp.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author liugj
 *
 */
public class HdCreditReturnContentModel implements Serializable {
	private static final long serialVersionUID = 2690024932905963095L;
	    private String resultCode;
	    private String resMsg;
	    private String score;
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

	    public void setScore(String score) {
	         this.score = score;
	     }
	     public String getScore() {
	         return score;
	     }

	    public void setItemId(String itemId) {
	         this.itemId = itemId;
	     }
	     public String getItemId() {
	         return itemId;
	     }

}
