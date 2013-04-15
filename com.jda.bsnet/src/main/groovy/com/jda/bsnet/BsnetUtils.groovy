package com.jda.bsnet
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import sun.misc.BASE64Encoder;

class BsnetUtils {

	static String encrypt(String plaintext)
	{
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA");
			md.update(plaintext.getBytes("UTF-8")); //step 3
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} //step 2
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		byte[] raw = md.digest() //step 4
		String hash = (new BASE64Encoder()).encode(raw); //step 5
		return hash; //step 6
	}

}
