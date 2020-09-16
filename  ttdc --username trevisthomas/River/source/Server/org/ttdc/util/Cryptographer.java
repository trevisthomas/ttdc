package org.ttdc.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.apache.log4j.Logger;

import com.Ostermiller.util.Base64;

public class Cryptographer {
	private final String SECRET_KEY = "rO0ABXNyABRqYXZhLnNlY3VyaXR5LktleVJlcL35T7OImqVDAgAETAAJYWxnb3JpdGhtdAASTGphdmEvbGFuZy9TdHJpbmc7WwAHZW5jb2RlZHQAAltCTAAGZm9ybWF0cQB+AAFMAAR0eXBldAAbTGphdmEvc2VjdXJpdHkvS2V5UmVwJFR5cGU7eHB0AANERVN1cgACW0Ks8xf4BghU4AIAAHhwAAAACG00v6TZ39rHdAADUkFXfnIAGWphdmEuc2VjdXJpdHkuS2V5UmVwJFR5cGUAAAAAAAAAABIAAHhyAA5qYXZhLmxhbmcuRW51bQAAAAAAAAAAEgAAeHB0AAZTRUNSRVQ=";
	private String KEY;
	private static Logger log = Logger.getLogger(Cryptographer.class);
	
	public Cryptographer(String key) {
		if ((key == null) || key.equals("")) {
			this.KEY = SECRET_KEY;
		}
		else {
			this.KEY = key;
		}
	}
	
	public String encrypt(String toEncrypt) {
		if ((toEncrypt == null) || (toEncrypt.trim().equals("")))
			return null;
		try {
			byte[] bytes = DESEncrypt(toEncrypt.getBytes("UTF-8"), KEY);
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
			byte[] bytes = Base64.decode(str.getBytes("UTF-8"));
			return DESDecrypt(bytes, KEY);
		}
		catch (Exception e) {
			return null;
		}
	}
	
	public static String makePrivateKeyString(String seed) throws Exception {
		// create a binary key from the argument key (seed)
		if ((seed == null) || seed.equals("")) {
			// Default seed.
			seed = "01010101010101010102030405060708090A0B0C0D0E0F101112131415161718";
		}

		SecureRandom sr = new SecureRandom(seed.getBytes("UTF-8"));
		KeyGenerator kg = KeyGenerator.getInstance("DES");
		kg.init(sr);
		SecretKey sk = kg.generateKey();
		String skString = serialize(sk);
		return skString;
	}

	private byte[] DESEncrypt(byte[] toEncrypt, String key) throws Exception {
		SecretKey sk = deserialize(this.KEY);

		// do the encryption with that key
		Cipher cipher = Cipher.getInstance("DES");
		cipher.init(Cipher.ENCRYPT_MODE, sk);
		byte[] encrypted = cipher.doFinal(toEncrypt);
		return encrypted;
	}

	private String DESDecrypt(byte[] toDecrypt, String key) throws Exception {
		SecretKey sk = deserialize(this.KEY);

		// do the decryption with that key
		Cipher cipher = Cipher.getInstance("DES");
		cipher.init(Cipher.DECRYPT_MODE, sk);
		byte[] decrypted = cipher.doFinal(toDecrypt);
		return new String(decrypted);
	}

	public static void main(String args[]) throws Exception {
		String message = "Trevis is what i want to encrypt.";
		String secretkey = Cryptographer.makePrivateKeyString("aa");
		
		Cryptographer c = new Cryptographer(secretkey);

		log.info("Message:" + message);

		String str = c.encrypt(message);
		if (str == null)
			System.out.println("Got the null");
		log.info("LENGTH " + str.length());
		log.info("encrypted:"+str);
		String st = c.decrypt(str);
		log.info("decrypted:" + st);
	}

	private static String serialize(SecretKey secretKey) throws Exception {
		// serialize the object
		String serializedObject = "";
		try {
			ByteArrayOutputStream bo = new ByteArrayOutputStream();
			ObjectOutputStream so = new ObjectOutputStream(bo);
			so.writeObject(secretKey);
			so.flush();
			// serializedObject = bo.toString();
			serializedObject = new String(Base64.encode(bo.toByteArray()));

		} catch (Exception e) {
			System.out.println(e);
		}

		return serializedObject;
	}

	private static SecretKey deserialize(String serializedObject) throws Exception {
		// deserialize the object
		try {
			byte b[] = Base64.decode(serializedObject.getBytes());
			ByteArrayInputStream bi = new ByteArrayInputStream(b);
			ObjectInputStream si = new ObjectInputStream(bi);
			SecretKey obj = (SecretKey) si.readObject();
			return obj;
		} catch (Exception e) {
			System.out.println(e);
			throw e;
		}
	}

}