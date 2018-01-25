package cn.com.fotic.eimp.repository.entity;

/**
 * 欺诈信息验证返回信息
 * @author lly
 */
public enum IdentifyCodeEnum {
	V_CN_NA("V_CN_NA","查询不到身份证信息"),
	V_CN_NM_UM("V_CN_NM_UM","姓名与身份证号不匹配"),
	V_CN_NM_MA("V_CN_NM_MA","姓名与身份证号匹配"),	
	V_PH_NA("V_PH_NA","查询不到电话号码信息"),
	V_PH_CN_UM("V_PH_CN_UM","电话号码与本人不匹配"),	
	V_PH_CN_MA_UL30D("V_PH_CN_MA_UL30D","电话号码与本人匹配，30天内有使用"),	
	V_PH_CN_MA_UL90D("V_PH_CN_MA_UL90D","电话号码与本人匹配，90天内有使用"),	
	V_PH_CN_MA_UL180D("V_PH_CN_MA_UL180D","电话号码与本人匹配，180天内有使用"),	
	V_PH_CN_MA_UM180D("V_PH_CN_MA_UM180D","电话号码与本人匹配，180天内没有使用"),	
	V_PH_NM_UM("V_PH_NM_UM","电话号码与姓名不匹配"),	
	V_PH_NM_MA_UL30D("V_PH_NM_MA_UL30D","电话号码与姓名匹配，30天内有使用"),	
	V_PH_NM_MA_UL90D("V_PH_NM_MA_UL90D","电话号码与姓名匹配，90天内有使用"),	
	V_PH_NM_MA_UL180D("V_PH_NM_MA_UL180D","电话号码与姓名匹配，180天内有使用"),
	V_PH_NM_MA_UM180D("V_PH_NM_MA_UL180D","电话号码与姓名匹配，180天内没有使用");
	private String code;
	private String info;
	IdentifyCodeEnum(String code,String info) {
		this.code=code;
		this.info=info;
	}
	
	//获取验证信息
	public static String getInfo(String code) {
		for (IdentifyCodeEnum c : IdentifyCodeEnum.values()) {  
	        if (code.equals(c.getCode())) {  
	            return c.getInfo();  
	        }  
	    }
		 return null; 
	}
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}  
}
