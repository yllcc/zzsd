package cn.com.fotic.eimp.repository.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Data;
import lombok.ToString;

/**
 * 反欺诈信息
 * 
 * @author yangll
 */
@Data
@ToString
@Entity
@Table(name = "CREDIT_FRAUD_INFO")
public class CreditFraudDic {
	@Id
	@SequenceGenerator(name = "SEQ_FRAUD_CREDIT", sequenceName = "SEQ_FRAUD_CREDIT", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_FRAUD_CREDIT")
	private Integer id;// 自增id
	@Column(name="SERIAL_NO")
	private String serialNo;// 流水号
	@Column(name="LOAN_NUM")
	private String loanNum;// 借款单号
	@Column(name="FRAUD_NUM")
	private String fraudNum;// 反欺诈申请单号
	@Column(name="BUSINESS_NO")
	private String businessNo;// 业务号
	@Column(name="CUST_NAME")
	private String custName;// 姓名
	@Column(name="CERT_TYPE",length=2)
	private String certType;// 证件类型
	@Column(name="CERT_NUM")
	private String certNum;// 证件号
	@Column(name="PHONE")
	private String phone;// 手机号
	@Column(name="EMAIL")
	private String email;// 邮箱
	@Column(name="BANK_CARD")
	private String bankCard;// 银行卡
	@Column(name="ADDRESS")
	private String address;// 地址
	@Column(name="APPLY_TIME")
	private Date applyTime;// 申请时间
	@Column(name="APPLY_PERSON")
	private String appltPerson;// 申请人
	@Column(name="FRAUD_SCORE")
	private String fraudScore;// 反欺诈评分
	@Column(name="SCORE")
	private String score;// 信贷系统评分
	@Column(name="CHECK_TIME")
	private Date checkTime;// 查证时间
	@Column(name="RESPONSE_BODY")
	private String responseBody;
	@Column(name="IDENTIFY_CODE")
	private String identifyCode;// 验证码
	@Column(name="IDENTIFY_INFO")
	private String identifyInfo;//验证信息
	@Column(name="ATTRIBUTES1")
	private String attributes1;// 字段1
	@Column(name="ATTRIBUTES2")
	private String attributes2;// 字段2
}
