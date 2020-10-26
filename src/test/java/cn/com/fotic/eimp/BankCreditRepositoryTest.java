package cn.com.fotic.eimp;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import cn.com.fotic.eimp.primary.BankCreditRepository;
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
		bankCredit.setSerialNo("sasdasas");
		bankCredit.setCustName("asd");
		bankCredit.setCertType("2");
		bankCredit.setCertNum("12312312");
		bankCredit.setRateCreditcardApprovalCont("12");
		bankCredit.setRateCreditreporCount("11");
		bankCredit.setRateLoanoffLoanopenRatio("as");
		bankCredit.setRateMaritalState("as");
		bankCredit.setRateEduLevel("das");
		bankCredit.setRateNoaccountFirstendBal("ad");
		bankCredit.setRateFiveyearMaxoverdueCount("gh");
		bankCredit.setRateRegister("gd");
		bankCredit.setRateCreditAccountCount("876");
		bankCredit.setRateNormalAvenotusedlimitrat("a");
		bankCredit.setRateRecentlyOpencardLimit("0kd");
		bankCredit.setRateFirstnoaccountCardage("0ojh");
		bankCredit.setAttributes1("1");
		bankCredit.setAttributes2("asas");
		BankCreditRepository.save(bankCredit);
	}
}
