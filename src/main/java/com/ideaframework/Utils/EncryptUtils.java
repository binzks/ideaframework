package com.ideaframework.Utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

public class EncryptUtils {

	public static final String KEY_ALGORITHM = "AES";
	public static final String ENCODING = "UTF-8";

	/** 把inputString MD5加密 */
	public static String md5(String inputString) {
		return DigestUtils.md5Hex(inputString);
	}

	/**
	 * AES加密
	 * 
	 * @param txt
	 * @param sKey
	 * @return AES加密 返回base64加密后的字符串
	 * @throws Exception
	 */
	public static String AESEncrypt(String txt, String sKey) throws Exception {
		SecretKeySpec key = new SecretKeySpec(sKey.getBytes(), KEY_ALGORITHM);
		Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);// 创建密码器
		byte[] byteTxt = txt.getBytes(ENCODING);
		cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化
		// AES 加密
		byte[] result = cipher.doFinal(byteTxt);
		// base64 加密
		return new Base64().encodeToString(result);
	}

	/**
	 * AES解密
	 * 
	 * @param txt
	 * @param sKey
	 * @return
	 * @throws Exception
	 */
	public static String AESDecrypt(String txt, String sKey) throws Exception {
		SecretKeySpec key = new SecretKeySpec(sKey.getBytes(), KEY_ALGORITHM);
		Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);// 创建密码器
		cipher.init(Cipher.DECRYPT_MODE, key);// 初始化
		// 使用BASE64对密文进行解码
		byte[] encrypted = Base64.decodeBase64(txt);
		// 解密
		byte[] original = cipher.doFinal(encrypted);
		return new String(original, ENCODING);
	}

	public static String createSignature(String key, String timeStamp, String params) {
		return md5("$" + key + "$" + timeStamp + "$" + params + "$");
	}

}
