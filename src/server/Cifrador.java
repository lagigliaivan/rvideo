/**
 * String cadenaEncriptada = Cifrador.encriptar("clave", "Mi Cadena");
 * String cadenaDesencriptada = Cifrador.desencriptar("clave", cadenaEncriptada); 
 */


package server.src;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import org.apache.log4j.Logger;

public class Cifrador {
	private static byte[] SALT_BYTES = {
	(byte)0xA9, (byte)0x9B, (byte)0xC8, (byte)0x32,
	(byte)0x56, (byte)0x35, (byte)0xE3, (byte)0x03
					};
	
	private static int ITERATION_COUNT = 19;
   	 
    	
	public static String encriptar(String passPhrase, String str) {
		Cipher ecipher = null;
		Cipher dcipher = null;
	    Logger logger = Logger.getLogger("Cifrador");

		try {
			// Crear la key

			KeySpec keySpec = new PBEKeySpec(passPhrase.toCharArray(), 
				SALT_BYTES, ITERATION_COUNT);
			SecretKey key = SecretKeyFactory.getInstance(
				"PBEWithMD5AndDES").generateSecret(keySpec);
			
			ecipher = Cipher.getInstance(key.getAlgorithm());
			dcipher = Cipher.getInstance(key.getAlgorithm());
    
			// Preparar los parametros para los ciphers

			AlgorithmParameterSpec paramSpec = new PBEParameterSpec(
				SALT_BYTES, ITERATION_COUNT);
			
			// Crear los ciphers

			ecipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
			dcipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
		} catch (javax.crypto.NoSuchPaddingException e) {
			logger.error(e.getMessage());
		} catch (java.security.NoSuchAlgorithmException e) {
			logger.error(e.getMessage());
		} catch (java.security.InvalidKeyException e) {
			logger.error(e.getMessage());
		} catch (InvalidKeySpecException e) {
			logger.error(e.getMessage());
		} catch (InvalidAlgorithmParameterException e) {
			logger.error(e.getMessage());
		}
		
		try {
			// Encodear la cadena a bytes usando utf-8

			byte[] utf8 = str.getBytes("UTF8");
    
			// Encriptar

			byte[] enc = ecipher.doFinal(utf8);
    
			// Encodear bytes a base64 para obtener cadena

			return new sun.misc.BASE64Encoder().encode(enc);
		} catch (javax.crypto.BadPaddingException e) {
			logger.error(e.getMessage());
		} catch (IllegalBlockSizeException e) {
			logger.error(e.getMessage());
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage());
		}
		
		return null;
	}
    
	public static String desencriptar(String passPhrase, String str) {
		Cipher ecipher = null;
		Cipher dcipher = null;
		Logger logger = Logger.getLogger("Cifrador");
		try {
			// Crear la key

			KeySpec keySpec = new PBEKeySpec(passPhrase.toCharArray(), 
				SALT_BYTES, ITERATION_COUNT);
			SecretKey key = SecretKeyFactory.getInstance(
				"PBEWithMD5AndDES").generateSecret(keySpec);
			ecipher = Cipher.getInstance(key.getAlgorithm());
			dcipher = Cipher.getInstance(key.getAlgorithm());
    
			// Preparar los parametros para los ciphers

			AlgorithmParameterSpec paramSpec = new PBEParameterSpec(
				SALT_BYTES, ITERATION_COUNT);
    
			// Crear los ciphers

			ecipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
			dcipher.init(Cipher.DECRYPT_MODE, key, paramSpec);    
		} catch (javax.crypto.NoSuchPaddingException e) {
			logger.error(e.getMessage());
		} catch (java.security.NoSuchAlgorithmException e) {
			logger.error(e.getMessage());
		} catch (java.security.InvalidKeyException e) {
			logger.error(e.getMessage());
		} catch (InvalidKeySpecException e) {
			logger.error(e.getMessage());
		} catch (InvalidAlgorithmParameterException e) {
			logger.error(e.getMessage());
		}
		
		try {
			// Decodear base64 y obtener bytes

			byte[] dec = new sun.misc.BASE64Decoder().decodeBuffer(str);
    
			// Desencriptar

			byte[] utf8 = dcipher.doFinal(dec);
    
			// Decodear usando utf-8

			return new String(utf8, "UTF8");
		} catch (javax.crypto.BadPaddingException e) {
			logger.error(e.getMessage());
		} catch (IllegalBlockSizeException e) {
			logger.error(e.getMessage());
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage());
		} catch (java.io.IOException e) {
			logger.error(e.getMessage());
		}

		return null;
	}
}