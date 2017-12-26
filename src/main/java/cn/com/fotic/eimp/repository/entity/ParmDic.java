package cn.com.fotic.eimp.repository.entity;

import lombok.Data;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 系统用户表
 */
@Data
@ToString
@Entity
@Table(name = "parm_dic")

public class ParmDic {
	@Id
	private String keyName;

	private String optCode;

	private String optName;

	private String optSeq;

	private String optSts;

}
