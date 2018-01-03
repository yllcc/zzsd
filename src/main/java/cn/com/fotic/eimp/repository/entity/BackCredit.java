package cn.com.fotic.eimp.repository.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import lombok.Data;
import lombok.ToString;

/**
 * 查询人行信息
 */

@Data
@ToString
@Entity
@Table(name = "BACK_CREDIT")
public class BackCredit {

	@Id
	@Column(name = "CUST_NAME")
	private String cust_name;// 客户名称
	
	@Column(name = "CERT_TYPE")
	private String cert_type;// 证件类型
	
	@Column(name = "CERT_NUM")
	private String cert_num;// 证件号码
	
	@Column(name = "RATE_CREDITCARD_APPROVAL_COUNT")
	private String rate_creditcard_approval_count;// 信用卡审批次数
	
	@Column(name = "RATE_CREDITREPOR_COUNT")
	private String rate_creditrepor_count;// 近3个月贷款审批次数
	
	@Column(name = "RATE_LOANOFF_LOANOPEN_RATIO")
	private String rate_loanoff_loanopen_ratio;// 过去贷款结清数与过去贷款开户数之比
	
	@Column(name = "RATE_MARITAL_STATE")
	private String rate_marital_state;// 婚姻状态
	
	@Column(name = "RATE_EDU_LEVEL")
	private String rate_edu_level;// 学历
	
	@Column(name = "RATE_NOACCOUNT_FIRSTEND_BAL")
	private String rate_noaccount_firstend_bal;// 未销户贷记卡最早额度与最近额度之差'
	
	@Column(name = "RATE_FIVEYEAR_MAXOVERDUE_COUNT")
	private String rate_fiveyear_maxoverdue_count;// 五年内最大逾期次数'
	
	@Column(name = "RATE_REGISTER")
	private String rate_register;// 性别
	
	@Column(name = "RATE_CREDIT_ACCOUNT_COUNT")
	private String rate_credit_account_count;// '信贷账户数
	
	@Column(name = "RATE_NORMAL_AVENOTUSEDLIMITRAT")
	private String rate_normal_avenotusedlimitrat;// 正常贷记卡平均未用额度组占比
	
	@Column(name = "RATE_RECENTLY_OPENCARD_LIMIT")
	private String rate_recently_opencard_limit;// 最近开户贷记卡额度
	
	@Column(name = "RATE_FIRSTNOACCOUNT_CARDAGE")
	private String rate_firstnoaccount_cardage;// 最早未销户贷记卡卡龄

}
