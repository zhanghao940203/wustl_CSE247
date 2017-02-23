package dh;

import dh.utils.MExp;

public class DH {
	
	private final long privKey;
	public  final long base, modulus;
	private final MExp modexp;
	
	public DH(long base, long privKey, long modulus) {
		this.privKey = privKey;
		this.modulus = modulus;
		this.base    = base;
		this.modexp  = new MExp(base, modulus);
	}
	
	/**
	 * Compute the public key for the contained private key.
	 * As shown in lecture this is base to the privKey power mod modulus
	 * @return
	 */
	public long getPubKey() {
		long PubKey = modexp.toThePower(privKey);
		return PubKey; // FIXME
	}
	
	/**
	 * Compute Diffie--Hellman agreement:  raise the other agent's public key
	 *   to this private key power, mod the common modulus.
	 * @param otherPubKey
	 * @return
	 */
	public long getAgreedNum(long otherPubKey) {
		MExp otherexp = new MExp(otherPubKey, modulus);
		long agreeNum = otherexp.toThePower(privKey);
		return agreeNum;  // FIXME
	}
	
}
