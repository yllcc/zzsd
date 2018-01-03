package cn.com.fotic.eimp.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.com.fotic.eimp.model.HdAntiFraudModel;
import cn.com.fotic.eimp.repository.BankCreditRepository;
import cn.com.fotic.eimp.repository.CreditRepository;
import cn.com.fotic.eimp.repository.entity.BackCredit;
import cn.com.fotic.eimp.repository.entity.BankCredit;
import cn.com.fotic.eimp.utils.Base64Utils;
import cn.com.fotic.eimp.utils.HttpUtil;
import cn.com.fotic.eimp.utils.JaxbUtil;
import cn.com.fotic.eimp.utils.Md5Utils;
import cn.com.fotic.eimp.utils.RSAUtils;
import cn.com.fotic.eimp.utils.ThreeDESUtils;
import cn.com.fotic.eimp.utils.VerificationUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author liugj
 *
 */
@Slf4j
@Service
public class CreditstartService {

	private VerificationUtils vt;

	@Autowired
	private CreditRepository creditRepository;

	@Autowired
	private BankCreditRepository bankCreditRepository;

	/**
	 * 校验证件类型证件号
	 * 
	 * @param custName
	 * @param idNo
	 * @param idType
	 * @return
	 */
	public boolean VerificationService(String custName, String idNo, String idType) {
		boolean name = vt.nameValidate(custName);
		if (name == true) {
			boolean idTypeAndIdNo = vt.cardNocheck(idType, idNo);
			if (idTypeAndIdNo == true) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}

	}

	/**
	 * 调用翰迪接口，反欺诈 1.生成xml
	 * 
	 * @param idNo
	 * @param custName
	 * @return
	 */
	public String HdAntiFraudService(String idNo, String custName) {
		HdAntiFraudModel hd = new HdAntiFraudModel();
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");// 设置日期格式
		String sendTime = df.format(new Date());// new Date()为获取当前系统时间
		hd.setSendTime(sendTime);
		hd.setTransCode("100101");
		hd.setVersion("1.0.0");
		hd.setApplication("GwBiz.Req");
		hd.setCertNo(idNo);
		hd.setChannelId("11009028");
		hd.setChannelOrderId(sendTime);
		hd.setIp("");
		hd.setLinkedMerchantId("2088621466375255");
		hd.setMobile("");
		hd.setName(custName);
		hd.setOpenId("");
		hd.setEmail("");
		hd.setImei("");
		hd.setAddress("");
		hd.setAddress("");
		hd.setBankCard("");
		hd.setProductItemCode("100102,100103");
		hd.setWifiMac("");
		hd.setMac("");
		String xmlReq = JaxbUtil.convertToXml(hd);
		return xmlReq;

	}

	/**
	 * 调用翰迪接口，反欺诈 2.加密请求数据
	 * 
	 * @return
	 * @throws Exception
	 */
	public String checkRiskSystem(String xml) throws Exception {
		String publicKey = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAIkt25i+OsoCyMhjNQ9+298iCSEzQjtStStv+gJK+rQ57DA23ie3tI/7+/845IlrqGF8U42Om6POVEIj/jNHPbkCAwEAAQ==";
		String channelId = "11009028";
		// 测试
		String URL = "http://testds.handydata.cn:8010/dataservice/service.ac";
		// 平台
		// String URL = "http://testds.handydata.cn:8011/dataservice-manage/";
		String mkey = UUID.randomUUID().toString();

		// 加密报文体格式：BASE64(商户号)| BASE64(RSA(报文加密密钥))| BASE64(3DES(报文原文))
		String strKey = RSAUtils.encryptByPublicKey(new String(mkey.getBytes(), "utf-8"), publicKey);

		String strxml = new String(
				Base64Utils.encode(ThreeDESUtils.encrypt(xml.toString().getBytes("utf-8"), mkey.getBytes())));

		String returnXml = new String(Base64Utils.encode(channelId.getBytes("utf-8"))) + "|" + strKey + "|" + strxml;
		log.info(returnXml);

		long start = System.currentTimeMillis();
		log.info("T1=" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS").format(new Date()));
		String reutrnResult = HttpUtil.sendXMLDataByPost(URL, returnXml);
		log.info("T2=" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS").format(new Date()) + ",time length:"
				+ (System.currentTimeMillis() - start));
		log.info("韩迪返回的数据：" + reutrnResult);
		String xmlArr[] = reutrnResult.split("\\|");

		if (xmlArr[0].equals("0")) {
			String err = new String(Base64Utils.decode(xmlArr[2]), "utf-8");
			log.info("韩迪返回的:" + new String(Base64Utils.decode(xmlArr[2]), "utf-8"));
			return err;
		} else {
			byte[] b = ThreeDESUtils.decrypt(Base64Utils.decode(xmlArr[1]), mkey.getBytes());

			String tradeXml = new String(b, "utf-8");
			log.info("333" + tradeXml);
			log.info("444" + new String(Base64Utils.encode(Md5Utils.md5ToHexStr(tradeXml).getBytes("utf-8"))));// BASE64(MD5(报文))
			return tradeXml;
		}
	}

	/**
	 * xml 错误解析
	 * 
	 * @param xml
	 * @return
	 */
	public static String readRespMsg(String xml) {
		if (StringUtils.isEmpty(xml)) {
			return "未知";
		}
		try {
			Element ele = DocumentHelper.parseText(xml).getRootElement();
			return ele.elementText("respDesc");
		} catch (DocumentException e) {
			return "未知2";
		}

	}

	/**
	 * 查询人行视图
	 * 
	 * @param businessNo
	 * @param cust_name
	 * @param cert_type
	 * @param cert_num
	 */
	public void creditOracle(String businessNo, String cust_name, String cert_type, String cert_num) {
		/*
		 * Connection con = null;// 创建一个数据库连接 PreparedStatement pre = null;//
		 * 创建预编译语句对象，一般都是用这个而不用Statement ResultSet result = null;// 创建一个结果集对象
		 */ BankCredit um = new BankCredit();
		try {
			/*
			 * Class.forName("oracle.jdbc.driver.OracleDriver");// 加载Oracle驱动程序
			 * 
			 * String url = "jdbc:oracle:" + "thin:@10.7.101.39:1521/orcl";//
			 * 127.0.0.1是本机地址，XE是精简版Oracle的默认数据库名 String user = "wmxt_hd";// 用户名,系统默认的账户名
			 * String password = "wmxt_hd";// 你安装时选设置的密码 con =
			 * DriverManager.getConnection(url, user, password);// 获取连接 log.info("连接成功！");
			 */

			BackCredit cm = creditRepository.getOptName(cust_name, cert_type, cert_num);
			log.info(cm.getCert_num() + "1111111" + businessNo + cm.getCust_name() + cm.getCert_type());
			if (cm != null || !cm.equals("")) {
				// 查询人行存在评级信息
				um.setSerial_No(businessNo);
				um.setCreated_time(new Date());
				um.setCreator("admin");
				um.setCert_num(cm.getCert_num());
				um.setCert_type(cm.getCert_type());
				um.setCust_name(cm.getCust_name());
				um.setRATE_CREDIT_ACCOUNT_COUNT(cm.getRate_credit_account_count());
				um.setRATE_CREDITCARD_APPROVAL_COUNT(cm.getRate_creditcard_approval_count());
				um.setRATE_CREDITREPOR_COUNT(cm.getRate_creditrepor_count());
				um.setRATE_EDU_LEVEL(cm.getRate_edu_level());
				um.setRATE_FIRSTNOACCOUNT_CARDAGE(cm.getRate_firstnoaccount_cardage());
				um.setRATE_FIVEYEAR_MAXOVERDUE_COUNT(cm.getRate_fiveyear_maxoverdue_count());
				um.setRATE_LOANOFF_LOANOPEN_RATIO(cm.getRate_loanoff_loanopen_ratio());
				um.setRATE_MARITAL_STATE(cm.getRate_marital_state());
				um.setRATE_NOACCOUNT_FIRSTEND_BAL(cm.getRate_noaccount_firstend_bal());
				um.setRATE_NORMAL_AVENOTUSEDLIMITRAT(cm.getRate_normal_avenotusedlimitrat());
				um.setRATE_RECENTLY_OPENCARD_LIMIT(cm.getRate_recently_opencard_limit());
				um.setRATE_REGISTER(cm.getRate_register());
				bankCreditRepository.save(um);
			} else {
				// 不存在评级信息
				// 发送韩迪http请求

			}
		} catch (Exception e) {
			e.printStackTrace();
		} /*
			 * finally { try{ // 逐一将上面的几个对象关闭，因为不关闭的话会影响性能、并且占用资源 // 注意关闭的顺序，最后使用的最先关闭 if
			 * (result != null) result.close(); if (pre != null) pre.close(); if (con !=
			 * null) con.close(); log.info("数据库连接已关闭！"); }catch (Exception e){
			 * e.printStackTrace(); } }
			 */
	}

}
