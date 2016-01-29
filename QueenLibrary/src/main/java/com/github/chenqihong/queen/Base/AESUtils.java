package com.github.chenqihong.queen.Base;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AESUtils {
	
	private static SecretKeySpec mSecretSpec = null;
	private static byte[] mEncodedRawKey = null;
	
	public static byte[] encrypt(byte[] rawkey, byte[] data) throws Exception{
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.ENCRYPT_MODE, mSecretSpec);
		byte[] encrypted = cipher.doFinal(data);
		if(null == mEncodedRawKey){
			mEncodedRawKey = RSAUtils.encodeData(rawkey);
		}

		byte[] encodedData = new byte[encrypted.length + mEncodedRawKey.length];
		for(int i = 0; i < encrypted.length; i++){
			encodedData[i] = encrypted[i];
		}
		for(int i = encrypted.length , j = 0; j < mEncodedRawKey.length; i++, j++){
			encodedData[i] = mEncodedRawKey[j];
		}
		return encodedData;
		
	}

}
