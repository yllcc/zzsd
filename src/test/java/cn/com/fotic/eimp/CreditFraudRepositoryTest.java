package cn.com.fotic.eimp;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import cn.com.fotic.eimp.primary.CreditFraudRepository;
import cn.com.fotic.eimp.repository.entity.CreditFraudDic;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = CreditFraudRepositoryTest.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@EnableAutoConfiguration
public class CreditFraudRepositoryTest {
	@Autowired(required=true)
	private CreditFraudRepository creditFraudRepository;
	@Test
	public void save() {
		CreditFraudDic creditFraudDic=new CreditFraudDic();
		creditFraudDic.setLoanNum("1221");// 借款单号
		creditFraudDic.setFraudNum("1312");// 反欺诈申请单号
		creditFraudDic.setBusinessNo("sa");// 业务号
		creditFraudDic.setCustName("asdas");// 姓名
		creditFraudDic.setCertType("1");// 证件类型
		creditFraudDic.setCertNum("1211");// 证件号
		creditFraudDic.setPhone("18310088172");// 手机号
		creditFraudDic.setEmail("112");// 邮箱
		creditFraudDic.setBankCard("11");// 银行卡
		creditFraudDic.setAddress("1");// 地址
		creditFraudDic.setApplyTime(new Date());;// 申请时间
		creditFraudDic.setAppltPerson("12");// 申请人
		creditFraudDic.setFraudScore("1");// 反欺诈评分
		creditFraudDic.setCheckTime(new Date());// 查证时间
		creditFraudDic.setAttributes1("11");// 字段1
		creditFraudDic.setAttributes2("121");// 字段2
		creditFraudDic.setSerialNo("12");
		creditFraudRepository.save(creditFraudDic);
	}
}