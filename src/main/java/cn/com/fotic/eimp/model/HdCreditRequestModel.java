package cn.com.fotic.eimp.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lombok.Data;
/**
 * 翰迪征信请求消息头
 * @author yangll
 *
 */
@Data
@XmlRootElement(name = "subatm")    
@XmlType(propOrder = {     
     "application",     
     "version",     
     "sendTime",     
     "transCode",     
     "channelId",
     "channelOrderId",     
     "name",     
     "cid",     
     "mobile",
     "cardNo"
})  
public class HdCreditRequestModel implements Serializable {

	private static final long serialVersionUID = 3463418243141184661L;
	private String application;// 应用名称
	private String version;// 通讯协议版本号
	private String sendTime;// 发送时间 n14
	private String transCode;// 交易代码 N6
	private String channelId;// 渠道代码
	private String channelOrderId;// 渠道订单Id
	private String name;// 姓名
	private String cid;// 身份证号
	private String mobile;// 手机号
	private String cardNo;// 银行卡号
}
