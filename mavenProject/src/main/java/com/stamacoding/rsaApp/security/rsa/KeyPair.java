package com.stamacoding.rsaApp.security.rsa;

import com.stamacoding.rsaApp.logger.L;

/**
 * Represents a RSA key pair consisting of a public and a fitting private key. Invoking the constructor {@link #KeyPair()} automatically generates the both keys.
 */
public class KeyPair {
	
	public static final int N_MAXIMUM = 65536;
	public static final int N_MINIMUM = 1000;
	
	public static final int PQ_MAXIMUM = (int) Math.sqrt(N_MAXIMUM);
	public static final int PQ_MINIMUM = (int) Math.sqrt(N_MINIMUM);
	
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
		generate();
	}
	
	public KeyPair(Key privateKey, Key publicKey) {
		setPrivateKey(privateKey);
		setPublicKey(publicKey);
	}

	public Key getPrivateKey() {
		return privateKey;
	}
	
	private void setPrivateKey(Key privateKey) {
		if(privateKey == null) L.f("RSA", this.getClass(), new IllegalArgumentException("The private key is not allowed to be null!"));
		
		this.privateKey = privateKey;
	}
	
	public Key getPublicKey() {
		return publicKey;
	}
	
	private void setPublicKey(Key publicKey) {
		if(publicKey == null) L.f("RSA", this.getClass(), new IllegalArgumentException("The public key is not allowed to be null!"));
		
		this.publicKey = publicKey;
	}
	
	private void generate() {
		int p, q, n, phi, e, d;
		// Generate p and q

		p = KeyMathUtils.primeNumb(PQ_MINIMUM, PQ_MAXIMUM);
		q = KeyMathUtils.primeNumb(PQ_MINIMUM, PQ_MAXIMUM, p);
		// Calculate n and phi(n)
		n = p * q;
		phi = (p-1)*(q-1);
		
		d = -1;
		do {
			// Calculate e
			e = KeyMathUtils.primeNumb(2, phi);
			
			// Calculate d
			d = KeyMathUtils.modularInverse(e, phi);
		}while(d == -1 || d == e);
		
		L.t("RSA", this.getClass(), "p\t=>\t" + p);
		L.t("RSA", this.getClass(), "q\t=>\t" + q);
		L.t("RSA", this.getClass(), "n\t=>\t" + n + " = " + p + " * " + q);
		L.t("RSA", this.getClass(), "phi\t=>\t" + phi + " = (" + p + " - 1) * (" + q + " - 1) = " + (p-1) + " * " + (q-1));
		L.t("RSA", this.getClass(), "e\t=>\t" + e);
		L.t("RSA", this.getClass(), "d\t=>\t" + d);
		
		Key privateKey = new Key(d, n);
		Key publicKey = new Key(e, n);
		setPrivateKey(privateKey);
		setPublicKey(publicKey);
		
		L.t("RSA", this.getClass(), this.toString());
	}
	
	
	@Override
	public String toString() {
		return "private" + getPrivateKey() + " <> public" + getPublicKey();
	}
}
