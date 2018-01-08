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
	@Column(name="SERIAL_NO")
	private String  serialNo;//流水号
	@Column(name="CUST_NAME")
	private String  custName   ;//客户名称'
	@Column(name="CUST_Type")
	private String  certType  ;//证件类型
	@Column(name="CUST_NUM")
	private String  certNum  ;//证件号码
	@Column(name="RATE_CREDITCARD_APPROVAL_COUNT")
	private String  rateCreditcardApprovalCont;//'信用卡审批次数
	@Column(name="RATE_CREDITREPOR_COUNT")
	private String   rateCreditreporCount  ;//近3个月贷款审批次数
	@Column(name="RATE_LOANOFF_LOANOPEN_RATIO")
	private String  rateLoanoffLoanopenRatio;//过去贷款结清数与过去贷款开户数之比'
	@Column(name="RATE_MARITAL_STATE")
	private String rateMaritalState  ;//婚姻状态
	@Column(name="RATE_EDU_LEVEL")
	private String rateEduLevel ;//学历
	@Column(name="RATE_NOACCOUNT_FIRSTEND_BAL")
	private String rateNoaccountFirstendBal ;//'未销户贷记卡最早额度与最近额度之差'
	@Column(name="RATE_FIVEYEAR_MAXOVERDUE_COUNT")
	private String rateFiveyearMaxoverdueCount ;//五年内最大逾期次数'
	@Column(name="RATE_REGISTER")
	private String  rateRegister;//性别
	@Column(name="RATE_CREDIT_ACCOUNT_COUNT")
	private String  rateCreditAccountCount;//'信贷账户数
	@Column(name="RATE_NORMAL_AVENOTUSEDLIMITRAT")
	private String  rateNormalAvenotusedlimitrat;//正常贷记卡平均未用额度组占比
	@Column(name="RATE_RECENTLY_OPENCARD_LIMIT")
	private String  rateRecentlyOpencardLimit;//最近开户贷记卡额度
	@Column(name="RATE_FIRSTNOACCOUNT_CARDAGE")
	private String rateFirstnoaccountCardage ;//最早未销户贷记卡卡龄
	@Column(name="APPLY_TIME")
	private Date applyTime;//申请时间
	@Column(name="APPLY_PERSON")
	private String  applyPerson;//申请人
	@Column(name="ATTRIBUTES1")
	private String  attributes1;//字段1
	@Column(name="ATTRIBUTES1")
	private String  attributes2;//字段2


}
