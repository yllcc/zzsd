package cn.com.fotic.eimp.utils;
import org.springframework.util.StringUtils;

import cn.com.fotic.eimp.model.JSON_TYPE;

/**
 * 
 * @author liugj
 *
 */
public class JsonTypeUtil {
	public static JSON_TYPE getJsonType(String str){
		if(StringUtils.isEmpty(str)){
			return JSON_TYPE.JSON_TYPE_ERROR;
		}
		
		final char[] strChar=str.substring(0, 1).toCharArray();
		final char firstChar=strChar[0];
		
		if(firstChar=='{'){
			return JSON_TYPE.JSON_TYPE_OBJECT;
		}else if(firstChar=='['){
			return JSON_TYPE.JSON_TYPE_ARRAY;
		}else{
			return JSON_TYPE.JSON_TYPE_ERROR;
		}
	}
}
