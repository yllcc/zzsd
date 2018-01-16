package cn.com.fotic.eimp.utils;

import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;

/**
 * RSA公钥/私钥/签名工具包 
 * @author yangll
 *
 */
public class RSAUtils {
	
	public static final String KEY_ALGORITHM = "RSA";
	public static final String PUBLIC_KEY = "RSAPublicKey";
	public static final String PRIVATE_KEY = "RSAPrivateKey";
	private static final int KEY_SIZE = 512;

	// 私钥解密
	public static byte[] decryptByPrivateKey(byte[] data, byte[] key)
			throws Exception {
		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(key);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
		Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		return cipher.doFinal(data);
	}
	// 私钥解密2
	public static String decryptByPrivateKey(String data, String key)
			throws Exception {
		byte[] dataByte=decryptByPrivateKey(Base64.getDecoder().decode(data),Base64.getDecoder().decode(key));
		return new String(dataByte);
	}
	// 公钥解密
	public static byte[] decryptByPublicKey(byte[] data, byte[] key)
			throws Exception {
		X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(key);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		PublicKey publicKey = keyFactory.generatePublic(x509KeySpec);
		Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
		cipher.init(Cipher.DECRYPT_MODE, publicKey);
		return cipher.doFinal(data);
	}
	// 公钥解密2
	public static String decryptByPublicKey(String data, String key)
			throws Exception {
		byte[] dataByte=decryptByPublicKey(Base64.getDecoder().decode(data),Base64.getDecoder().decode(key));
		return new String(dataByte);
	}
	// 公钥加密
	public static byte[] encryptByPublicKey(byte[] data, byte[] key)
			throws Exception {
		X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(key);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		PublicKey publicKey = keyFactory.generatePublic(x509KeySpec);
		Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		return cipher.doFinal(data);
	}
	// 公钥加密2
	public static String encryptByPublicKey(String data, String key)throws Exception {
		byte[] signByte=encryptByPublicKey(data.getBytes("utf-8"),Base64.getDecoder().decode(key));
		return Base64.getEncoder().encodeToString(signByte);
	}
	// 私钥加密
	public static byte[] encryptByPrivateKey(byte[] data, byte[] key)
			throws Exception {
		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(key);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
		Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
		cipher.init(Cipher.ENCRYPT_MODE, privateKey);
		return cipher.doFinal(data);
	}
	// 私钥加密2
	public static String encryptByPrivateKey(String data, String key)
	throws Exception {
		byte[] signByte=encryptByPrivateKey(data.getBytes(),Base64.getDecoder().decode(key));
		return Base64.getEncoder().encodeToString(signByte);
	}
	
	//私钥验证公钥密文
	public static boolean checkPublicEncrypt(String data,String sign,String pvKey)
	throws Exception{
		return data.equals(decryptByPrivateKey(sign,pvKey));
	}
	public static boolean checkPrivateEncrypt(String data,String sign,String pbKey)
	throws Exception{
		return data.equals(decryptByPublicKey(sign,pbKey));
	}
	//取得私钥
	public static byte[]getPrivateKey(Map<String,Object> keyMap) throws Exception{
		Key key=(Key)keyMap.get(PRIVATE_KEY);
		return key.getEncoded();
	}
	//取得公钥
	public static byte[]getPublicKey(Map<String,Object> keyMap) throws Exception{
		Key key=(Key)keyMap.get(PUBLIC_KEY);
		return key.getEncoded();
	}
	//初始化密
	public static Map<String,Object> initKey() throws Exception{
		KeyPairGenerator keyPairGen =KeyPairGenerator.getInstance(KEY_ALGORITHM);		
		keyPairGen.initialize(KEY_SIZE);		
		KeyPair keyPair=keyPairGen.generateKeyPair();		
		RSAPublicKey publicKey=(RSAPublicKey)keyPair.getPublic();
		RSAPrivateKey privateKey=(RSAPrivateKey)keyPair.getPrivate();		
		Map<String,Object> keyMap =new HashMap<String,Object>(2);
		keyMap.put(PUBLIC_KEY,publicKey);
		keyMap.put(PRIVATE_KEY,privateKey);
		return keyMap;
	}

	/**
   * 获取公钥的模和指数，经过Base64编码后的。
   * @param key
   * @return [模, 指数]
   * @throws InvalidKeySpecException
   * @throws NoSuchAlgorithmException
   */
  public static String[] readModulusAndPublicExponent(String key) throws InvalidKeySpecException, NoSuchAlgorithmException{
    X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(key));
    KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
    RSAPublicKey publicKey = (RSAPublicKey) keyFactory.generatePublic(x509KeySpec);
    String[] res = new String[2];
    res[0] = Base64.getEncoder().encodeToString(publicKey.getModulus().toByteArray());
    res[1] = Base64.getEncoder().encodeToString(publicKey.getPublicExponent().toByteArray());
    //Base64.toBase64String
    return res;
  }	
    
    
	 public static void main(String[] args) {
		 
	    String publicKey = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAJJX7jDIXCyuEx+f0kS8CkrRsxOslH5rEP1+/6z06nh+k5+z9jFMRm/wsurcCRxDCbySq+91pALLEchu0V7j8SECAwEAAQ==";
	    String[] mps;
		try {
			mps = readModulusAndPublicExponent(publicKey);
	    System.out.println("model:"+mps[0]);
	    System.out.println("publicExponent:"+mps[1]);    
	    String data = "11111111111111111111111111";
		System.out.print(Base64.getEncoder().encodeToString(ThreeDESUtils.encrypt(data.getBytes(), data.getBytes())));
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String xml="<?xml version='1.0' encoding='UTF-8' standalone='yes'?>"+
				"<subatm><application>GwBiz.Req</application><version>1.0.0</version>"
				+"<sendTime>20150516101111</sendTime>"
				+"<transCode> 100101</transCode>"
				+"<channelId>11111100011</channelId>"
				+"<channelOrderId>100001120501</channelOrderId>"
				+"<certNo>411521199909098888</ certNo >"
				+"<linkedMerchantId>2088621466375255</linkedMerchantId>"
				+"<openId>268816231939677097046273602</openId>"
				+"<productItemCode>100102,101008,101009</productItemCode>"
				+"<name>张三</name>"
				+"<mobile>1313123121</mobile>"
				+"<email>qeqwewq@126.com</email>"
				+"<bankCard>12345678234567</bankCard>"
				+"<address>黄土高坡</address>"
				+"<ip>127.0.0.1</ip>"
				+"<mac>e2432feef</mac>"
				+"<wifimac>e2432feef</wifimac>"
				+"<imei>e2432feef</imei>"
				+"</subatm>";
	}
}
