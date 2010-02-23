package org.ttdc.util;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.apache.log4j.Logger;

import com.Ostermiller.util.Base64;

public class Cryptographer {
	private final String DEFAULT_KEY = "01010101010101010102030405060708090A0B0C0D0E0F101112131415161718";
	private String KEY;
	private static Logger log = Logger.getLogger(Cryptographer.class);
	
	public Cryptographer(String key) {

		if ((key == null) || key.equals("")) {
			this.KEY = DEFAULT_KEY;
		}
		else {
			this.KEY = key;
		}
	}
	
	public String encrypt(String toEncrypt) {
		if ((toEncrypt == null) || (toEncrypt.trim().equals("")))
			return null;
		try {
			byte [] bytes = DESEncrypt(toEncrypt.getBytes("UTF-8"), KEY);
			//String base64Encoded = new String(Base64.encodeBytes(bytes));// for store use, so must convert to string
			String base64Encoded = new String(Base64.encode(bytes));
			
			return base64Encoded;
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public String decrypt(String str) {
		if (str == null)
			return null;
		try {
			byte [] bytes = Base64.decode(str.getBytes());
			return DESDecrypt(bytes, KEY);
		}
		catch (Exception e) {
			return null;
		}
	}
	
	private byte[] DESEncrypt(byte[] toEncrypt, String key) throws Exception {
		// create a binary key from the argument key (seed)
		SecureRandom sr = new SecureRandom(key.getBytes());
		KeyGenerator kg = KeyGenerator.getInstance("DES");
		kg.init(sr);
		SecretKey sk = kg.generateKey();

		// do the encryption with that key
		Cipher cipher = Cipher.getInstance("DES");
		cipher.init(Cipher.ENCRYPT_MODE, sk);
		byte[] encrypted = cipher.doFinal(toEncrypt);
		return encrypted;
	}

	private String DESDecrypt(byte[] toDecrypt, String key) throws Exception {
		// create a binary key from the argument key (seed)
		SecureRandom sr = new SecureRandom(key.getBytes());
		KeyGenerator kg = KeyGenerator.getInstance("DES");
		kg.init(sr);
		SecretKey sk = kg.generateKey();

		// do the decryption with that key
		Cipher cipher = Cipher.getInstance("DES");
		cipher.init(Cipher.DECRYPT_MODE, sk);
		byte[] decrypted = cipher.doFinal(toDecrypt);
		return new String(decrypted);
	}

	public static void main(String args[]) {
		
		Cryptographer c = new Cryptographer("aa");
		String str = c.encrypt("Trevis is what i want to encrypt.");
		if (str == null)
			System.out.println("Got the null");
		log.info("LENGTH " + str.length());
		log.info("encrypted:"+str);
		String st = c.decrypt(str);
		log.info("decrypted:" + st);
	}
}