package cn.com.fotic.eimp.utils;

import java.math.BigDecimal;

import cn.com.fotic.eimp.repository.entity.BankCredit;
import cn.com.fotic.eimp.repository.entity.MaritalEducation;
import cn.com.fotic.eimp.repository.entity.Score;
import cn.com.fotic.eimp.repository.entity.SexEnum;
/**
 * 分数求和
 * @author lly
 */
public class SumUtil {

	private final static double rate=2.45;
	private final static double creditScore=974.5;
	
	/**
	 * 统计区间总分
	 * @param bankCredit
	 * @return
	 */
	public static  int countScore(BankCredit bankCredit) {
		int sumScore=0;
		//信用卡审批次数
		String approvalCount=Score.getScore(Score.approvalCount, bankCredit.getRateCreditcardApprovalCont());
		//3个月审批次数
		String threeCount=Score.getScore(Score.threeCount, bankCredit.getRateCreditreporCount());
		//过去贷款结清数与过去贷款开户数之比'
		String loanopenRatio=Score.getScore(Score.loanopenRatio, bankCredit.getRateLoanoffLoanopenRatio());
		//未销户贷记卡最早额度与最近额度之差
		String firstendBal=Score.getScore(Score.firstendBal, bankCredit.getRateNoaccountFirstendBal());
		//五年内最大逾期次数
		String maxoverdueCount=Score.getScore(Score.maxoverdueCount, bankCredit.getRateFiveyearMaxoverdueCount());
		//信贷账户数
		String accountCount=Score.getScore(Score.accountCount, bankCredit.getRateCreditAccountCount());
		//正常贷记卡平均未用额度组占比
		String avenotusedlimitrat=Score.getScore(Score.avenotusedlimitrat, bankCredit.getRateNormalAvenotusedlimitrat());
		//最近开户贷记卡额度		
		String opencardLimit=Score.getScore(Score.opencardLimit, bankCredit.getRateRecentlyOpencardLimit());
		//最早未销户贷记卡卡龄
		String accountCardage=Score.getScore(Score.accountCardage, bankCredit.getRateFirstnoaccountCardage());
		//性别
		int sexScore=SexEnum.getSexScore(bankCredit.getRateRegister());
		//学历婚姻
		int meScore=MaritalEducation.getMaritalEducationScore(bankCredit);
		System.out.println(approvalCount+"&"+threeCount+"&"+loanopenRatio+"&"+firstendBal+"&"+
		maxoverdueCount+"&"+accountCount+"&"+avenotusedlimitrat+"&"+opencardLimit+"&"+accountCardage+"&"+sexScore+"&"+meScore);
		
		sumScore=Integer.valueOf(approvalCount)+Integer.valueOf(threeCount)+Integer.valueOf(loanopenRatio)+Integer.valueOf(firstendBal)+
				Integer.valueOf(maxoverdueCount)+Integer.valueOf(accountCount)+Integer.valueOf(avenotusedlimitrat)
				+Integer.valueOf(opencardLimit)+Integer.valueOf(accountCardage)+sexScore+meScore;
		return sumScore;
	}
	
	/**
	 * 征信算分
	 * @param score
	 * @return
	 * 征信分 =  2.45  × 京东分 —974.5（结果四舍五入）
	 */
	public static String getCreditScore(String score) {
		BigDecimal b1 = new BigDecimal(Double.valueOf(score));
		BigDecimal b2 = new BigDecimal(rate);
		BigDecimal b3=b1.multiply(b2);
		BigDecimal result=b3.subtract(new BigDecimal(creditScore)).setScale(0,BigDecimal.ROUND_HALF_UP);
		return result.toString();
	}
	
	public static void main(String[] args) {
		BankCredit bankCredit=new BankCredit();
		bankCredit.setRateCreditcardApprovalCont("4");
		bankCredit.setRateCreditreporCount("8");
//		bankCredit.setRateLoanoffLoanopenRatio("1.0");
//		bankCredit.setRateNoaccountFirstendBal("0");
//		bankCredit.setRateCreditAccountCount("9");
//		bankCredit.setRateNormalAvenotusedlimitrat("75.0");
//		bankCredit.setRateRecentlyOpencardLimit("80000");
//		bankCredit.setRateFirstnoaccountCardage("80");
//		bankCredit.setRateMaritalState("未婚");
//		bankCredit.setRateEduLevel("大专");
		System.out.println(countScore(bankCredit));
		//System.out.println(getCreditScore("598"));
	}
}
