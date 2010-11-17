package br.usp.ime.cogroo.logic;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.RSAKeyGenParameterSpec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

import sun.security.rsa.RSAPublicKeyImpl;
import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.Component;
import br.usp.ime.cogroo.model.User;
import br.usp.ime.cogroo.util.CriptoUtils;

@SuppressWarnings("restriction")
@Component
@ApplicationScoped
public class SecurityUtil {
	
	private static final String UTF8 = "UTF-8";
	
	private Map<String, KeyTime> keyCache = Collections.synchronizedMap( new HashMap<String, KeyTime>() );
	
	private static final Logger LOG = Logger
		.getLogger(SecurityUtil.class);
	
	public SecurityUtil() {
	}
	
	/**
	 * Encrypt data using an key encrypted with a private key.
	 * @param privateKey the private key to decrypt the secret key
	 * @param encryptedSecretKey a encrypted secret key
	 * @param data the data to encrypt
	 * @return the encrypted data
	 * @throws InvalidKeyException one of the keys is invalid
	 */
	public byte[] encrypt(PrivateKey privateKey, byte[] encryptedSecretKey, String data) throws InvalidKeyException {
		byte[] encryptedData = null;
		try {
			// Decrypt secret symmetric key with private key
			Cipher rsacf = Cipher.getInstance("RSA");
			rsacf.init(Cipher.DECRYPT_MODE, privateKey);
			byte[] secretKey = rsacf.doFinal(encryptedSecretKey);
		
			encryptedData = encrypt(secretKey, data);
		} catch (Exception e) {
			LOG.error("Exception encrypting data", e);
		}
		
		return encryptedData;
	}
	
	public byte[] encrypt(byte[] secretKey, String data) throws InvalidKeyException {
		byte[] encryptedData = null;
		try {
			// Encrypt data using the secret key
			Cipher aescf = Cipher.getInstance("AES/CBC/PKCS5Padding");
			IvParameterSpec ivspec = new IvParameterSpec(new byte[16]);
			aescf.init(Cipher.ENCRYPT_MODE,
					new SecretKeySpec(secretKey, "AES"), ivspec);
			encryptedData = aescf.doFinal(data.getBytes(UTF8));
		} catch (Exception e) {
			LOG.error("Exception encrypting data", e);
		}
		return encryptedData;
	}

	private byte[] encryptSecretKey(PublicKey pkey, byte[] secretKey) {
		byte[] result = null;
		try {
			Cipher rsacf = Cipher.getInstance("RSA");
			rsacf.init(Cipher.ENCRYPT_MODE, pkey);
			result = rsacf.doFinal(secretKey);
		} catch (Exception e) {
			LOG.error("Error", e);
		} 
		return result;
	}
	
	private byte[] decryptSecretKey(PrivateKey privatekey, byte[] encryptedSecretKey) {
		byte[] result = null;
		try {
			Cipher rsacf = Cipher.getInstance("RSA");
			rsacf.init(Cipher.DECRYPT_MODE, privatekey);
			result = rsacf.doFinal(encryptedSecretKey);
		} catch (Exception e) {
			LOG.error("Error", e);
		} 
		return result;
	}

	public byte[] decrypt(PrivateKey privateKey, byte[] encryptedSecretKey, byte[] encryptedText) {
		byte[] text = null;
		try {
			byte[] secretKey = decryptSecretKey(privateKey, encryptedSecretKey);
			text = decrypt(secretKey, encryptedText);
		} catch (Exception e) {
			LOG.error("Should not happen", e);
		}

		return text;
	}
	
	public byte[] decrypt(byte[] secretKey, byte[] encryptedText) {
		byte[] text = null;
		try {
			Cipher aescf = Cipher.getInstance("AES/CBC/PKCS5Padding");
			IvParameterSpec ivspec = new IvParameterSpec(new byte[16]);
			aescf.init(Cipher.DECRYPT_MODE,
					new SecretKeySpec(secretKey, "AES"), ivspec);
			text = aescf.doFinal(encryptedText);

		} catch (Exception e) {
			LOG.error("Should not happen", e);
		}

		return text;
	}

	public PublicKey loadPublicKey(File fPub) throws IOException,
			ClassNotFoundException {
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fPub));
		PublicKey ret = (PublicKey) ois.readObject();
		ois.close();
		return ret;
	}

	public PrivateKey loadPrivateKey(File fPvk) throws IOException,
			ClassNotFoundException {
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fPvk));
		PrivateKey ret = (PrivateKey) ois.readObject();
		ois.close();
		return ret;
	}

	private static final int RSAKEYSIZE = 1024;

	public KeyPair genKeyPair() {
		KeyPair kpr = null;
		try {
			KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
			kpg.initialize(new RSAKeyGenParameterSpec(RSAKEYSIZE,
					RSAKeyGenParameterSpec.F4));
			kpr = kpg.generateKeyPair();
		} catch (NoSuchAlgorithmException e) {
			LOG.error("Error generating key pair", e);
		} catch (InvalidAlgorithmParameterException e) {
			LOG.error("Error generating key pair", e);
		}
		return kpr;
	}
	
	public byte[] genSecretKey() {
		byte[] key = null;
		try {
			KeyGenerator kg = KeyGenerator.getInstance("AES");
			kg.init(128);
			SecretKey sk = kg.generateKey();
			key = sk.getEncoded();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return key;
	}
	
	public String encode(byte[] key) {
		return Base64.encodeBase64String(key);
	}
	
	public String encodeURLSafe(String data) {
		String ret = null;
		try {
			ret = URLEncoder.encode(data, UTF8);
		} catch (Exception e) {
			LOG.error("Should not happen", e);
		}
		return ret;
	}
	
	public String encodeURLSafe(byte[] key) {
		String encKey = null;
		try {
			String value = encode(key);
			encKey = URLEncoder.encode(value, UTF8);
		} catch (UnsupportedEncodingException e) {
			LOG.error("Should not happen", e);
		}
		return encKey;
	}
	
	public byte[] decode(String encoded) {
		byte[] bytes = null;
		bytes = Base64.decodeBase64(encoded);
		return bytes;
	}
	
	public String decodeString(String encoded) {
		String ret = null;
		try {
			ret = new String(decode(encoded), UTF8);
		} catch (UnsupportedEncodingException e) {
			LOG.error("Error decoding string: " + encoded, e);
		}
		return ret;
	}
	
	public byte[] decodeURLSafe(String encoded) {
		byte[] bytes = null;
		try {
			bytes = Base64.decodeBase64(URLDecoder.decode(encoded, UTF8).getBytes(UTF8));
		} catch (Exception e) {
			LOG.error("Error decoding string: " + encoded, e);
		}
		return bytes;
	}
	
	public String decodeURLSafeString(String encoded) {
		String ret = null;
		try {
			ret = URLDecoder.decode(encoded, UTF8);
		} catch (UnsupportedEncodingException e) {
			LOG.error("Error decoding string: " + encoded, e);
		}
		return ret;
	}
	
	public PublicKey encodedStringToPublicKey(byte[] encKey) throws InvalidKeyException {
		PublicKey key = null;
		key = new RSAPublicKeyImpl(encKey);
		return key;
	}
	
	/**
	 * Generates a random secret key for the user. The key is saved for a while.
	 * @param user the user name
	 * @param pubKey the user public key to encrypt the new key
	 * @return the encrypted key
	 * @throws InvalidKeyException if the received key was invalid
	 */
	public String genSecretKeyForUser(User user, byte[] pubKey) throws InvalidKeyException {
		PublicKey pkey = encodedStringToPublicKey(pubKey);
		byte[] key = genSecretKey();
		LOG.info("Saving keys for user: " + user);
		this.keyCache.put(user.getLogin(), new KeyTime(key));
		byte[] enckey = encryptSecretKey(pkey, key);
		String encKeyStr = encodeURLSafe(enckey);
		return encKeyStr;
	}
	
	/**
	 * Encrypt a string using SHA
	 * @param plaintext the original text
	 * @return resultant hash
	 */
	public synchronized String encrypt(String plaintext)
	  {
	    MessageDigest md = null;
	    try
	    {
	      md = MessageDigest.getInstance("SHA");
	      md.update(plaintext.getBytes(UTF8));
	    }
	    catch(Exception e)
	    {
	      LOG.error("Should not happen!", e);
	    }
	    byte raw[] = md.digest();
	    
	    return Base64.encodeBase64String(raw);
	  }

	public String generateAuthenticationTokenForUser(User user,
			byte[] encryptedPassword) {
		
		String encryptedToken = null; 
		LOG.debug("Will check if user is valid " + user + " encryptedPassword: " + encryptedPassword);
		if(isValid(user, encryptedPassword)) {
			
			String randomStr = Long.toHexString(Double.doubleToLongBits(Math.random()));
			// save the hash of the random string, return it encrypted with the user
			LOG.debug("Generated token " + randomStr);
			LOG.debug("Generated token hash " + encrypt(randomStr));
			if(keyCache.containsKey(user.getLogin())) {
				byte[] secretKey = keyCache.get(user.getLogin()).secretKey;
				try {
					encryptedToken = encodeURLSafe(encrypt(secretKey, randomStr));
					LOG.debug("Generated encryptedToken " + encryptedToken);
				} catch (InvalidKeyException e) {
					// key should be valid
					LOG.error("Unexpected error", e);
				}
				
				// remove the key and check for expired ones
				keyCache.remove(user.getLogin());
				deleteExpiredKeys();
			} else {
				LOG.error("Couldn't get key for " + user);
				LOG.error("Cache size: " + keyCache.size());
				for (String key : keyCache.keySet()) {
					LOG.error("   key: " + key);
				}
			}
		}
		else {
			LOG.error("Unknown user: " + user);
		}
		
		return encryptedToken;
	}
	
	private boolean isValid(User user, byte[] encryptedPassword) {
		LOG.info("check user");
		boolean isValid = false;
		if(this.keyCache.containsKey(user.getLogin())) {
			byte[] secretKey = this.keyCache.get(user.getLogin()).secretKey;
			try {
				LOG.info("Got encrypted secret key for " + user);
				byte[] passwdBytes = decrypt(secretKey, encryptedPassword);
				String passwd = new String(passwdBytes, UTF8);
				String passCripto = CriptoUtils.digestMD5(user.getLogin(), passwd);
				isValid = passCripto.equalsIgnoreCase(user.getPassword());
				
			} catch (UnsupportedEncodingException e) {
				LOG.error("Error", e);
			}
		} else {
			LOG.info("Couldn't get secret key for " + user);
			LOG.error("Cache size: " + keyCache.size());
			for (String key : keyCache.keySet()) {
				LOG.error("   key: " + key);
			}
		}
		LOG.trace("User is valid: " + isValid);
		return isValid;
	}

	private void deleteExpiredKeys() {
		List<String> toDelete = new ArrayList<String>();
		synchronized (keyCache) {
			for (String user : keyCache.keySet()) {
				KeyTime kt = keyCache.get(user);
				if(kt.expired()) {
					LOG.info("Key expired for user: " + user);
					toDelete.add(user);
				}
			}
			for (String user : toDelete) {
				keyCache.remove(user);
			}
		}

	}
	
	private class KeyTime {
		
		public final byte[] secretKey;
		private final Date expiration;
		private static final int EXPIRES_IN_MILLISECONDS = 10 * 60 * 1000;
		
		public KeyTime(byte[] secretKey) {
			this.secretKey = secretKey;
			this.expiration = new Date();
			this.expiration.setTime(this.expiration.getTime() + EXPIRES_IN_MILLISECONDS );
		}
		
		public boolean expired() {
			return new Date().after(expiration);
		}
	}

}