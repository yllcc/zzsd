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
 * 征信信息
 * @author yangll
 *
 */
@Data
@ToString
@Entity
@Table(name = "CREDIT_PERSONAL_INFO")
public class CreditPersonalDic {
	@Id
	@SequenceGenerator(name = "SEQ_CREDIT_PERSONAL", sequenceName = "SEQ_CREDIT_PERSONAL", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_CREDIT_PERSONAL")
	private Integer id;// 自增id
	@Column(name="SERIAL_NO")
	private String serialNo;// 流水号
	@Column(name="BUSINESS_NO")
	private String businessNo;// 业务号
	@Column(name="LOAN_NUM")
	private String loanNum;// 借款单号
	@Column(name="APPLY_NUM")
	private String applyNum;//申请编号
	@Column(name="APPLY_TIME")
	private Date applyTime;//申请时间
	@Column(name="CUST_NAME")
	private String custName;// 姓名
	@Column(name="CERT_TYPE")
	private String certType;// 证件类型
	@Column(name="CERT_NUM")
	private String certNum;// 证件号
	@Column(name="PHONE")
	private String phone;// 手机号
	@Column(name="APPLY_PERSON")
	private String appltPerson;//申请人
	@Column(name="CREDIT_SCORE")
	private String creditScore;//征信评分
	@Column(name="CHECK_TIME")
	private Date checkTime;//查征时间
	@Column(name="STABILITY")
	private String stability;//稳定性 (极强、强、一般、 弱、极弱，分别 对应[1,2,3,4,5]
	@Column(name="BUYING_INDEX")
	private String buyingIndex;//购买力指数
	@Column(name="RISK_INDEX")
	private String riskIndex;//交易风险指数
	@Column(name="PERFORMANCE_INDEX")
	private String performanceIndex;//履约指数 
	@Column(name="RESONABLE_CONSUMING")
	private String resonableConsuming;//理性消费指数
	@Column(name="CITY")
	private String city;//常驻城市
	@Column(name="CITY_STABILITY")
	private String cityStability;//地址一致性校验 (是、否分别对应 [1,0])
	@Column(name="ONLINE_BUYING")
	private String onlineBuy;//网购指数
	@Column(name="COMSUMING_SOCIAL")
	private String comsumSocial;//消费社交影响力
	@Column(name="INCOME")
	private String income;//收入水平
	@Column(name="RISK_PERIOD_CONSUME")
	private String riskPeriodConsume;//高危时段消费指 数
	@Column(name="RISK_CATEGORY_CONSUME")
	private String riskCategoryComsume;//高危品类消费指 数
	@Column(name="WORKTIME_SHOPPING")
	private String workTimeShop;//工作时间网购偏 好
	@Column(name="CELLPHONE_PREFERENCE")
	private String cellPhone_preference;//手机依赖度
	@Column(name="ECOMMERCE_ACTIVENESS")
	private String ecommerceActiveness;//电商 活跃度
	@Column(name="ECOMMERCE_ADDRESS_STABILITY")
	private String ecommerceAddressStability;//电商收货地址稳定性(极强、强、一般、 弱、极弱 分别对 应[1,2,3,4,5])
	@Column(name="ECOMMERCE_CELLPHONE_STABILITY")
	private String ecommerceCellPhoneStability;//电商联系方式稳 定性((极强、强、一般、 弱、极弱 分别对 应[1,2,3,4,5]))
	@Column(name="ECOMMERCE_ACCOUNT_HISTORY")
	private String ecommerceAccountHistory;//电商账龄等级
	@Column(name="CASH_PREFERENCE")
	private String cashPreference;//现金偏好程度
	@Column(name="RISK_PERIOD_PAYMENT")
	private String riskPeriodPayment;//高危时段支付指 数
	@Column(name="RISK_CATEGORY_PAYMENT")
	private String riskCategoryPayment;//高危品类支付指 数
	@Column(name="BANK_CARD_STABILITY")
	private String bankCardStability;//银行卡稳定性
	@Column(name="BANK_CARD_ACTIVENESS")
	private String bankCardActiveness;//银行卡活跃度
	@Column(name="BANK_CARD_HISTORY")
	private String bankCardHistory;//银行账龄等级
	@Column(name="IS_TARGERT")
	private String isTargert;//是否受行业关注（1是2否）
	@Column(name="ATTRIBUTES1")
	private String attributes1;// 字段1
	@Column(name="ATTRIBUTES2")
	private String attributes2;// 字段2
}
