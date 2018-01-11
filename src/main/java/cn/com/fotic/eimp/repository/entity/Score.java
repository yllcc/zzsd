package cn.com.fotic.eimp.repository.entity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
/**
 * 分数评估
 * @author lly
 */
public class Score{
	//信用卡审批次数
	public static String[][]  approvalCount = {{"[LOW,1)","83"},{"[1,2)","64"},{"[2,4)","26"},{"[4,HIGH)","-9"},{"OTHER","-9"}}; 
	//3个月审批次数
	public static String[][]  threeCount = {{"[LOW,1)","63"},{"[1,2)","51"},{"[2,HIGH)","38"},{"OTHER","38"}}; 
	//过去贷款结清数与过去贷款开户数之比'
	public static String[][] loanopenRatio={{"[LOW,0)","49"},{"[0,0.055)","65"},{"[0.055,0.956)","84"},{"[0.956,HIGH)","37"},{"OTHER","37"}};
	//未销户贷记卡最早额度与最近额度之差
	public static String[][] firstendBal={{"[LOW,-32999)","49"},{"[-32999,-14999)","63"},{"[-14999,-4999)","69"},{"[-4999,2004)","47"},{"[2004,15040)","55"},{"[15040,HIGH)","66"},{"OTHER","47"}};
	//五年内最大逾期次数
	public static String[][] maxoverdueCount={{"[LOW,1)","72"},{"[1,3)","54"},{"[3,HIGH)","28"},{"OTHER","28"}};
	//信贷账户数
	public static String[][] accountCount={{"[LOW,3)","39"},{"[3,4)","45"},{"[4,6)","53"},{"[6,10)","61"},{"[10,HIGH)","72"},{"OTHER","39"}};
	//正常贷记卡平均未用额度组占比
	public static String[][] avenotusedlimitrat={{"[LOW,0.914)","53"},{"[0.914,12.641)","71"},{"[12.641,104.773)","65"},{"[104.773,HIGH)","43"},{"OTHER","43"}};
	//最近开户贷记卡额度		
	public static String[][] opencardLimit={{"[LOW,6100)","50"},{"[6100,15100)","55"},{"[15100,36052)","69"},{"[36052,HIGH)","55"},{"OTHER","50"}};
	//最早未销户贷记卡卡龄		
	public static String[][] accountCardage={{"[LOW,20)","24"},{"[20,38)","47"},{"[38,46)","58"},{"[46,79)","72"},{"[79,HIGH)","96"},{"OTHER","24"}};
		
	//static String[][]  arr1 = {{"[LOW,1)","83"},{"[1,2)","64"},{"[2,4)","26"},{"[4,HIGH)","26"}}; 
	
	public static boolean rangeInDefined(int current, int min, int max){  
	    return Math.max(min, current) == Math.min(current, max);  
	} 

	public static String getScore(String[][] arr, String value) { // 求和子函数
		for (int i = 0; i < arr.length; i++) {
			String[] s = arr[i];
			if (checkRegion(s[0], value)) {
				return s[1];
			}

		}
		return "";// 返回二维数组中个元素的和
	}
	 
	 /**
	  * 判断是否在区间内
	  * @param region
	  * @return
	  */
	 public static boolean checkRegion(String region,String value) {
		 if(StringUtils.isNotBlank(value)) {
			 if(isNumeric(value)) {
				 String open_sign=region.substring(0, 1);
				 String close_sign=region.substring(region.length()-1);
				 String[] num=region.substring(1,region.length()-1).split(",");
				 if(open_sign.equals("[")&&close_sign.equals(")")) {
					 if("LOW".equals(num[0])) {
						 if("0".equals(num[1])&&"0".equals(value)) {
							 return true;
						 }
						 return (Double.valueOf(value) < Double.valueOf(num[1]))?true:false;
					 }
					 if("HIGH".equals(num[1])) {
						 return  Double.valueOf(value) >= Double.valueOf(num[0])?true:false;
					 }
					return  (Double.valueOf(value) >= Double.valueOf(num[0]) && Double.valueOf(value) < Double.valueOf(num[1]))?true:false;
				 } 
			 }else {
				 if("OTHER".equals(region)) {
					 return true;
				 }
				 return false;
			 }
		 }else {
			 if("OTHER".equals(region)) {
				 return true;
			 }
			 return false;
		 }
		return false;
	 }
	 public static void main(String[] args) {
//		 System.out.println(isNumeric("asd"));
//		 System.out.println(isNumeric("10.02"));
		System.out.println(getScore(approvalCount,"ASD"));
//		System.out.println(getScore(approvalCont,"10.02"));
//		 String s="adfadf";
//		 System.out.println(s.substring(1,s.length()-1));
	}
	 
	 public static boolean isNumeric(String str){
         Pattern pattern = Pattern.compile("^[+-]?\\d+(\\.\\d+)?$*");
         Matcher isNum = pattern.matcher(str);
         if( !isNum.matches() ){
             return false;
         }
         return true;
  }

}  