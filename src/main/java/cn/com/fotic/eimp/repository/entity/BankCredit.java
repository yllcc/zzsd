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
 * 人行征信记录表
 * @author yangll
 */
@Data
@ToString
@Entity
@Table(name = "BANK_CREDIT")
public class BankCredit {
	@Id
	@SequenceGenerator(name = "SEQ_BANK_CREDIT", sequenceName = "SEQ_BANK_CREDIT",allocationSize=1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_BANK_CREDIT")  
	private Integer id;
	private String  serial_No;//流水号
	private String  cust_name   ;//客户名称'
	private String  cert_type  ;//证件类型
	private String  cert_num  ;//证件号码
	private String  RATE_CREDITCARD_APPROVAL_COUNT;//'信用卡审批次数
	private String  RATE_CREDITREPOR_COUNT   ;//近3个月贷款审批次数
	private String  RATE_LOANOFF_LOANOPEN_RATIO;//过去贷款结清数与过去贷款开户数之比'
	private String  RATE_MARITAL_STATE ;//婚姻状态
	private String  RATE_EDU_LEVEL;//学历
	private String  RATE_NOACCOUNT_FIRSTEND_BAL;//'未销户贷记卡最早额度与最近额度之差'
	private String  RATE_FIVEYEAR_MAXOVERDUE_COUNT;//五年内最大逾期次数'
	private String  RATE_REGISTER;//性别
	private String  RATE_CREDIT_ACCOUNT_COUNT;//'信贷账户数
	private String  RATE_NORMAL_AVENOTUSEDLIMITRAT;//正常贷记卡平均未用额度组占比
	private String  RATE_RECENTLY_OPENCARD_LIMIT;//最近开户贷记卡额度
	private String  RATE_FIRSTNOACCOUNT_CARDAGE;//最早未销户贷记卡卡龄
	private String  creator;//创建人
	private Date created_time;//创建时间
	private String  updater;//修改人
	private Date updated_time;//修改时间
	private String  attributes1;//字段1
	private String  attributes2;//字段2


}
