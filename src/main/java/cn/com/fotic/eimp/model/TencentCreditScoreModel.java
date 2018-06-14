package cn.com.fotic.eimp.model;

import lombok.Data;


/**
 * 
 * @author liugj
 *腾讯分返回xml对应实体类
 */
@Data
public class TencentCreditScoreModel {

   
    private String resCode;//响应吗
   
    private String resMsg;//响应描述
  
    private TencentCreditScoreContentModel data;//基本信息

    
}

