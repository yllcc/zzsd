package cn.com.fotic.eimp.model;

import java.util.List;

import lombok.Data;

/**
 * 
 * @author liugj
 *
 */
@Data
public class TencentCreditScoreContentModel{
    private Integer riskScore;//分值
    private List<RiskInfo> riskInfo;//风险类型说明
   
}