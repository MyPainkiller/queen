package com.github.chenqihong.queen.Base;

import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.security.Key;
import java.security.KeyFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

public class RSAUtils {

	/** */
	/**
	 * 加密算法RSA
	 */
	public static final String KEY_ALGORITHM = "RSA";

	/** */
	/**
	 * 签名算法
	 */
	public static final String SIGNATURE_ALGORITHM = "MD5withRSA";

	/** */
	/**
	 * 获取公钥的key
	 */
	private static final String PUBLIC_KEY = "RSAPublicKey";

	/** */
	/**
	 * 获取私钥的key
	 */
	private static final String PRIVATE_KEY = "RSAPrivateKey";

	/** */
	/**
	 * RSA最大加密明文大小
	 */
	private static final int MAX_ENCRYPT_BLOCK = 117;

	/** */
	/**
	 * RSA最大解密密文大小
	 */
	private static final int MAX_DECRYPT_BLOCK = 128;

	private static String sPublicKey = null;
	
	public static byte[] encodeData(byte[] data) throws Exception{
		byte[] encodeData = encryptByPublicKey(data,sPublicKey);
		return encodeData;
	}

	public static void setPublicKey(String publicKey){
		sPublicKey = publicKey;
	}
	
	public static byte[] decodeData(byte[] encoded) throws Exception{
		String privateKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJc" +
				"AgEAAoGBANqjLL7TecJy3GOnyRxZ5mmUHapI+EUBRhojfMfvjbuFriq" +
				"6uXagJU6ugcvA+W+DqbE2euLS5vYXliZK7PzsrCzbGdwMXXLgzPXVz2F" +
				"0amXyvZQxtzsp4YTqdP1Qat0rmFzVIiBKd7pYmoH2YPijR9gZREKu7RNdC" +
				"FFt5J7SqWIPAgMBAAECgYB0+FOaDB3aMAVEs6Fer6Ib3gg48C0TDelpVo8Nk" +
				"FXte0mAxsEue+M5wxbh4W4RgQkqLBF2hIrYIipMdk1vy1cY8oVDBv7KseB97XD" +
				"XFk0FjO+amySfWsm8qoen//A9W20rN0M1rcSDOyYVmTSU++MdKdvhb6Z3NGEROv5" +
				"d2mUn8QJBAP9av5yy6v85+1d9g2yCeF5pv8cCfvLHFd8togQAeTJTapNTy2BRm5pPi" +
				"i++F4wkTy8GAJIUeiZgpobN6sLFgJUCQQDbMKo9vaXig7/R/Ni1COqov4rqfDvVI+30RvG" +
				"i5Bv0ouy0ct47OMKs/l8JbX2KUwAjlJR5sNf7/nCQf2JLxLsTAkB4onWdYMU2GD1OYuRgse6No+" +
				"XO7bXtmfISQmuk+lhHVzMlMNQGx+b76pG85lAXi9vAulQwru3aDZT+YG+E3mtVAkAVd+/EulWMG" +
				"SFpnN1Q5f2t54zgqKhnmOxOl8BFFxiR3n9WsUWJAWc38HY4gyvfp3+E/KGKRcLx4Jr/3Coxh6RXAk" +
				"EAmLauxCIGfK0UpiNA1lodPx7sCthwrKJrmXJKM8jTQjCb/yTdLSd1DkV4PfUMk2QPGsAdZSYsqJK0o" +
				"ltplUzNAA==";
		
		byte[] decodeData = decryptByPrivateKey(encoded, privateKey);
		return decodeData;
	}
	
	public static byte[] encryptByPublicKey(byte[] data, String publicKey)
			throws Exception {
		//Log.i("MyApplication", "src data="+new String(data,"UTF-8"));
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		byte[] bytePublicKey = Base64.decode(publicKey, Base64.DEFAULT);
		X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(
				bytePublicKey);
		Key publicK = keyFactory.generatePublic(x509EncodedKeySpec);

		// 对数据加密
		Cipher cipher =Cipher.getInstance("RSA/ECB/PKCS1Padding"); //Cipher.getInstance(keyFactory.getAlgorithm());
		cipher.init(Cipher.ENCRYPT_MODE, publicK);
		int inputLen = data.length;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int offSet = 0;
		byte[] cache;
		int i = 0;
		// 对数据分段加密
		while (inputLen - offSet > 0) {
			if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
				cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
			} else {
				cache = cipher.doFinal(data, offSet, inputLen - offSet);
			}
			out.write(cache, 0, cache.length);
			i++;
			offSet = i * 117;
		}
		byte[] encryptedData = out.toByteArray();
		out.close();
		return encryptedData;
	}

	public static byte[] decryptByPrivateKey(byte[] encryptedData,
			String privateKey) throws Exception {
		byte[] keyBytes = Base64.decode(privateKey, Base64.DEFAULT);
		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");//Cipher.getInstance(keyFactory.getAlgorithm());
		cipher.init(Cipher.DECRYPT_MODE, privateK);
		int inputLen = encryptedData.length;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int offSet = 0;
		byte[] cache;
		int i = 0;
		// 对数据分段解密
		while (inputLen - offSet > 0) {
			if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
				cache = cipher
						.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
			} else {
				cache = cipher
						.doFinal(encryptedData, offSet, inputLen - offSet);
			}
			out.write(cache, 0, cache.length);
			i++;
			offSet = i * MAX_DECRYPT_BLOCK;
		}
		byte[] decryptedData = out.toByteArray();
		out.close();
		return decryptedData;
	}

}
