package cn.com.fotic.eimp;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import cn.com.fotic.eimp.repository.BankCreditRepository;
import cn.com.fotic.eimp.repository.entity.BankCredit;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = BankCreditRepository.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@EnableAutoConfiguration
public class BankCreditRepositoryTest {
	@Autowired(required=true)
	private BankCreditRepository BankCreditRepository;
	@Test
	public void save() {
		BankCredit bankCredit=new BankCredit();
		bankCredit.setId("2");
		bankCredit.setSerial_No("sasdasas");
		bankCredit.setCust_name("asd");
		bankCredit.setCert_type("2");
		bankCredit.setCert_num("12312312");
		bankCredit.setRATE_CREDITCARD_APPROVAL_COUNT("12");
		bankCredit.setRATE_CREDITREPOR_COUNT("11");
		bankCredit.setRATE_LOANOFF_LOANOPEN_RATIO("as");
		bankCredit.setRATE_MARITAL_STATE("as");
		bankCredit.setRATE_EDU_LEVEL("das");
		bankCredit.setRATE_NOACCOUNT_FIRSTEND_BAL("ad");
		bankCredit.setRATE_FIVEYEAR_MAXOVERDUE_COUNT("gh");
		bankCredit.setRATE_REGISTER("gd");
		bankCredit.setRATE_CREDIT_ACCOUNT_COUNT("876");
		bankCredit.setRATE_NORMAL_AVENOTUSEDLIMITRAT("a");
		bankCredit.setRATE_RECENTLY_OPENCARD_LIMIT("0kd");
		bankCredit.setRATE_FIRSTNOACCOUNT_CARDAGE("0ojh");
		bankCredit.setCreator("yanll");
		bankCredit.setUpdater("adfadf");
		bankCredit.setCreated_time(new Date());
		bankCredit.setUpdated_time(new Date());
		bankCredit.setAttributes1("1");
		bankCredit.setAttributes2("asas");
		BankCreditRepository.save(bankCredit);
		BankCredit s=BankCreditRepository.getOptName("1");
		System.out.println(s.toString());
	}
}
