package cn.com.fotic.eimp.repository.entity;

import org.apache.commons.lang3.StringUtils;

public class Score{
	
	static String[][]  arr = {{"[0,1)","83"},{"[2,3)","34"},{"[3,7)","90"}};   
	
	public static boolean rangeInDefined(int current, int min, int max){  
	    return Math.max(min, current) == Math.min(current, max);  
	} 

	public static String getScore(String[][] arr, String value) { // 求和子函数
		for (int i = 0; i < arr.length; i++) {
			String[] s = arr[i];
			if (checkRegion(s[0], value)) {
				System.out.println(s[1]);
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
		 if(StringUtils.isNotBlank(region)) {
			 String open_sign=region.substring(0, 1);
			 String close_sign=region.substring(region.length()-1);
			 String[] num=region.substring(1,region.length()-1).split(",");
			 if(open_sign.equals("[")&&close_sign.equals(")")) {
				return  (Double.valueOf(value) >= Double.valueOf(num[0]) && Double.valueOf(value) < Double.valueOf(num[1]))?true:false;
			 }
		 }
		 return false;
	 }
	 public static void main(String[] args) {
		 getScore(arr,"3.5");
//		 String s="adfadf";
//		 System.out.println(s.substring(1,s.length()-1));
	}
}  