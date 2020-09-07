package com.chen.logic;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AesUtil {

	private final static String cipher = "1234567890123456";

	public static boolean verifyToken(String verify) throws Exception {
		int server_time = (int)(System.currentTimeMillis()/1000); 
		
		String time = decrypt(verify);
		int client_time = Integer.parseInt(time);
		
		if (server_time - client_time < 10) { //10秒 验证超时
			return true;
		}
		return false;
	}

	// 加密
	public static String encrypt(String plainText) {

		try {
			byte[] raw = cipher.getBytes("UTF-8");
			SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");// "算法/模式/补码方式"
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
			byte[] encrypted = cipher.doFinal(plainText.getBytes("utf-8"));
			return Base64.getEncoder().encodeToString(encrypted);// 此处使用BASE64做转码功能，同时能起到2次加密的作用。
		} catch (Exception e) {
			return null;
		}
	}

	// 解密
	public static String decrypt(String plainText) throws Exception {
		try {
			byte[] raw = cipher.getBytes("utf-8");
			SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, skeySpec);
			byte[] encrypted = Base64.getDecoder().decode(plainText);// 先用base64解密

			byte[] original = cipher.doFinal(encrypted);
			String originalString = new String(original, "utf-8");
			return originalString;

		} catch (Exception ex) {
			return null;
		}
	}
}
