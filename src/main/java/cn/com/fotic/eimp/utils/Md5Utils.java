package cn.com.fotic.eimp.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * md5加密
 * 
 * @author yangll
 *
 */
public class Md5Utils {
	private Md5Utils() {
	}
	public static String md5ToBas64(String data) {
		MessageDigest md = null;
		String str = null;
		try {
			md = MessageDigest.getInstance("MD5");
			str = new String(Base64.getEncoder().encode(md.digest(data.getBytes("UTF-8"))), "UTF-8");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return str;
	}

	public static String md5ToHexStr(String data) {
		MessageDigest md = null;
		String str = null;
		try {
			md = MessageDigest.getInstance("MD5");
			// str = Hex.toHexString(md.digest(data.getBytes("UTF-8")));
			str = bytesToHexString(md.digest(data.getBytes("UTF-8")));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return str;
	}

	/**
	 * Convert byte[] to hex string
	 *
	 * @param src
	 *            byte[] data
	 * @return hex string
	 */
	public static String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}

	public static void main(String[] args) throws UnsupportedEncodingException {
		String digest = md5ToBas64("中国阿萨德");
		System.out.println(digest);
		System.out.println(digest.length());
	}
}
