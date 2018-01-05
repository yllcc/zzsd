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
	private String serialNo;// 流水号
	private String businessNo;// 业务号
	private String loanNum;// 借款单号
	private String applyNum;//申请编号
	private Date applyTime;//申请时间
	private String custName;// 姓名
	private String certType;// 证件类型
	private String certNum;// 证件号
	private String phone;// 手机号
	private String appltPerson;//申请人
	private String creditScore;//征信评分
	private Date checkTime;//查征时间
	private String stability;//稳定性 (极强、强、一般、 弱、极弱，分别 对应[1,2,3,4,5]
	private String buyingIndex;//购买力指数
	private String riskIndex;//交易风险指数
	private String performanceIndex;//履约指数 
	private String resonableConsuming;//理性消费指数
	private String city;//常驻城市
	private String cityStability;//地址一致性校验 (是、否分别对应 [1,0])
	private String onlineBuy;//网购指数
	private String comsumSocial;//消费社交影响力
	private String income;//收入水平
	private String riskPeriodConsume;//高危时段消费指 数
	private String riskCategoryComsume;//高危品类消费指 数
	private String workTimeShop;//工作时间网购偏 好
	private String cellPhone_preference;//手机依赖度
	private String ecommerceActiveness;//电商 活跃度
	private String ecommerceAddressStability;//电商收货地址稳定性(极强、强、一般、 弱、极弱 分别对 应[1,2,3,4,5])
	private String ecommerceCellPhoneStability;//电商联系方式稳 定性((极强、强、一般、 弱、极弱 分别对 应[1,2,3,4,5]))
	private String ecommerceAccountHistory;//电商账龄等级
	private String cashPreference;//现金偏好程度
	private String riskPeriodPayment;//高危时段支付指 数
	private String riskCategoryPayment;//高危品类支付指 数
	private String bankCardStability;//银行卡稳定性
	private String bankCardActiveness;//银行卡活跃度
	private String bankCardHistory;//银行账龄等级
	private String isTargert;//是否受行业关注（1是2否）
	private String attributes1;// 字段1
	private String attributes2;// 字段2

}
