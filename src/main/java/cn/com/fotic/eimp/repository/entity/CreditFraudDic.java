package cn.com.fotic.eimp.repository.entity;

import java.util.Date;

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
	private String serial_No;// 流水号
	private String loanNum;// 借款单号
	private String fraudNum;// 反欺诈申请单号
	private String businessNo;// 业务号
	private String custName;// 姓名
	private String certType;// 证件类型
	private String certNum;// 证件号
	private String phone;// 手机号
	private String email;// 邮箱
	private String bankCard;// 银行卡
	private String address;// 地址
	private Date appltTime;// 申请时间
	private String appltPerson;// 申请人
	private String fraudScore;// 反欺诈评分
	private Date checkTime;// 查证时间
	private String attributes1;// 字段1
	private String attributes2;// 字段2
}
