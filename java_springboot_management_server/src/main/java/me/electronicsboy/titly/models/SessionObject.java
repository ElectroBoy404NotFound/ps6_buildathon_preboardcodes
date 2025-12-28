package me.electronicsboy.titly.models;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import me.electronicsboy.titly.exceptions.EncryptionException;

@Entity
@Table(name = "sessionstokens")
public class SessionObject {
	@Id
	private String id;

    @Column(nullable = false)
    private String local_base64Private;

    @Column(nullable = false)
    private String local_base64Public;

    @Column(nullable = false)
    private String device_base64Public;
    
    @Column(nullable = false)
    private String HMACPassword;

    @PrePersist
	public void generateId() {
	    if (this.id == null) {
	        byte[] randomBytes = new byte[16]; // 128 bits
	        try {
				SecureRandom.getInstanceStrong().nextBytes(randomBytes);
			} catch (NoSuchAlgorithmException e) {
				throw new EncryptionException("Secure random error!");
			}
	        this.id = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
	    }
	}
    
    public SessionObject() {
    }
    
	public SessionObject(String local_base64Private, String local_base64Public, String device_base64Public) {
		this.local_base64Private = local_base64Private;
		this.local_base64Public = local_base64Public;
		this.device_base64Public = device_base64Public;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the local_base64Private
	 */
	public String getLocal_base64Private() {
		return local_base64Private;
	}

	/**
	 * @param local_base64Private the local_base64Private to set
	 */
	public void setLocal_base64Private(String local_base64Private) {
		this.local_base64Private = local_base64Private;
	}

	/**
	 * @return the local_base64Public
	 */
	public String getLocal_base64Public() {
		return local_base64Public;
	}

	/**
	 * @param local_base64Public the local_base64Public to set
	 */
	public void setLocal_base64Public(String local_base64Public) {
		this.local_base64Public = local_base64Public;
	}

	/**
	 * @return the device_base64Public
	 */
	public String getDevice_base64Public() {
		return device_base64Public;
	}

	/**
	 * @param device_base64Public the device_base64Public to set
	 */
	public void setDevice_base64Public(String device_base64Public) {
		this.device_base64Public = device_base64Public;
	}

	/**
	 * @return the hMACPassword
	 */
	public String getHMACPassword() {
		return HMACPassword;
	}

	/**
	 * @param hMACPassword the hMACPassword to set
	 */
	public void setHMACPassword(String hMACPassword) {
		HMACPassword = hMACPassword;
	}
}
