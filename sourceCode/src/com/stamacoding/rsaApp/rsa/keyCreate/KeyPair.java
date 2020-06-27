package com.stamacoding.rsaApp.rsa.keyCreate;

import com.stamacoding.rsaApp.log.logger.Logger;

/**
 * Represents a RSA key pair consisting of a public and a fitting private key. Invoking the constructor {@link #KeyPair()} automatically generates the both keys.
 */
public class KeyPair {
	/**
	 * The private key (d, n).
	 */
	private Key privateKey;
	/**
	 * The public key (e, n).
	 */
	private Key publicKey;
	
	/**
	 * Generates a RSA key pair.
	 * @see KeyPair
	 */
	public KeyPair() {
		// Variables
		int p, q, n, phi, e, d;
		
		// 1
		p = KeyUtils.primeNumb(256);
		q = KeyUtils.primeNumb(256, p);
		
		// 2
		n = p * q;
		phi = (p-1)*(q-1);
		
		// 3
		e = KeyUtils.primeNumb(phi);
		
		// 4
		d = KeyUtils.modularInverse(e, phi);
		
		
		Logger.debug(this.getClass().getSimpleName(), "p: " + p);
		Logger.debug(this.getClass().getSimpleName(), "q: " + q);
		Logger.debug(this.getClass().getSimpleName(), "n: " + n);
		Logger.debug(this.getClass().getSimpleName(), "phi(n): " + phi);
		Logger.debug(this.getClass().getSimpleName(), "e: " + e);
		Logger.debug(this.getClass().getSimpleName(), "d: " + d);
		
		Key privateKey = new Key(d, n);
		Key publicKey = new Key(e, n);
		setPrivateKey(privateKey);
		setPublicKey(publicKey);
	}

	public Key getPrivateKey() {
		return privateKey;
	}
	public void setPrivateKey(Key privateKey) {
		this.privateKey = privateKey;
	}
	public Key getPublicKey() {
		return publicKey;
	}
	public void setPublicKey(Key publicKey) {
		this.publicKey = publicKey;
	}
	
	
	@Override
	public String toString() {
		return "private" + getPrivateKey() + " <> public" + getPublicKey();
	}
}