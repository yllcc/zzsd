package cn.com.fotic.eimp;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import cn.com.fotic.eimp.primary.CreditPersonalRepository;
import cn.com.fotic.eimp.repository.entity.CreditPersonalDic;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CreditPersonalRepositoryTest.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@EnableAutoConfiguration
public class CreditPersonalRepositoryTest {
	@Autowired(required=true)
	private CreditPersonalRepository creditPersonalRepository;
	@Test
	public void  save() {
		CreditPersonalDic personaldic=new CreditPersonalDic();
		personaldic.setSerialNo("121");;// 流水号
		personaldic.setBusinessNo("121");// 业务号
		
		personaldic.setLoanNum("222");// 借款单号
		personaldic.setApplyNum("as");//申请编号
		personaldic.setApplyTime(new Date());//申请时间
		personaldic.setCustName("as");// 姓名
		personaldic.setCertType("1");// 证件类型
		personaldic.setCertNum("12121");// 证件号
		personaldic.setPhone("18310088172");// 手机号
		personaldic.setApplyPerson("1221");;//申请人
		personaldic.setCreditScore("11");//征信评分
		personaldic.setCheckTime(new Date());//查征时间
		personaldic.setStability("1");;//稳定性 (极强、强、一般、 弱、极弱，分别 对应[1,2,3,4,5]
		personaldic.setBuyingIndex("12");;//购买力指数
		personaldic.setRiskIndex("12");;//交易风险指数
		personaldic.setPerformanceIndex("12");;//履约指数 
		personaldic.setResonableConsuming("12");;//理性消费指数
		personaldic.setCity("1");;//常驻城市
		personaldic.setCityStability("1");;//地址一致性校验 (是、否分别对应 [1,0])
		personaldic.setOnlineBuy("qwqw");//网购指数
		personaldic.setComsumSocial("q");;//消费社交影响力
		personaldic.setIncome("12");//收入水平
		personaldic.setRiskPeriodConsume("1");//高危时段消费指 数
		personaldic.setRiskCategoryComsume("12");;//高危品类消费指 数
		personaldic.setWorkTimeShop("12");//工作时间网购偏 好
		personaldic.setCellPhone_preference("1");//手机依赖度
		personaldic.setEcommerceActiveness("12");//电商 活跃度
		personaldic.setEcommerceAddressStability("1");//电商收货地址稳定性(极强、强、一般、 弱、极弱 分别对 应[1,2,3,4,5])
		personaldic.setEcommerceCellPhoneStability("1");;//电商联系方式稳 定性((极强、强、一般、 弱、极弱 分别对 应[1,2,3,4,5]))
		personaldic.setEcommerceAccountHistory("1");//电商账龄等级
		personaldic.setCashPreference("12");;//现金偏好程度
		personaldic.setRiskPeriodPayment("12");//高危时段支付指 数
		personaldic.setRiskCategoryPayment("12");//高危品类支付指 数
		personaldic.setBankCardStability("1");;//银行卡稳定性
		personaldic.setBankCardActiveness("12");//银行卡活跃度
		personaldic.setBankCardHistory("1");//银行账龄等级
		personaldic.setIsTargert("1");;//是否受行业关注（1是2否）
		personaldic.setAttributes1("1");// 字段1
		personaldic.setAttributes2("2");;// 字段2
		creditPersonalRepository.save(personaldic);
	}
}
