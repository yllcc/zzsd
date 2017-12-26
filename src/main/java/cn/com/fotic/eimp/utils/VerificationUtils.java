package cn.com.fotic.eimp.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * 参数校验
 * @author yangll
 *
 */
@Slf4j
public class VerificationUtils {
	public static final String REGEX_MOBILE = "^((17[0-9])|(14[0-9])|(13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$";
	//户口簿
	public static final String REGEX_register="/^[a-zA-Z0-9]{3,21}$/";
	//护照
	public static final String REGEX_passport="/^(P\\d{7})|(G\\d{8})$/";
	//军官证or士兵证
	public static final String REGEX_officer="/^[a-zA-Z0-9]{7,21}$/";
	//港澳通行证
	public static final String REGEX_HK="/^[HMhm]{1}([0-9]{10}|[0-9]{8})$/";
	//台湾通行证
	public static final String REGEX_TW1="/^[0-9]{8}$/";
	public static final String REGEX_TW2="/^[0-9]{10}$/";
	//外国人居留证
	public static final String REGEX_permit="/^[a-zA-Z]{3}[0-9]{12}$/";
	//警官证
	public static final String REGEX_cop=" /^\\d{8}$/";
	//香港身份证
	public static final String REGEX_HK_CARD="[A-Z][0-9]{6}\\([0-9A]\\)";
	//澳门身份证
	public static final String REGEX_aomen="/^[1|5|7][0-9]{6}\\([0-9Aa]\\)/";
	//台湾身份证
	public static final String REGEX_TW_CARD="[A-Z][0-9]{9}";
	
	
	
	
	/**
	 * 证件类型与号码校验
	 * @param cardType
	 * @param cardNo
	 * @return
	/*0-身份证
	1-户口簿
	2-护照
	3-军官证
	4-士兵证
	5-港澳居民来往内地通行证
	6-台湾同胞来往内地通行证
	7-临时身份证
	8-外国人居留证
	9-警官证
	A-香港身份证
	B-澳门身份证
	C-台湾身份证
	X-其他证件。*/
	public static boolean  cardNocheck(String cardType,String cardNo){
		if(StringUtils.isEmpty(cardType.trim())||StringUtils.isEmpty(cardNo.trim())){
			return false;
		}
		if(cardNo.length()<=30){
			switch (cardType) {
			case "0":
				return IDCardValidate(cardNo);
			case "1":
				return Pattern.matches(REGEX_register, cardNo);
			case "2":
				return Pattern.matches(REGEX_passport, cardNo);
			case "3":
				return Pattern.matches(REGEX_officer, cardNo);
			case "4":
				return Pattern.matches(REGEX_officer, cardNo);
			case "5":
				return Pattern.matches(REGEX_HK, cardNo);
			case "6":
				return Pattern.matches(REGEX_TW1, cardNo)||Pattern.matches(REGEX_TW2, cardNo);
			case "7":
				return IDCardValidate(cardNo);
			case "8":
				return Pattern.matches(REGEX_permit, cardNo);
			case "9":
				return Pattern.matches(REGEX_cop, cardNo);
			case "A":
				return Pattern.matches(REGEX_HK_CARD, cardNo);
			case "B":
				return Pattern.matches(REGEX_aomen, cardNo);
			case "C":
				return Pattern.matches(REGEX_TW_CARD, cardNo);
			case "X":
				return  true;
			default:
				return false;
			}
		}
		return false;
	}
	
	/**
	 * 姓名校验
	 * @param name
	 * @return boolean 
	 */
	public boolean nameValidate(String name){
		if(StringUtils.isNotEmpty(name)&& name.length()<=30){
			return true;
		}
		return false;
	}
	
	/**
	 * 手机号校验
	 * @param phoneNo
	 * @return boolean
	 */
	public boolean phoneCheck(String phoneNo){
		return Pattern.matches(REGEX_MOBILE, phoneNo);
	}
	
	
	/** 
     * 功能：身份证的有效验证 
     */  
    public static boolean IDCardValidate(String IDStr) {  
        IDStr = IDStr.trim().toUpperCase();  
        String[] ValCodeArr = { "1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2" };  
        String[] Wi = { "7", "9", "10", "5", "8", "4", "2", "1", "6", "3", "7", "9", "10", "5", "8", "4", "2" };  
        String Ai = "";  
        // ================ 号码的长度 15位或18位 ================  
        if (IDStr.length() != 15 && IDStr.length() != 18) {  
            //身份证号码长度应该为15位或18位  
            return false;  
        }  
        // ================ 数字 除最后以为都为数字 ================  
        if (IDStr.length() == 18) {  
            Ai = IDStr.substring(0, 17);  
        } else if (IDStr.length() == 15) {  
            Ai = IDStr.substring(0, 6) + "19" + IDStr.substring(6, 15);  
        }  
        if (isNumeric(Ai) == false) {  
            //身份证15位号码都应为数字 ; 18位号码除最后一位外，都应为数字。  
            return false;  
        }  
        // ================ 出生年月是否有效 ================  
        String strYear = Ai.substring(6, 10);// 年份  
        String strMonth = Ai.substring(10, 12);// 月份  
        String strDay = Ai.substring(12, 14);// 月份  
        if (isDataFormat(strYear + "-" + strMonth + "-" + strDay) == false) {  
            //身份证生日无效。  
            return false;  
        }  
        GregorianCalendar gc = new GregorianCalendar();  
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");  
        try {
			if ((gc.get(Calendar.YEAR) - Integer.parseInt(strYear)) > 150  
			        || (gc.getTime().getTime() - s.parse(strYear + "-" + strMonth + "-" + strDay).getTime()) < 0) {  
			    //身份证生日不在有效范围。  
			    return false;  
			}
		} catch (NumberFormatException | ParseException e) {
			log.info("身份证日期范围无效");
			return false;
		}  
        if (Integer.parseInt(strMonth) > 12 || Integer.parseInt(strMonth) == 0) {  
            //身份证月份无效  
            return false;  
        }  
        if (Integer.parseInt(strDay) > 31 || Integer.parseInt(strDay) == 0) {  
            //身份证日期无效  
            return false;  
        }  
        // ================ 地区码时候有效 ================  
        Hashtable<String,String> h = GetAreaCode();  
        if (h.get(Ai.substring(0, 2)) == null) {  
            //身份证地区编码错误。  
            return false;  
        }  
        int TotalmulAiWi = 0;  
        for (int i = 0; i < 17; i++) {  
            TotalmulAiWi = TotalmulAiWi + Integer.parseInt(String.valueOf(Ai.charAt(i))) * Integer.parseInt(Wi[i]);  
        }  
        int modValue = TotalmulAiWi % 11;  
        String strVerifyCode = ValCodeArr[modValue];  
        Ai = Ai + strVerifyCode;  
  
        if (IDStr.length() == 18) {  
            if (Ai.equals(IDStr) == false) {  
                //身份证无效，不是合法的身份证号码  
                return false;  
            }  
        } else {  
            return true;  
        }  
        return true;  
    }
    
    /** 
     * 功能：设置地区编码 
     */  
    private static Hashtable<String,String> GetAreaCode() {  
        Hashtable<String,String> hashtable = new Hashtable<String,String>();  
        hashtable.put("11", "北京");  
        hashtable.put("12", "天津");  
        hashtable.put("13", "河北");  
        hashtable.put("14", "山西");  
        hashtable.put("15", "内蒙古");  
        hashtable.put("21", "辽宁");  
        hashtable.put("22", "吉林");  
        hashtable.put("23", "黑龙江");  
        hashtable.put("31", "上海");  
        hashtable.put("32", "江苏");  
        hashtable.put("33", "浙江");  
        hashtable.put("34", "安徽");  
        hashtable.put("35", "福建");  
        hashtable.put("36", "江西");  
        hashtable.put("37", "山东");  
        hashtable.put("41", "河南");  
        hashtable.put("42", "湖北");  
        hashtable.put("43", "湖南");  
        hashtable.put("44", "广东");  
        hashtable.put("45", "广西");  
        hashtable.put("46", "海南");  
        hashtable.put("50", "重庆");  
        hashtable.put("51", "四川");  
        hashtable.put("52", "贵州");  
        hashtable.put("53", "云南");  
        hashtable.put("54", "西藏");  
        hashtable.put("61", "陕西");  
        hashtable.put("62", "甘肃");  
        hashtable.put("63", "青海");  
        hashtable.put("64", "宁夏");  
        hashtable.put("65", "新疆");  
        hashtable.put("71", "台湾");  
        hashtable.put("81", "香港");  
        hashtable.put("82", "澳门");  
        hashtable.put("91", "国外");  
        return hashtable;  
    }  
  
    /** 
     * 验证日期字符串是否是YYYY-MM-DD格式 
     */  
    public static boolean isDataFormat(String str) {  
        boolean flag = false;  
        String regxStr = "^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]"
        		+ "?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|"
        		+ "(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2]["
        		+ "0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])"
        		+ "|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?"
        		+ "((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s"
        		+ "(((0?[0-9])|([1-2][0-3]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$";  
        Pattern pattern1 = Pattern.compile(regxStr);  
        Matcher isNo = pattern1.matcher(str);  
        if (isNo.matches()) {  
            flag = true;  
        }  
        return flag;  
    }  
    /** 
     * 功能：判断字符串是否为数字 
     */  
    private static boolean isNumeric(String str) {  
        Pattern pattern = Pattern.compile("[0-9]*");  
        Matcher isNum = pattern.matcher(str);  
        if (isNum.matches()) {  
            return true;  
        } else {  
            return false;  
        }  
    }  
}
